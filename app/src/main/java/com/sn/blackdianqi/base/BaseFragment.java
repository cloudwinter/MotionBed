package com.sn.blackdianqi.base;

import android.os.Build;
import android.os.Bundle;

import com.sn.blackdianqi.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by d on 2016/12/2.
 */

public abstract class BaseFragment extends Fragment {

    // 同步控制回调
    public abstract void onTongbukzEvent(boolean show, boolean open);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        onTongbukzEvent(event.isTongbukzShow(), event.isTongbukzSwitch());
    }

    /**
     * 获取系统版本
     * @return
     */
    public static int getSystemVersion() {
        return Build.VERSION.SDK_INT;
    }



}
