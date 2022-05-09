package com.sn.blackdianqi.bean;

/**
 * Created by xiayundong on 2022/5/6.
 */
public class MessageEvent {

    private boolean tongbukzShow;

    private boolean tongbukzSwitch;

    public MessageEvent() {
    }

    public MessageEvent(boolean tongbukzShow, boolean tongbukzSwitch) {
        this.tongbukzShow = tongbukzShow;
        this.tongbukzSwitch = tongbukzSwitch;
    }

    public boolean isTongbukzShow() {
        return tongbukzShow;
    }

    public void setTongbukzShow(boolean tongbukzShow) {
        this.tongbukzShow = tongbukzShow;
    }

    public boolean isTongbukzSwitch() {
        return tongbukzSwitch;
    }

    public void setTongbukzSwitch(boolean tongbukzSwitch) {
        this.tongbukzSwitch = tongbukzSwitch;
    }


    @Override
    public String toString() {
        return "MessageEvent{" +
                "tongbukzShow=" + tongbukzShow +
                ", tongbukzSwitch=" + tongbukzSwitch +
                '}';
    }
}
