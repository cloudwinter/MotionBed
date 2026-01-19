package com.sn.blackdianqi.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;


import com.sn.blackdianqi.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 系统异常捕获
 * Created by xiayundong on 2018/10/31.
 */

public class AppUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "AppUncaughtException";

    private static AppUncaughtExceptionHandler sExceptionHandler;

    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private boolean mCrashing;

    private AppUncaughtExceptionHandler() {

    }

    public static AppUncaughtExceptionHandler getInstance() {
        if (sExceptionHandler == null) {
            sExceptionHandler = new AppUncaughtExceptionHandler();
        }
        return sExceptionHandler;
    }


    public void init(Context context) {
        mCrashing = false;
        mContext = context.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (mCrashing) {
            // 防止多次触发
            return;
        }
        mCrashing = true;
        // 打印异常信息
        ex.printStackTrace();
        if (!handleException(ex) && mDefaultHandler != null) {
            // 默认自己的异常处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 强制杀掉当前进程 FIXME
            Process.killProcess(Process.myPid());
            System.exit(0);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        try {
            // TODO
            // 第一步：获取异常信息
            String exceptionStr = getCrashExceptionData(ex);
            Log.e(TAG, "异常信息： " + exceptionStr);
            // 第二步：保存到sd卡
            saveExceptionToLocalCache(exceptionStr);
            // 第三步：上传到服务器
            // 第四步：弹出对话框
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private String getCrashExceptionData(Throwable ex) {
        StringBuilder builder = new StringBuilder();
        String packageName = mContext.getPackageName();
        PackageManager pm = mContext.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {

            if (ex != null) {
                // app message
                builder.append("APP Version: " + packageInfo.versionName);
                builder.append("_" + packageInfo.versionCode + "\n");

                // phone message
                builder.append("OS Version: " + Build.VERSION.RELEASE);
                builder.append("_");
                builder.append(Build.VERSION.SDK_INT + "\n");

                // phone factory
                builder.append("Vendor: " + Build.MANUFACTURER + "\n");

                // phone model
                builder.append("Model: " + Build.MODEL + "\n");

                String errorMsg = ex.getLocalizedMessage();
                if (TextUtils.isEmpty(errorMsg)) {
                    errorMsg = ex.getMessage();
                }
                if (TextUtils.isEmpty(errorMsg)) {
                    errorMsg = ex.toString();
                }
                builder.append("Exception: " + errorMsg + "\n");
                StackTraceElement[] stackTraceElements = ex.getStackTrace();
                for (StackTraceElement element : stackTraceElements) {
                    builder.append(element.toString() + "\n");
                }
            } else {
                builder.append("NO Exception, Throwable is null \n");
            }

        }
        return builder.toString();
    }

    /**
     * 保存到本地缓存中
     *
     * @param exceptionStr
     */
    private void saveExceptionToLocalCache(String exceptionStr) {
        if (TextUtils.isEmpty(exceptionStr)) {
            return;
        }
        Log.i(TAG, "保存到sdcard开始");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = sdf.format(new Date());
        String fileName = "crash-" + currentDateStr + "-log.txt";
        String cachePath = mContext.getExternalCacheDir().getAbsolutePath();
        String errorPath = cachePath + "/error/";
        LogUtils.i(TAG, "存储文件目录为： " + errorPath);
        File errorFile = new File(errorPath);
        if (!errorFile.exists()) {
            errorFile.mkdirs();
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(errorPath + fileName);
            outputStream.write(exceptionStr.getBytes());
            outputStream.flush();
            Log.i(TAG, "保存到sdcard结束");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
