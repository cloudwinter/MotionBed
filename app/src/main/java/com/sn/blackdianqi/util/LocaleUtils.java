package com.sn.blackdianqi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * 切换语言的工具类
 */

public class LocaleUtils {

    private final static String TAG = "LocaleUtils";

    /**
     * 中文
     */
    public static final Locale LOCALE_CHINESE = Locale.CHINESE;
    /**
     * 英文
     */
    public static final Locale LOCALE_ENGLISH = Locale.ENGLISH;
    /**
     * 法文
     */
    public static final Locale LOCALE_FRENCH = Locale.FRENCH;
    /**
     * 俄文
     */
    public static final Locale LOCALE_RUSSIAN = new Locale("ru");
    /**
     * 保存SharedPreferences的文件名
     */
    private static final String LOCALE_FILE = "LOCALE_FILE";
    /**
     * 保存Locale的key
     */
    private static final String LOCALE_KEY = "LOCALE_KEY";

    /**
     * 获取用户设置的Locale
     *
     * @param pContext Context
     * @return Locale
     */
    public static Locale getUserLocale(Context pContext) {
        SharedPreferences _SpLocale = pContext.getSharedPreferences(LOCALE_FILE, Context.MODE_PRIVATE);
        String _LocaleJson = _SpLocale.getString(LOCALE_KEY, "");
        return jsonToLocale(_LocaleJson);
    }

    /**
     * 获取当前的Locale
     *
     * @param pContext Context
     * @return Locale
     */
    public static Locale getCurrentLocale(Context pContext) {
        Locale _Locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0有多语言设置获取顶部的语言
            _Locale = pContext.getResources().getConfiguration().getLocales().get(0);
        } else {
            _Locale = pContext.getResources().getConfiguration().locale;
        }
        return _Locale;
    }

    /**
     * 保存用户设置的Locale
     *
     * @param pContext    Context
     * @param pUserLocale Locale
     */
    public static void saveUserLocale(Context pContext, Locale pUserLocale) {
        SharedPreferences _SpLocal = pContext.getSharedPreferences(LOCALE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor _Edit = _SpLocal.edit();
        String _LocaleJson = localeToJson(pUserLocale);
        _Edit.putString(LOCALE_KEY, _LocaleJson);
        _Edit.apply();
    }

    /**
     * Locale转成json
     *
     * @param pUserLocale UserLocale
     * @return json String
     */
    private static String localeToJson(Locale pUserLocale) {
        Gson _Gson = new Gson();
        return _Gson.toJson(pUserLocale);
    }

    /**
     * json转成Locale
     *
     * @param pLocaleJson LocaleJson
     * @return Locale
     */
    private static Locale jsonToLocale(String pLocaleJson) {
        Gson _Gson = new Gson();
        return _Gson.fromJson(pLocaleJson, Locale.class);
    }

    /**
     * 更新Locale
     *
     * @param pContext       Context
     * @param pNewUserLocale New User Locale
     */
    public static void updateLocale(Context pContext, Locale pNewUserLocale) {
        if (needUpdateLocale(pContext, pNewUserLocale)) {
            Configuration _Configuration = pContext.getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                _Configuration.setLocale(pNewUserLocale);
            } else {
                _Configuration.locale = pNewUserLocale;
            }
            DisplayMetrics _DisplayMetrics = pContext.getResources().getDisplayMetrics();
            pContext.getResources().updateConfiguration(_Configuration, _DisplayMetrics);
            saveUserLocale(pContext, pNewUserLocale);
        }
    }

    /**
     * 判断需不需要更新
     *
     * @param pContext       Context
     * @param pNewUserLocale New User Locale
     * @return true / false
     */
    public static boolean needUpdateLocale(Context pContext, Locale pNewUserLocale) {
        return pNewUserLocale != null && !getCurrentLocale(pContext).equals(pNewUserLocale);
    }


    /**
     * 获取手机出厂时默认的densityDpi
     */
    public static int getDefaultDisplayDensity() {
        try {
            Class aClass = Class.forName("android.view.WindowManagerGlobal");
            Method method = aClass.getMethod("getWindowManagerService");
            method.setAccessible(true);
            Object iwm = method.invoke(aClass);
            Method getInitialDisplayDensity = iwm.getClass().getMethod("getInitialDisplayDensity", int.class);
            getInitialDisplayDensity.setAccessible(true);
            Object densityDpi = getInitialDisplayDensity.invoke(iwm, Display.DEFAULT_DISPLAY);
            Log.i(TAG, "getDefaultDisplayDensity: densityDpi="+densityDpi);
            return (int) densityDpi;
        } catch (Exception e) {
            Log.e(TAG, "getDefaultDisplayDensity: ", e);
            e.printStackTrace();
            return -1;
        }
    }
}
