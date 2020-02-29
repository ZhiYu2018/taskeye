package org.pangu.common.vo;

public enum RtFlagEnum {
    RT_IDEL(1),
    RT_RUNNING(2),
    RT_END(3),
    RT_FAILED(4),
    RT_TIMEOVER(5);
    private final int value;
    RtFlagEnum(int v){
        value = v;
    }

    public int getValue() {
        return value;
    }
}
