package com.sn.blackdianqi.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xiayundong on 2021/9/26.
 */
public class DateBean implements Serializable {

    private Date date;

    private String year;

    private String endYear;

    private String month;

    private String day;

    private String week;

    private String hour;

    private String minute;

    private String second;

    public DateBean(Date date) {
        this.date = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        year = calendar.get(Calendar.YEAR) + "";
        endYear = year.substring(2, 4);
        // 通过calendar 获取到的月份比实际月份少1
        int calendarMonth = calendar.get(Calendar.MONTH) + 1;
        month = calendarMonth < 10 ? "0" + calendarMonth : calendarMonth + "";
        day = calendar.get(Calendar.DATE) < 10 ? "0" + calendar.get(Calendar.DATE) : calendar.get(Calendar.DATE) + "";

        hour = calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR_OF_DAY) + "";
        minute = calendar.get(Calendar.MINUTE) < 10 ? "0" + calendar.get(Calendar.MINUTE) : calendar.get(Calendar.MINUTE) + "";
        second = calendar.get(Calendar.SECOND) < 10 ? "0" + calendar.get(Calendar.SECOND) : calendar.get(Calendar.SECOND) + "";
        //一周的第几天
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            week = "07";
        } else {
            week = "0" + (calendar.get(Calendar.DAY_OF_WEEK) - 1);
        }

    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEndYear() {
        return endYear;
    }

    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }

    @Override
    public String toString() {
        return "DateBean{" +
                "date=" + date +
                ", year='" + year + '\'' +
                ", endYear='" + endYear + '\'' +
                ", month='" + month + '\'' +
                ", day='" + day + '\'' +
                ", week='" + week + '\'' +
                ", hour='" + hour + '\'' +
                ", minute='" + minute + '\'' +
                ", second='" + second + '\'' +
                '}';
    }


    public static void main(String[] args) {
        System.out.println(new DateBean(new Date()));
    }
}
