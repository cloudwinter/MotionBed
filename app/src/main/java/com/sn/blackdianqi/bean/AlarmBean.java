package com.sn.blackdianqi.bean;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by xiayundong on 2021/9/25.
 */
public class AlarmBean implements Serializable {

    /**
     * 闹钟开关
     */
    private boolean alarmSwitch;

    /**
     * 时间小时
     */
    private String hourStr;
    /**
     * 时间分钟
     */
    private String minuteStr;
    /**
     * 模式
     */
    private String modeCode;

    /**
     * 选中的星期
     */
    private HashMap<Integer, Boolean> weekCheckBeanMap = new HashMap<>();

    /**
     * 按摩
     */
    private boolean anmo;

    /**
     * 响铃
     */
    private boolean xiangling;

    public boolean isAlarmSwitch() {
        return alarmSwitch;
    }

    public void setAlarmSwitch(boolean alarmSwitch) {
        this.alarmSwitch = alarmSwitch;
    }

    public String getHourStr() {
        return hourStr;
    }

    public void setHourStr(String hourStr) {
        this.hourStr = hourStr;
    }

    public String getMinuteStr() {
        return minuteStr;
    }

    public void setMinuteStr(String minuteStr) {
        this.minuteStr = minuteStr;
    }

    public String getModeCode() {
        return modeCode;
    }

    public void setModeCode(String modeCode) {
        this.modeCode = modeCode;
    }

    public HashMap<Integer, Boolean> getWeekCheckBeanMap() {
        return weekCheckBeanMap;
    }

    public void setWeekCheckBeanMap(HashMap<Integer, Boolean> weekCheckBeanMap) {
        this.weekCheckBeanMap = weekCheckBeanMap;
    }

    public boolean isAnmo() {
        return anmo;
    }

    public void setAnmo(boolean anmo) {
        this.anmo = anmo;
    }

    public boolean isXiangling() {
        return xiangling;
    }

    public void setXiangling(boolean xiangling) {
        this.xiangling = xiangling;
    }
}
