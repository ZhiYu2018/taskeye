package org.pangu.common.vo;

public enum StatusEnum {
    STATUS_IDLE(1),
    STATUS_ENABLE(2),
    STATUS_DISABLE(3),
    STATUS_STOP(4);
    final int value;
    StatusEnum(int v){
        value = v;
    }

    public int getValue() {
        return value;
    }
}
