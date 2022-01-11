package com.sn.blackdianqi.util;

import com.sn.blackdianqi.bean.DateBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    /**
     * 转小时
     * @param value 数据值
     * @param ratio 比例
     * @return
     */
    public static String transferToH(String value, int ratio) {
        BigDecimal val = new BigDecimal(value);
        BigDecimal result = val.divide(new BigDecimal(ratio)).setScale(2, RoundingMode.HALF_UP);
        return result.toString();
    }

    public static void  main(String[] args) {
        System.out.println(new DateBean(new Date()));
    }
}
