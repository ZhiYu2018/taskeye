package org.pangu.repository;

import org.pangu.common.vo.Pair;
import org.pangu.db.TaskInfoMapper;
import org.pangu.db.TriggersLogMapper;
import org.pangu.common.dao.TaskInfoDo;
import org.pangu.common.dao.TriggersLogDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TimedTaskRepository {
    private static Logger logger = LoggerFactory.getLogger(TimedTaskRepository.class);
    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private TriggersLogMapper triggersLogMapper;

    public Optional<TaskInfoDo> queryByName(String appId, String name){
        try{
            TaskInfoDo taskInfoDo = taskInfoMapper.queryByName(appId, name);
            if(taskInfoDo == null){
                logger.info("Find none such task {}.{}", appId, name);
            }

            return Optional.ofNullable(taskInfoDo);
        }catch (Throwable t){
            logger.error("Find task {}.{} exceptions:{}", appId, name, t.getMessage());
        }

        return null;
    }

    public int existByName(String appId, String name){
        try{
            Integer count = taskInfoMapper.countByName(appId, name);
            if(count.intValue() == 0){
                logger.info("Find none such task {}.{}", appId, name);
            }
            return count.intValue();
        }catch (Throwable t){
            logger.error("Find task {}.{} exceptions:{}", appId, name, t.getMessage());
        }
        return -1;
    }

    public void addTaskInfo(TaskInfoDo taskVo){
        try{
            Long r = taskInfoMapper.add(taskVo);
            logger.info("Add task {}.{} return:{}", taskVo.getAppId(), taskVo.getJobName(), r);
        }catch (Throwable t){
            logger.error("Add task {}.{} exception:{}", taskVo.getAppId(), taskVo.getJobName(), t.getMessage());
        }
    }

    public void updateTaskInfo(TaskInfoDo taskVo){
        try{
            Integer r = taskInfoMapper.updateTaskInfo(taskVo);
            logger.info("updateTaskInfo {}.{} return:{}", taskVo.getAppId(), taskVo.getJobName(),r);
        }catch (Throwable t){
            logger.error("updateTaskInfo {}.{} exception:{}", taskVo.getAppId(), taskVo.getJobName(), t.getMessage());
        }
    }

    public void updateTaskRunning(Pair<TaskInfoDo, TriggersLogDo> pair){
        try{
            Integer r = taskInfoMapper.updateRunStatus(pair.getFirst());
            if(r.intValue() == 0){
                logger.warn("updateRunStatus: {} {} {} return {}", pair.getFirst().getAppId(), pair.getFirst().getJobName(),
                        pair.getFirst().getJobOptId(), r);
                return ;
            }
        }catch (Throwable t){
            logger.error("updateRunStatus {}.{} exception:{}", pair.getFirst().getAppId(), pair.getFirst().getJobName(),
                    t.getMessage());
            return;
        }

        /**插入流水**/
        try{
            triggersLogMapper.addTriggersLog(pair.getSecond());
        }catch (Throwable t){
            logger.error("addTriggersLog {}.{} exception:{}", pair.getFirst().getAppId(), pair.getFirst().getJobName(),
                    t.getMessage());
        }
    }

    public void updateTaskEnd(Pair<TaskInfoDo, TriggersLogDo> pair){
        try {
            Integer r = taskInfoMapper.updateEndStatus(pair.getFirst());
            if(r.intValue() == 0){
                logger.warn("updateTaskEnd: {} {} {} return {}", pair.getFirst().getAppId(),
                        pair.getFirst().getJobName(), pair.getFirst().getJobOptId(), r);
                return ;
            }
        }catch (Throwable t){
            logger.error("updateEndStatus {}.{} exception:{}", pair.getFirst().getAppId(),
                         pair.getFirst().getJobName(), t.getMessage());
            return;
        }

        try{
            triggersLogMapper.updateTriggersStatus(pair.getSecond());
        }catch (Throwable t){
            logger.error("updateTriggersLog {}.{} exception:{}", pair.getFirst().getAppId(),
                         pair.getFirst().getJobName(), t.getMessage());
        }

    }

}
