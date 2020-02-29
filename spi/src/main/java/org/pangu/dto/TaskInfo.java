package org.pangu.dto;

public class TaskInfo {
    private Integer status;
    private String taskName;
    private String timeExpr;
    private String appId;
    private String startTime;
    private Long timeUsed;


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTimeExpr() {
        return timeExpr;
    }

    public void setTimeExpr(String timeExpr) {
        this.timeExpr = timeExpr;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Long getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(Long timeUsed) {
        this.timeUsed = timeUsed;
    }

}
