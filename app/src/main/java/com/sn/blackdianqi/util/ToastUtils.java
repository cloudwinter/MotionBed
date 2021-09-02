package com.sn.blackdianqi.util;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.sn.blackdianqi.view.SNToast;


/**
 * Toast工具
 *  Created by wanghongchuang
 *  on 2016/8/25.
 *  email:844285775@qq.com
 */
public class ToastUtils {

    private static SNToast mToast;

    private static Handler mhandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    public static void showToast(Context context, int strId) {
        showToast(context, context.getString(strId), false);
    }

    public static void showToast(Context context, String text) {
        showToast(context, text, false);
    }

    public static void showToast(Context context, int strId, boolean lengthLong) {
        showToast(context, context.getString(strId), lengthLong);
    }


    public static void showToast(Context context, String text, boolean lengthLong) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        mhandler.removeCallbacks(r);
        if (null != mToast) {
            mToast.setText(text);
        } else {
            mToast = SNToast.makeText(context, text, Toast.LENGTH_LONG);
        }
        if (text.length() > 5) {
            lengthLong = true;
        }
        mhandler.postDelayed(r, lengthLong ? 1500 : 1000);
        mToast.show();
    }

    /**
     * 取消
     */
    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
