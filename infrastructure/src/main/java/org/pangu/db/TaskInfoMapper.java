package org.pangu.db;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pangu.common.dao.TaskInfoDo;

import java.util.List;

@Mapper
public interface TaskInfoMapper {
    Long add(@Param("dto") TaskInfoDo dto);
    Integer updateTaskInfo(@Param("dto") TaskInfoDo dto);
    Integer updateRunStatus(@Param("dto") TaskInfoDo dto);
    Integer updateEndStatus(@Param("dto") TaskInfoDo dto);
    Integer updateTimeOutStatus(@Param("dto") TaskInfoDo dto);
    TaskInfoDo queryByName(@Param("appId") String appId, @Param("name") String name);
    Integer countByName(@Param("appId") String appId, @Param("name") String name);
    List<TaskInfoDo> getTimeOutTask(@Param("lastTime") String lastTime, @Param("skipId") Long skipId);
}
