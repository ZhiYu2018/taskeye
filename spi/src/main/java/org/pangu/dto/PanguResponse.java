package org.pangu.dto;

public class PanguResponse <T>{
    private String flowId;
    private String status;
    private String msg;
    private T data;

    public PanguResponse() {
    }

    public PanguResponse(String flowId, String status, String msg) {
        this.flowId = flowId;
        this.status = status;
        this.msg = msg;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
