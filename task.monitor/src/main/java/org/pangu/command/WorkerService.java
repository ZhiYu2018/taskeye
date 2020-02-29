package org.pangu.command;

import org.pangu.common.util.Helper;
import org.pangu.common.vo.AlarmEnum;
import org.pangu.common.vo.Pair;
import org.pangu.common.vo.RtFlagEnum;
import org.pangu.common.dao.ScheduleTimeDo;
import org.pangu.common.dao.TaskInfoDo;
import org.pangu.common.dao.TriggersLogDo;
import org.pangu.domain.TimedTask;
import org.pangu.domain.vo.DoResponse;
import org.pangu.outbox.AlarmNotify;
import org.pangu.repository.TimedTaskRepository;
import org.pangu.vo.AlarmEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class WorkerService {
    private static Logger logger = LoggerFactory.getLogger(WorkerService.class);
    private final static String TIME_CRON = "0 /5 * * * ? *";
    private final static long LAST_CRON_TIME_SECOND = 5 * 60L;
    @Autowired
    private TimedTaskRepository timedTaskRepository;
    @Autowired
    private AlarmNotify alarmNotify;
    private String appId;
    private String owner;
    private LinkedBlockingDeque<Pair<TaskInfoDo, Boolean>> taskEventList;
    private LinkedBlockingDeque<Pair<TriggersLogDo, Boolean>> triggersLogDoList;
    private ScheduledThreadPoolExecutor executor;
    private ExecutorService executorService;
    private AtomicInteger threadNum;
    private AtomicLong scheduleTimes;
    public WorkerService(){
        appId = "task.eye.monitor";
        owner = UUID.randomUUID().toString().substring(0, 20);
        logger.info("WorkerService create, owner:{}", owner);
        threadNum = new AtomicInteger(0);
        scheduleTimes = new AtomicLong(0);
        taskEventList = new LinkedBlockingDeque<>();
        triggersLogDoList = new LinkedBlockingDeque<>();
        executor = new ScheduledThreadPoolExecutor(2, r -> { return new Thread(r, String.format("Judge_%02d",threadNum.incrementAndGet()));});
        executor.scheduleWithFixedDelay(()->{ checkTimeOutJob();}, 5, 5, TimeUnit.SECONDS);
        executor.scheduleWithFixedDelay(()->{ checkLongJob();}, 5, 5, TimeUnit.SECONDS);
        executorService = Executors.newFixedThreadPool(4, r -> { return new Thread(r, String.format("Alarm_%02d",threadNum.incrementAndGet()));});
        for (int t = 0; t < 3; t++){
            executorService.execute(()->{ sendTimeOutAlarm();});
        }

        for(int t = 0; t < 1; t++) {
            executorService.execute(() -> { sendOverTimeAlarm(); });
        }
    }

    public void run(){
        while(true){
            try {
                if (executor.awaitTermination(300, TimeUnit.SECONDS)) {
                    break;
                }
            }catch (Throwable t){
                logger.warn("Run get exception:", t);
            }
            logger.info("Schedule times:{}", scheduleTimes.get());
        }
    }

    private void sendOverTimeAlarm(){
        logger.info("sendOverTimeAlarm begin ......");
        while (!executor.isShutdown()){
            try{
                Pair<TriggersLogDo, Boolean> dto = triggersLogDoList.poll(10, TimeUnit.SECONDS);
                if(dto == null){
                    continue;
                }

                if(!dto.getSecond()){
                    continue;
                }

                AlarmEvent event = new AlarmEvent();
                event.setTime(dto.getFirst().getOverTime());
                event.setAppId(dto.getFirst().getAppId());
                event.setJobName(dto.getFirst().getJobName());
                event.setOptId(dto.getFirst().getJobOptId());
                event.setAction("Warn");
                event.setMsg("Long to run !!!");
                alarmNotify.notify(event);
                /**更新告警情况**/
                timedTaskRepository.updateTriggerLogAlarm(dto.getFirst());
            }catch (Throwable t){
                logger.error("sendTimeOutAlarm:",t);
            }
        }
        logger.info("sendOverTimeAlarm over ......");
    }

    private void sendTimeOutAlarm(){
        logger.info("sendTimeOutAlarm begin ......");
        while(!executor.isShutdown()){
            try{
                Pair<TaskInfoDo, Boolean> dto = taskEventList.poll(10, TimeUnit.SECONDS);
                if(dto == null){
                    continue;
                }

                if(dto.getSecond()) {
                    AlarmEvent event = createAlarmEvent(dto.getFirst(), "Fault", "The task has not yet been executed!!!");
                    alarmNotify.notify(event);
                }
                /**更新告警情况**/
                timedTaskRepository.updateJobTimeOut(dto.getFirst());
            }catch (Throwable t){
                logger.error("sendTimeOutAlarm:",t);
            }
        }
        logger.info("sendTimeOutAlarm over ......");
    }

    private void checkTimeOutJob(){
        final String jobName = "checkTimeOutJob";
        if(timedTaskRepository.tryLock(appId, jobName, owner) == false){
            return;
        }
        scheduleTimes.incrementAndGet();
        try {
            Optional<ScheduleTimeDo> optDto = timedTaskRepository.getScheduleTimeByName(appId, jobName);
            if(optDto == null){
                return ;
            }

            ScheduleTimeDo scheTimeDo = new ScheduleTimeDo();
            scheTimeDo.setAppId(appId);
            scheTimeDo.setJobName(jobName);
            String curNow = Helper.dateToString(new Date());
            String cronNow = getLastCron();

            if(optDto.isPresent() == false){
                scheTimeDo.setLastTime(cronNow);
                timedTaskRepository.addSchedule(scheTimeDo);
            }else{
                scheTimeDo.setLastTime(optDto.get().getLastTime());
            }

            Long skipId = 0L;
            while(true){
                List<TaskInfoDo> doList = timedTaskRepository.getTimeOutJob(scheTimeDo.getLastTime(), skipId);
                if(doList.isEmpty()){
                    break;
                }

                skipId = doList.get(doList.size() - 1).getId();
                for(TaskInfoDo taskDo :doList){
                    TimedTask timedTask = TimedTask.create();
                    DoResponse<Pair<TaskInfoDo, Boolean>> response = timedTask.judgeTimeOut(taskDo, curNow);
                    if(response.getStatus() != 0){
                        continue;
                    }
                    taskEventList.offer(response.getData());
                }
            }

            if(cronNow.compareTo(scheTimeDo.getLastTime()) > 0) {
                logger.info("AppId {} jobName: {} update last time to {} from {}", scheTimeDo.getAppId(),
                            scheTimeDo.getJobName(), cronNow, scheTimeDo.getLastTime());
                scheTimeDo.setLastTime(cronNow);
                timedTaskRepository.updateScheduleTime(scheTimeDo);
            }
        }catch (Throwable t){
            logger.warn("checkTimeOutJob exceptions:", t);
        }finally {
            timedTaskRepository.tryUnLock(appId, jobName, owner);
        }

    }

    private void checkLongJob(){
        final String jobName = "checkLongJob";
        if(timedTaskRepository.tryLock(appId, jobName, owner) == false){
            return;
        }
        scheduleTimes.incrementAndGet();

        try {
            Optional<ScheduleTimeDo> optDto = timedTaskRepository.getScheduleTimeByName(appId, jobName);
            if(optDto == null){
                return ;
            }

            ScheduleTimeDo scheTimeDo = new ScheduleTimeDo();
            scheTimeDo.setAppId(appId);
            scheTimeDo.setJobName(jobName);
            String curNow = Helper.dateToString(new Date());

            String cronNow = getLastCron();
            if(optDto.isPresent() == false){
                scheTimeDo.setLastTime(cronNow);
                timedTaskRepository.addSchedule(scheTimeDo);
            }else{
                scheTimeDo.setLastTime(optDto.get().getLastTime());
            }

            Long skipId = 0L;
            while (true){
                List<TriggersLogDo> logDoList = timedTaskRepository.getOverTimeLog(scheTimeDo.getLastTime(), skipId);
                if(logDoList.isEmpty()){
                    break;
                }

                skipId = logDoList.get(logDoList.size() - 1).getId();
                for(TriggersLogDo logDo:logDoList){
                    TimedTask timedTask = TimedTask.create();
                    DoResponse<Pair<TriggersLogDo, Boolean>> response = timedTask.judgeLongTask(logDo, curNow);
                    if(response.getStatus() != 0){
                        continue;
                    }

                    triggersLogDoList.offer(response.getData());
                }
            }
            if(cronNow.compareTo(scheTimeDo.getLastTime()) > 0) {
                logger.info("AppId {} jobName: {} update last time to {} from {}", scheTimeDo.getAppId(),
                        scheTimeDo.getJobName(), cronNow, scheTimeDo.getLastTime());
                scheTimeDo.setLastTime(cronNow);
                timedTaskRepository.updateScheduleTime(scheTimeDo);
            }
        }catch (Throwable t){
            logger.warn("checkLongJob exceptions:", t);
        }finally {
            timedTaskRepository.tryUnLock(appId, jobName, owner);
        }
    }

    private static String getLastCron(){
        Date lastCronDate = Helper.getNextDate(TIME_CRON, Helper.getTimeBefor(new Date(), LAST_CRON_TIME_SECOND));
        return Helper.dateToString(lastCronDate);
    }

    private static AlarmEvent createAlarmEvent(TaskInfoDo taskDo, String action, String msg){
        AlarmEvent alarmEvent = new AlarmEvent();
        alarmEvent.setAction(action);
        alarmEvent.setAppId(taskDo.getAppId());
        alarmEvent.setJobName(taskDo.getJobName());
        alarmEvent.setMsg(msg);
        alarmEvent.setTime(taskDo.getLastNextTime());
        return alarmEvent;
    }

}
