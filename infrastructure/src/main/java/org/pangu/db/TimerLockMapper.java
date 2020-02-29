package org.pangu.db;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.pangu.common.dao.TimerLockDo;

@Mapper
public interface TimerLockMapper {
    TimerLockDo queryLock(@Param("appId") String appId, @Param("name") String name);
    Long insertLock(@Param("dto") TimerLockDo dto);
    Integer tryLock(@Param("dto") TimerLockDo dto);
    Integer tryUnlock(@Param("dto") TimerLockDo dto);
}
