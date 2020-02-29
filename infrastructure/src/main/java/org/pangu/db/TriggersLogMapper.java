package org.pangu.db;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pangu.common.dao.TriggersLogDo;

import java.util.List;

@Mapper
public interface TriggersLogMapper {
    Long addTriggersLog(@Param("dto") TriggersLogDo dto);
    Integer updateTriggersStatus(@Param("dto") TriggersLogDo dto);
    Integer updateTriggersAlarm(@Param("dto") TriggersLogDo dto);
    List<TriggersLogDo> getOverTimeLog(@Param("lastTime") String lastTime, @Param("skipId") Long skipId);
}
