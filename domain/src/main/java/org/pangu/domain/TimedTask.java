package org.pangu.domain;

import org.pangu.common.dao.TaskInfoDo;
import org.pangu.common.dao.TriggersLogDo;
import org.pangu.common.util.Helper;
import org.pangu.common.vo.*;
import org.pangu.domain.vo.*;
import org.pangu.dto.TaskInfo;
import org.pangu.dto.TaskStatus;

import java.util.Date;

public class TimedTask {
    private final static int DEF_TIME_DELAY = 10;
    private final static long DEF_TIME_USED = 20L;
    public static TimedTask create(){
        return new TimedTask();
    }

    public DoResponse<TaskInfoDo> register(TaskInfo taskInfo){
        TaskInfoDo taskVo = new TaskInfoDo();
        taskVo.setJobName(taskInfo.getTaskName());
        taskVo.setTimeOutTimes(0L);
        taskVo.setFailedTimes(0L);
        taskVo.setTimeExpr(taskInfo.getTimeExpr());
        taskVo.setAppId(taskInfo.getAppId());
        taskVo.setJobFlag(RtFlagEnum.RT_IDEL.getValue());
        taskVo.setStatus(StatusEnum.STATUS_ENABLE.getValue());
        taskVo.setTrigExecTimes(0L);
        taskVo.setLastExecTimes(0L);
        taskVo.setJobOptId("1900-01-01 00:00:00");
        if(taskInfo.getTimeUsed() == null){
            taskVo.setTimeUsed(0L);
        }else {
            taskVo.setTimeUsed(taskInfo.getTimeUsed());
        }
        /**默认10**/
        taskVo.setTimeDelay(DEF_TIME_DELAY);
        DoResponse<TaskInfoDo> response = new DoResponse<>();
        try{
            Date nextDate = Helper.getNextDate(taskInfo.getTimeExpr(), null);
            taskVo.setNextTime(Helper.dateToString(nextDate));
            response.setStatus(0);
            response.setData(taskVo);
            response.setMsg("Success");
            return response;
        }catch (Throwable t){
            response.setStatus(-1);
            response.setMsg("Parse cronExpression " + taskInfo.getTimeExpr() + " Exceptions: ");
            response.setThrowable(t);
            return response;
        }
    }

    public DoResponse<Pair<TaskInfoDo, TriggersLogDo>> updateTaskRtStatus(TaskInfoDo tskInfo, TaskStatus taskStatus){
        /**参数判断前面已经判断**/
        DoResponse<Pair<TaskInfoDo, TriggersLogDo>> response = new DoResponse<>();
        if(taskStatus.getAppId() == null || taskStatus.getTaskName() == null
                || taskStatus.getOptId() == null){
            response.setStatus(-1);
            response.setMsg("Args error");
            return response;
        }

        if(taskStatus.getFlag().intValue() == RtFlagEnum.RT_RUNNING.getValue()){
            /**开始执行：插入一条运行日志和更新运行状态**/
            /**标记为运行状态**/
            tskInfo.setJobFlag(RtFlagEnum.RT_RUNNING.getValue());
            tskInfo.setTrigExecTimes(1L);
            tskInfo.setJobOptId(taskStatus.getOptId());
            /**产生log**/
            TriggersLogDo logVo = new TriggersLogDo();
            logVo.setJobTime(tskInfo.getNextTime());
            logVo.setAppId(taskStatus.getAppId());
            logVo.setJobName(taskStatus.getTaskName());
            logVo.setJobOptId(taskStatus.getOptId());
            logVo.setJobMsg("Running");
            logVo.setStatus(RtFlagEnum.RT_RUNNING.getValue());
            /**更新预计运行时长**/
            if(tskInfo.getTimeUsed().longValue() != 0L) {
                logVo.setAlarm(AlarmEnum.ALARM_IDLE.getValue());
                logVo.setOverTime(Helper.getTimeAfterStr(null, tskInfo.getTimeUsed()));
            }else{
                logVo.setOverTime(Helper.getTimeAfterStr(null, DEF_TIME_USED));
                logVo.setAlarm(AlarmEnum.ALARM_NOT.getValue());
            }

            response.setStatus(0);
            response.setData(new Pair<>(tskInfo, logVo));
            return response;
        }

        /**其它状态当作结束**/
        /**标记为空闲状态状态**/
        tskInfo.setJobFlag(RtFlagEnum.RT_IDEL.getValue());
        tskInfo.setJobOptId(taskStatus.getOptId());
        /**更新操作执行流水**/
        TriggersLogDo logVo = new TriggersLogDo();
        logVo.setAppId(taskStatus.getAppId());
        logVo.setJobName(taskStatus.getTaskName());
        logVo.setJobOptId(taskStatus.getOptId());
        if(taskStatus.getMsg() != null){
            logVo.setJobMsg(taskStatus.getMsg());
        }else {
            logVo.setJobMsg("Stopped");
        }

        if(taskStatus.getErrorCode() != null && taskStatus.getErrorCode() !=0){
            logVo.setStatus(RtFlagEnum.RT_FAILED.getValue());
            tskInfo.setFailedTimes(1L);
        }else {
            logVo.setStatus(RtFlagEnum.RT_END.getValue());
            tskInfo.setFailedTimes(0L);
        }

        response.setStatus(0);
        response.setData(new Pair<>(tskInfo, logVo));
        return response;
    }

