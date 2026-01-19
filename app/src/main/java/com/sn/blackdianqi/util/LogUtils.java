package com.sn.blackdianqi.util;


import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.view.LoggerView;

/**
 *  Created by wanghongchuang
 *  on 2016/8/25.
 *  email:844285775@qq.com
 */

public class LogUtils {

    public static void i(String tag, String... strs){
        if(MyApplication.isDebug){
            StringBuilder sb = new StringBuilder();
            for(String str: strs){
                sb.append(str);
            }
            LoggerView.i(tag, sb.toString());
        }
    }

    public static void v(String tag, String... strs){
        if(MyApplication.isDebug){
            StringBuilder sb = new StringBuilder();
            for(String str: strs){
                sb.append(str);
            }
            LoggerView.v(tag, sb.toString());
        }
    }

    public static void d(String tag, String... strs){
        if(MyApplication.isDebug){
            StringBuilder sb = new StringBuilder();
            for(String str: strs){
                sb.append(str);
            }
            LoggerView.d(tag, sb.toString());
        }
    }

    public static void w(String tag, String... strs){
        if(MyApplication.isDebug){
            StringBuilder sb = new StringBuilder();
            for(String str: strs){
                sb.append(str);
            }
            LoggerView.w(tag, sb.toString());
        }
    }

    public static void e(String tag, String... strs){
        if(MyApplication.isDebug){
            StringBuilder sb = new StringBuilder();
            for(String str: strs){
                sb.append(str);
            }
            LoggerView.e(tag, sb.toString());
        }
    }
}
