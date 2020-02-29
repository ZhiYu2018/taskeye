package org.pangu.event;

import org.pangu.common.dao.TaskInfoDo;
import org.pangu.dto.TaskInfo;

public class Convert {
    public static TaskInfo taskDoToTaskInfo(TaskInfoDo tdo){
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setAppId(tdo.getAppId());
        taskInfo.setStatus(tdo.getStatus());
        taskInfo.setTaskName(tdo.getJobName());
        taskInfo.setTimeExpr(tdo.getTimeExpr());
        taskInfo.setTimeUsed(tdo.getTimeUsed());
        return taskInfo;
    }
}
