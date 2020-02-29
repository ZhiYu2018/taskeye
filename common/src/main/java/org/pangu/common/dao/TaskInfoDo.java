package org.pangu.common.dao;

public class TaskInfoDo {
    private Long Id;
    private Integer status;
    private String appId;
    private String jobName;
    private String timeExpr;
    private Integer jobFlag;
    private Integer timeDelay;
    private Long timeUsed;
    private Long timeOutTimes;
    private Long trigExecTimes;
    private Long lastExecTimes;
    private Long failedTimes;
    private String nextTime;
    private String jobOptId;
    private String lastNextTime;

    public TaskInfoDo() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getTimeExpr() {
        return timeExpr;
    }

    public void setTimeExpr(String timeExpr) {
        this.timeExpr = timeExpr;
    }

    public Integer getJobFlag() {
        return jobFlag;
    }

    public void setJobFlag(Integer jobFlag) {
        this.jobFlag = jobFlag;
    }

    public Integer getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(Integer timeDelay) {
        this.timeDelay = timeDelay;
    }

    public Long getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(Long timeUsed) {
        this.timeUsed = timeUsed;
    }

    public Long getTimeOutTimes() {
        return timeOutTimes;
    }

    public void setTimeOutTimes(Long timeOutTimes) {
        this.timeOutTimes = timeOutTimes;
    }

    public Long getTrigExecTimes() {
        return trigExecTimes;
    }

    public Long getLastExecTimes() {
        return lastExecTimes;
    }

    public void setLastExecTimes(Long lastExecTimes) {
        this.lastExecTimes = lastExecTimes;
    }

    public void setTrigExecTimes(Long trigExecTimes) {
        this.trigExecTimes = trigExecTimes;
    }

    public Long getFailedTimes() {
        return failedTimes;
    }

    public void setFailedTimes(Long failedTimes) {
        this.failedTimes = failedTimes;
    }

    public String getNextTime() {
        return nextTime;
    }

    public void setNextTime(String nextTime) {
        this.nextTime = nextTime;
    }

    public String getJobOptId() {
        return jobOptId;
    }

    public void setJobOptId(String jobOptId) {
        this.jobOptId = jobOptId;
    }

    public String getLastNextTime() {
        return lastNextTime;
    }

    public void setLastNextTime(String lastNextTime) {
        this.lastNextTime = lastNextTime;
    }
}
