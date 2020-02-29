package org.pangu.common.dao;

public class TriggersLogDo {
    private long Id;
    private int status;
    private int alarm;
    private String appId;
    private String jobName;
    private String jobOptId;
    private String jobMsg;
    private String jobTime;
    private String overTime;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
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

    public String getJobOptId() {
        return jobOptId;
    }

    public void setJobOptId(String jobOptId) {
        this.jobOptId = jobOptId;
    }

    public String getJobMsg() {
        return jobMsg;
    }

    public void setJobMsg(String jobMsg) {
        this.jobMsg = jobMsg;
    }

    public String getJobTime() {
        return jobTime;
    }

    public void setJobTime(String jobTime) {
        this.jobTime = jobTime;
    }

    public String getOverTime() {
        return overTime;
    }

    public void setOverTime(String overTime) {
        this.overTime = overTime;
    }
}
