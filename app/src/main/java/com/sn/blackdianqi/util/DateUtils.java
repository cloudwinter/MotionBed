package com.sn.blackdianqi.util;

import com.sn.blackdianqi.bean.DateBean;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xiayundong on 2021/9/26.
 */
public class DateUtils {

    public static Calendar calendar(long timeInMillis) {
        return calendar(new Date(timeInMillis));
    }

    public static Calendar calendar(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(date);
        return calendar;
    }

    public static void  main(String[] args) {
        System.out.println(new DateBean(new Date()));
    }
}
