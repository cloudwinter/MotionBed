package com.sn.blackdianqi.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.util.LocaleUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.PreferenceUtil;

import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Administrator on 2016/12/3 0003.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);



        //初始化PreferenceUtil
        PreferenceUtil.init(this);
        //根据上次的语言设置，重新设置语言
        switchLanguage(Prefer.getInstance().getSelectedLanguage());
        LogUtils.d("BaseActivity", "当前系统的版本为：" + Build.VERSION.SDK_INT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
    }

    public void showDialog(String title, DialogInterface.OnClickListener clickListener) {
        /***
         这里使用了 android.support.v7.app.AlertDialog.Builder
         可以直接在头部写 import android.support.v7.app.AlertDialog
         那么下面就可以写成 AlertDialog.Builder
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(title);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", clickListener);
        builder.show();
    }

    public void showDialog(String title, String negative, String positive, DialogInterface.OnClickListener clickListener) {
        /***
         这里使用了 android.support.v7.app.AlertDialog.Builder
         可以直接在头部写 import android.support.v7.app.AlertDialog
         那么下面就可以写成 AlertDialog.Builder
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(title);
        builder.setCancelable(false);
        builder.setNegativeButton(negative, null);
        builder.setPositiveButton(positive, clickListener);
        builder.show();
    }

    //隐藏输入框
    public void closrKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        if (getSystemVersion() >= 19) {
            //获取status_bar_height资源的ID
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                return getResources().getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }

    /**
     * 获取系统版本
     *
     * @return
     */
    public static int getSystemVersion() {
        return Build.VERSION.SDK_INT;
    }

    @Override
    public void finish() {
        closrKeyboard();
        super.finish();
    }


    protected void switchLanguage(String language) {
        //设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("fr")) {
            config.locale = Locale.FRENCH;
        } else {
            config.locale = Locale.ENGLISH;
        }
        if (resources.getConfiguration().fontScale != 1) {
            config.fontScale = 1.0f;
        }
        Log.d("BaseActivity", "densityDpi="+config.densityDpi);
        resources.updateConfiguration(config, dm);

    }

//    @Override
//    public Resources getResources() {
//        Resources res = super.getResources();
//        Configuration config=new Configuration();
//        config.setToDefaults();
//        res.updateConfiguration(config,res.getDisplayMetrics() );
//        return res;
//    }
}
