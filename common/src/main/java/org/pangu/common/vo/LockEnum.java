package org.pangu.common.vo;

public enum LockEnum {
    FLAG_UNLOCK(1),
    FLAG_LOCK(2);
    private final int flag;

    LockEnum(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
}
