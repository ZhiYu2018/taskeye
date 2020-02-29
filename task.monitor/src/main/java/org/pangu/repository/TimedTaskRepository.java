package org.pangu.repository;

import org.pangu.common.vo.LockEnum;
import org.pangu.db.ScheduleTimeMapper;
import org.pangu.db.TaskInfoMapper;
import org.pangu.db.TimerLockMapper;
import org.pangu.db.TriggersLogMapper;
import org.pangu.common.dao.ScheduleTimeDo;
import org.pangu.common.dao.TaskInfoDo;
import org.pangu.common.dao.TimerLockDo;
import org.pangu.common.dao.TriggersLogDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TimedTaskRepository {
    private static Logger logger = LoggerFactory.getLogger(TimedTaskRepository.class);
    @Autowired
    private TimerLockMapper timerLockMapper;
    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private TriggersLogMapper triggersLogMapper;
    @Autowired
    private ScheduleTimeMapper scheduleTimeMapper;

    public Optional<ScheduleTimeDo> getScheduleTimeByName(String appId, String name){
        try{
            ScheduleTimeDo scheduleTimeDo = scheduleTimeMapper.getByName(appId, name);
            return Optional.ofNullable(scheduleTimeDo);
        }catch (Throwable t){
            logger.error("getScheduleTimeByName {} {} time,exception:{}", appId, name, t.getMessage());
            return null;
        }
    }

    public void addSchedule(ScheduleTimeDo dto){
        try{
            Long r = scheduleTimeMapper.addSchedule(dto);
            logger.info("addSchedule:{}", r);
        }catch (Throwable t){
            logger.error("addSchedule {} {} time,exception:{}", dto.getAppId(), dto.getJobName(), t.getMessage());
        }
    }

    public void updateScheduleTime(ScheduleTimeDo dto){
        try{
            Integer r = scheduleTimeMapper.updateTime(dto);
            if(r.intValue() == 0){
                logger.warn("Update time {} {} {}, return r:{}", dto.getAppId(), dto.getJobName(),
                            dto.getLastTime(), r);
            }
        }catch (Throwable t){
            logger.error("updateScheduleTime {} {} time,exception:{}", dto.getAppId(), dto.getJobName(), t.getMessage());
        }
    }

    public boolean tryLock(String appId, String name, String owner){
        try {
            TimerLockDo timerLockDo = new TimerLockDo();
            timerLockDo.setAppId(appId);
            timerLockDo.setName(name);
            timerLockDo.setOwner(owner);

            TimerLockDo oldLockDo = timerLockMapper.queryLock(appId, name);
            if (oldLockDo == null) {
                try {
                    timerLockDo.setStatus(Integer.valueOf(LockEnum.FLAG_LOCK.getFlag()));
                    timerLockMapper.insertLock(timerLockDo);
                } catch (Throwable t) {
                    logger.warn("Try lock {}.{} use insert exceptions:", appId, name, t);
                    return false;
                }
                return true;
            }

            Integer r = timerLockMapper.tryLock(timerLockDo);
            return (r.intValue() != 0);
        }catch (Throwable t){
            logger.error("Lock {} {} {} exception:{}", appId, name, owner, t.getMessage());
            return false;
        }
    }

    public boolean tryUnLock(String appId, String name, String owner){
        TimerLockDo timerLockDo = new TimerLockDo();
        timerLockDo.setAppId(appId);
        timerLockDo.setName(name);
        timerLockDo.setOwner(owner);
        try{
            Integer r = timerLockMapper.tryUnlock(timerLockDo);
            if(r == 0){
                logger.warn("Unlock failed:{}:{}:{}, failed:{}", appId, name, owner, r);
            }
            return (r.intValue() != 0);
        }catch (Throwable t){
            logger.warn("Try unlock {}.{} use insert exceptions:", appId, name, t);
            return false;
        }
    }

    public List<TaskInfoDo> getTimeOutJob(String lastTime, Long skipId){
        try{
            return taskInfoMapper.getTimeOutTask(lastTime, skipId);
        }catch (Throwable t){
            logger.error("Get time after {} skipId {}, exception:{}", lastTime, skipId, t.getMessage());
            return new ArrayList<>();
        }
    }

    public List<TriggersLogDo> getOverTimeLog(String lastTime, Long skipId){
        try{
            return triggersLogMapper.getOverTimeLog(lastTime, skipId);
        }catch(Throwable t){
            logger.error("getOverTimeLog {} skipId {}, exception:{}", lastTime, skipId, t.getMessage());
            return new ArrayList<>();
        }
    }

    public void updateJobTimeOut(TaskInfoDo tDto){
        try{
            Integer r = taskInfoMapper.updateTimeOutStatus(tDto);
            if(r.intValue() == 0){
                logger.warn("updateJobTimeOut {}.{} return r:{}", tDto.getAppId(), tDto.getJobName(), r);
            }
        }catch (Throwable t){
            logger.error("updateJobTimeOut {}.{}, exception:{}", tDto.getAppId(), tDto.getJobName(),
                        t.getMessage());
        }
    }

    public void updateTriggerLogAlarm(TriggersLogDo tDto){
        try{
            Integer r = triggersLogMapper.updateTriggersAlarm(tDto);
            if(r.intValue() == 0){
                logger.warn("updateTriggerLogAlarm {}.{} return r:{}", tDto.getAppId(), tDto.getJobName(), r);
            }
        }catch (Throwable t){
            logger.error("updateTriggerLogAlarm {}.{}, exception:{}", tDto.getAppId(), tDto.getJobName(),
                    t.getMessage());
        }
    }
}
