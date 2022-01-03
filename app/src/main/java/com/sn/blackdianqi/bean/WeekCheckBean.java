package com.sn.blackdianqi.bean;

import java.io.Serializable;

/**
 * Created by xiayundong on 2021/9/25.
 */
public class WeekCheckBean implements Serializable {

    /**
     * 1,2,3,4,5,6,7
     */
    private int week;

    /**
     * 星期一
     */
    private String weekName;

    /**
     * 是否选中
     */
    private boolean isChecked;

}