    public DoResponse<TaskInfoDo> updateTaskInfo(TaskInfo taskInfo){
        TaskInfoDo taskDo = new TaskInfoDo();
        DoResponse<TaskInfoDo> response = new DoResponse<>();
        taskDo.setAppId(taskInfo.getAppId());
        taskDo.setJobName(taskInfo.getTaskName());

        if(taskInfo.getStatus() == null){
            taskDo.setStatus(StatusEnum.STATUS_ENABLE.getValue());
        }else{
            taskDo.setStatus(taskInfo.getStatus());
        }

        if((taskInfo.getTimeUsed() != null) && taskInfo.getTimeUsed() > 0){
            taskDo.setTimeUsed(taskInfo.getTimeUsed());
        }

        if((taskInfo.getStatus() != null) && (taskInfo.getStatus() >= StatusEnum.STATUS_IDLE.getValue())
           && (taskInfo.getStatus() <= StatusEnum.STATUS_STOP.getValue())){
            taskDo.setStatus(taskInfo.getStatus().intValue());
        }

        if(taskInfo.getTimeExpr() != null){
            taskDo.setTimeExpr(taskInfo.getTimeExpr());
            try{
                Date nextDate = Helper.getNextDate(taskInfo.getTimeExpr(), null);
                taskDo.setNextTime(Helper.dateToString(nextDate));
            }catch (Throwable t){
                response.setStatus(-1);
                response.setMsg("Parse cronExpression " + taskInfo.getTimeExpr() + " Exceptions: ");
                response.setThrowable(t);
                return response;
            }
        }

        response.setStatus(0);
        response.setData(taskDo);
        response.setMsg("Success");
        return response;
    }

    public DoResponse<Pair<TaskInfoDo, Boolean>> judgeTimeOut(TaskInfoDo dto, String now){
        DoResponse<Pair<TaskInfoDo, Boolean>> response = new DoResponse<>();
        boolean timeOver;
        Date nextDate = Helper.strToDate(dto.getNextTime());
        if(nextDate == null){
            timeOver = (dto.getNextTime().compareTo(now) < 0);
        }else{
            String nextDelay = Helper.getTimeAfterStr(nextDate, dto.getTimeDelay().longValue());
            timeOver = (nextDelay.compareTo(now) < 0);
        }

        if(timeOver == false){
            response.setStatus(-1);
            return response;
        }

        boolean timeOut = (dto.getLastExecTimes().compareTo(dto.getTrigExecTimes()) == 0);
        if(timeOut){
            dto.setTimeOutTimes(1L);
            dto.setJobFlag(RtFlagEnum.RT_IDEL.getValue());
        }else{
            dto.setTimeOutTimes(0L);
        }
        dto.setLastExecTimes(dto.getTrigExecTimes());
        dto.setLastNextTime(dto.getNextTime());

        Date next = Helper.strToDate(dto.getNextTime());
        dto.setNextTime(Helper.dateToString(Helper.getNextTrigDate(dto.getTimeExpr(), next)));
        Pair<TaskInfoDo, Boolean> pair = new Pair<>(dto, Boolean.valueOf(timeOut));
        response.setStatus(0);
        response.setData(pair);
        return response;
    }

    public DoResponse<Pair<TriggersLogDo, Boolean>> judgeLongTask(TriggersLogDo logDo, String now){
        DoResponse<Pair<TriggersLogDo, Boolean>> response = new DoResponse<>();
        /**当前已经处理了 或者 目前状态不是运行中**/
        if(logDo.getAlarm() == AlarmEnum.ALARM_SEND.getValue() || (logDo.getStatus() != RtFlagEnum.RT_RUNNING.getValue())){
            response.setStatus(-1);
            return response;
        }

        /**时间未到**/
        if((logDo.getOverTime().compareTo(now) >= 0)){
            response.setStatus(-1);
            return response;
        }
        /**时间已经到，并且当前状态是运行中**/
        Pair<TriggersLogDo, Boolean> pair;
        if(logDo.getAlarm() == AlarmEnum.ALARM_IDLE.getValue()) {
            logDo.setAlarm(AlarmEnum.ALARM_SEND.getValue());
            logDo.setJobMsg("Long task");
            logDo.setStatus(RtFlagEnum.RT_TIMEOVER.getValue());
            pair = new Pair<>(logDo, Boolean.TRUE);
        }else{
            pair = new Pair<>(logDo, Boolean.FALSE);
        }

        response.setStatus(0);
        response.setData(pair);
        return response;
    }



}
