package com.sn.blackdianqi.bean;

public class PressBean {
    public String name;
    public String pressNo;
    public int value;

    public PressBean(final String name, final String pressNo, final int value) {
        this.name = name;
        this.pressNo = pressNo;
        this.value = value;
    }

    public String getPressNo() {
        return pressNo;
    }

    public void setPressNo(String pressNo) {
        this.pressNo = pressNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
