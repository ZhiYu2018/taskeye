package org.pangu.dto;

public class PanguRequest<T>{
    private String appId;
    private String flowId;
    private T data;

    public PanguRequest() {
    }

    public PanguRequest(String appId, String flowId) {
        this.appId = appId;
        this.flowId = flowId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
