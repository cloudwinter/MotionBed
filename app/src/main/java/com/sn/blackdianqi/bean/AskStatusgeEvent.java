package com.sn.blackdianqi.bean;

/**
 * Created by xiayundong on 2022/5/6.
 */
public class AskStatusgeEvent {

    private boolean askStatus;


    public boolean isAskStatus() {
        return askStatus;
    }

    public void setAskStatus(boolean askStatus) {
        this.askStatus = askStatus;
    }

    public AskStatusgeEvent() {
    }

    public AskStatusgeEvent(boolean askStatus) {
        this.askStatus = askStatus;
    }

}
