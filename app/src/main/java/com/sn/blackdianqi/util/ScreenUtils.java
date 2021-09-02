package com.sn.blackdianqi.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 *  Created by wanghongchuang
 *  on 2016/8/25.
 *  email:844285775@qq.com
 */
public class ScreenUtils {

    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

}
