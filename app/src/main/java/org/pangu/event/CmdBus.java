package org.pangu.event;

import org.pangu.common.dao.TriggersLogDo;
import org.pangu.common.vo.Pair;
import org.pangu.common.vo.RtFlagEnum;
import org.pangu.common.dao.TaskInfoDo;
import org.pangu.domain.TimedTask;
import org.pangu.domain.vo.DoResponse;
import org.pangu.dto.PanguRequest;
import org.pangu.dto.TaskInfo;
import org.pangu.dto.TaskStatus;
import org.pangu.repository.TimedTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CmdBus {
    private static Logger logger = LoggerFactory.getLogger(CmdBus.class);
    private final static AtomicInteger CmdBusThreadNum = new AtomicInteger(0);
    private ThreadPoolExecutor taskStatusPool;
    private ThreadPoolExecutor taskConfPool;

    @Autowired
    private TimedTaskRepository repository;

    public CmdBus(){
        logger.info("CmdBus create ......");
        BlockingQueue<Runnable> statusQueue = new LinkedBlockingDeque<>(1000);
        taskStatusPool = new ThreadPoolExecutor(8, 8, 10, TimeUnit.SECONDS, statusQueue,
                    r -> {
                        return new Thread(r, "task.status" + CmdBusThreadNum.incrementAndGet());
                    },
                    new ThreadPoolExecutor.AbortPolicy());

        statusQueue = new LinkedBlockingDeque<>(1000);
        taskConfPool = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, statusQueue,
                    r -> {
                        return new Thread(r, "task.status" + CmdBusThreadNum.incrementAndGet());
                    },
                    new ThreadPoolExecutor.AbortPolicy());
    }

    public void submitTaskStatus(PanguRequest<TaskStatus> taskStatus){
        taskStatusPool.submit(new TaskStatusWork<>(taskStatus, this::TaskStatusConsume));
    }

    public void submitTaskConf(PanguRequest<TaskInfo> taskInfo){
        taskConfPool.submit(new TaskStatusWork<>(taskInfo, this::TaskConfConsume));
    }

    public void TaskStatusConsume(PanguRequest<TaskStatus> request){
        logger.info("Handler:{}.{}", request.getFlowId(), request.getAppId());
        TaskStatus taskStatus = request.getData();
        Optional<TaskInfoDo> optDo = repository.queryByName(taskStatus.getAppId(), taskStatus.getTaskName());
        if((optDo == null) || !optDo.isPresent()){
            logger.info("Handle task {}.{} failed", taskStatus.getAppId(), taskStatus.getTaskName());
            return ;
        }

        TaskInfoDo taskInfoDo = optDo.get();
        if(taskStatus.getOptId().compareTo(taskInfoDo.getJobOptId()) < 0){
            /**更新异常**/
            logger.warn("Task name {}.{} update status error: now optId {} <{}", taskStatus.getAppId(),
                        taskStatus.getTaskName(), taskStatus.getOptId(), taskInfoDo.getJobOptId());
            return ;
        }

        TimedTask timedTask = TimedTask.create();
        DoResponse<Pair<TaskInfoDo, TriggersLogDo>> doResponse = timedTask.updateTaskRtStatus(taskInfoDo, taskStatus);
        if (doResponse.getStatus() != 0) {
            logger.error("Handle {}.{} failed, msg:{}, exception:{}", taskStatus.getAppId(), taskStatus.getTaskName(),
                    doResponse.getMsg(), doResponse.getThrowable());
            return;
        }

        if(taskStatus.getFlag().compareTo(RtFlagEnum.RT_RUNNING.getValue()) != 0){
            logger.info("updateTaskEnd {} {} flag: {}", taskStatus.getAppId(), taskStatus.getTaskName(), taskStatus.getFlag());
            repository.updateTaskEnd(doResponse.getData());
            return ;
        }

        /**新的事件**/
        logger.info("updateTaskRunning {} {} flag: {}", taskStatus.getAppId(), taskStatus.getTaskName(), taskStatus.getFlag());
        repository.updateTaskRunning(doResponse.getData());
    }

    public void TaskConfConsume(PanguRequest<TaskInfo> request){
        logger.info("Handler:{}.{}", request.getFlowId(), request.getAppId());
        TaskInfo taskInfo = request.getData();
        Optional<TaskInfoDo> optDo = repository.queryByName(taskInfo.getAppId(), taskInfo.getTaskName());
        if(optDo == null){
            logger.info("Handle task {}.{} failed", taskInfo.getAppId(), taskInfo.getTaskName());
            return ;
        }

        TimedTask timedTask = TimedTask.create();
        if(!optDo.isPresent()) {
            logger.info("add ...");
            DoResponse<TaskInfoDo> doResponse = timedTask.register(taskInfo);
            if (doResponse.getStatus() != 0) {
                logger.error("Handle {}.{} failed, msg:{}, exception:{}", taskInfo.getAppId(), taskInfo.getTaskName(),
                        doResponse.getMsg(), doResponse.getThrowable());
                return;
            }

            repository.addTaskInfo(doResponse.getData());
            return;
        }

        logger.info("update ...");
        DoResponse<TaskInfoDo> doResponse = timedTask.updateTaskInfo(taskInfo);
        if (doResponse.getStatus() != 0) {
            logger.error("Handle {}.{} failed, msg:{}, exception:{}", taskInfo.getAppId(), taskInfo.getTaskName(),
                    doResponse.getMsg(), doResponse.getThrowable());
            return;
        }

        repository.updateTaskInfo(doResponse.getData());
    }


}
