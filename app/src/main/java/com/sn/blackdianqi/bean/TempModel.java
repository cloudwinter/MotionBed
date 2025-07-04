package com.sn.blackdianqi.bean;

public class TempModel {

    private String temp;
    private String tempCmd;
    private String tempColor;


    public TempModel(String temp, String tempCmd, String tempColor) {
        this.temp = temp;
        this.tempCmd = tempCmd;
        this.tempColor = tempColor;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTempCmd() {
        return tempCmd;
    }

    public void setTempCmd(String tempCmd) {
        this.tempCmd = tempCmd;
    }

    public String getTempColor() {
        return tempColor;
    }

    public void setTempColor(String tempColor) {
        this.tempColor = tempColor;
    }
}
