package com.sn.blackdianqi.base;

import android.os.Build;

import androidx.fragment.app.Fragment;

/**
 * Created by d on 2016/12/2.
 */

public class BaseFragment extends Fragment {

    /**
     * 获取状态栏高度
     * @return
     */
    public int getStatusBarHeight() {
        if(getSystemVersion() >= 19){
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
     * @return
     */
    public static int getSystemVersion() {
        return Build.VERSION.SDK_INT;
    }
}
