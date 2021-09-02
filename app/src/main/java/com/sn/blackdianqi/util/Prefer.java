package com.sn.blackdianqi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.bean.DeviceBean;


/**
 * Created by wanghongchuang
 * on 2016/8/25.
 * email:844285775@qq.com
 */
public class Prefer {
    private static final String TAG = "Prefer";
    private static final String PREFERENCE_FILE = "prefer_config";

    private static Prefer mInstance;
    private SharedPreferences mPref;
    private final String KEY_TYPE_LAN = "KEY_TYPE_LAN";//语言的切换  1 ，中文 2，英文
    private final String KEY_IS_LOGIN = "KEY_IS_LOGIN";
    private final String KEY_M1 = "KEY_M1";
    private final String KEY_M2 = "KEY_M2";
    private final String KEY_M3 = "KEY_M3";
    private final String KEY_M4 = "KEY_M4";
    private final String KEY_M5 = "KEY_M5";
    private final String KEY_BLESTATUS = "KEY_BLESTATUS";
    private final String KEY_BLECONNECTDEVICE = "KEY_KEY_BLECONNECTDEVICE";
    private final String KEY_NEEDGUIDE = "KEY_NEEDGUIDE";
    private final String KEY_DECICE = "KEY_DECICE";
    private final String KEY_LANGUAGE = "KEY_LANGUAGE";

    public static Prefer getInstance() {
        if (null == mInstance) {
            mInstance = new Prefer();
        }
        return mInstance;
    }

    private Prefer() {
        mPref = MyApplication.getInstance().getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
    }


    /**
     * zh/en/fr
     * @param language
     */
    public void setSelectedLanguage(String language) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_LANGUAGE, language);
        editor.commit();
    }

    /**
     * 获取当前选中语言
     * @return
     */
    public String getSelectedLanguage() {
       return mPref.getString(KEY_LANGUAGE,"en");
    }



    /**
     * 是否登录
     */
    public void setLogined(boolean isLogin) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(KEY_IS_LOGIN, isLogin);
        editor.commit();
    }

    public boolean isLogined() {
        return mPref.getBoolean(KEY_IS_LOGIN, false);
    }

    /**
     * 语言切换
     */
    public void setTypeLan(String typeLan) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_TYPE_LAN, typeLan);
        editor.commit();
    }

    public String isTypeLan() {
        return mPref.getString(KEY_TYPE_LAN, "");
    }
    /**
     * M1
     */
    public void setM1(String m1) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_M1, m1);
        editor.commit();
    }

    public String getM1() {
        return mPref.getString(KEY_M1, "lv");
    }

    /**
     * M2
     */
    public void setM2(String m2) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_M2, m2);
        editor.commit();
    }

    public String getM2() {
        return mPref.getString(KEY_M2, "lv");
    }

    /**
     * 一键看电视
     */
    public void setM3(String m3) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_M3, m3);
        editor.commit();
    }

    public String getM3() {
        return mPref.getString(KEY_M3, "lv");
    }

    /**
     * 零压力状态
     */
    public void setM4(String m4) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_M4, m4);
        editor.commit();
    }

    public String getM4() {
        return mPref.getString(KEY_M4, "lv");
    }

    /**
     * 止鼾状态
     */
    public void setM5(String m5) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_M5, m5);
        editor.commit();
    }

    public String getM5() {
        return mPref.getString(KEY_M5, "lv");
    }

    //引导页
    public void setNeedGuide(boolean needGuide) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(KEY_NEEDGUIDE, needGuide);
        editor.commit();
    }

    public boolean getneedGuide() {
        return mPref.getBoolean(KEY_NEEDGUIDE, true);
    }

    //蓝牙连接状态
    public void setBleStatus(String bleStatus, DeviceBean deviceBean) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_BLESTATUS, bleStatus);
        if (bleStatus.equals("未连接")) {
            editor.putString(KEY_BLECONNECTDEVICE,"");
        } else {
            String json = new Gson().toJson(deviceBean);
            LogUtils.d("Prefer","setBleStatus:"+json);
            editor.putString(KEY_BLECONNECTDEVICE,json);
        }
        editor.commit();
    }

    /**
     * 获取当前选中的device
     * @return
     */
    public DeviceBean getConnectedDevice() {
        String value = mPref.getString(KEY_BLECONNECTDEVICE, "");
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        DeviceBean deviceBean = null;
        try {
            deviceBean = new Gson().fromJson(value, DeviceBean.class);
        } catch (Exception e) {
            LogUtils.e("Prefer", e.getMessage());
            e.printStackTrace();
        }
        return deviceBean;
    }

    public String getBleStatus() {
        return mPref.getString(KEY_BLESTATUS, "未连接");
    }

    /**
     * 判断蓝牙是否连接
     * @return
     */
    public boolean isBleConnected() {
        String status = mPref.getString(KEY_BLESTATUS, "未连接");
        if (status.equals("未连接")) {
            return false;
        }
        return true;
    }

    //蓝牙当前地址
    public void setLatelyConnectedDevice(String deviceAddress) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(KEY_DECICE, deviceAddress);
        editor.commit();
    }

    public String getLatelyConnectedDevice() {
        return mPref.getString(KEY_DECICE, "");
    }


    //退出登录后清除缓存数据
    public void clearData() {
//        String setBleStatus = getBleStatus();
        String currentDevice = getLatelyConnectedDevice();
        String language = getSelectedLanguage();

        //清楚数据
        SharedPreferences.Editor editor = mPref.edit();
        editor.clear();
        editor.commit();

        //重新写入
        setLatelyConnectedDevice(currentDevice);
        setSelectedLanguage(language);
    }
}
