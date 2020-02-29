package org.pangu.common.vo;

public enum AlarmEnum {
    ALARM_IDLE(1),
    ALARM_SEND(2),
    ALARM_NOT(3);
    private final int value;
    AlarmEnum(int v){
        value = v;
    }

    public int getValue() {
        return value;
    }
}
