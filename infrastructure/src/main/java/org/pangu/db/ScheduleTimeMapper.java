package org.pangu.db;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pangu.common.dao.ScheduleTimeDo;


@Mapper
public interface ScheduleTimeMapper {
    Long addSchedule(@Param("dto") ScheduleTimeDo dto);
    ScheduleTimeDo getByName(@Param("appId") String appId, @Param("name") String name);
    Integer updateTime(@Param("dto") ScheduleTimeDo dto);
}
