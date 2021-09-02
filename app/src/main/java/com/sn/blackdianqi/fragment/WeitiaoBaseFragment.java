package com.sn.blackdianqi.fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;
import android.view.MotionEvent;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseFragment;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.ToastUtils;

public class WeitiaoBaseFragment extends BaseFragment {
    
    
    public static final String TAG = "WeitiaoBaseFragment";

    /**
     * 默认间隔
     */
    protected final static long DEFAULT_INTERVAL = 2000;

    // 特征值
    protected BluetoothGattCharacteristic characteristic;

    /**
     * 发送蓝牙命令
     *
     * @param cmd
     */
    protected void sendBlueCmd(String cmd) {
        cmd = cmd.replace(" ", "");
        Log.i(TAG, "sendBlueCmd: " + cmd);
        // 判断蓝牙是否连接
        if (!BlueUtils.isConnected()) {
            ToastUtils.showToast(getContext(), getString(R.string.device_no_connected));
            LogUtils.i(TAG, "sendBlueCmd -> 蓝牙未连接");
            return;
        }
        if (characteristic == null) {
            characteristic = MyApplication.getInstance().gattCharacteristic;
        }
        if (characteristic == null) {
            LogUtils.i(TAG, "sendBlueCmd -> 特征值未获取到");
            return;
        }
        characteristic.setValue(BlueUtils.StringToBytes(cmd));
        MyApplication.getInstance().mBluetoothLeService.writeCharacteristic(characteristic);
    }

    protected boolean isUPorCancel(int eventAction) {
        if (MotionEvent.ACTION_UP == eventAction || MotionEvent.ACTION_CANCEL == eventAction) {
            return true;
        }
        return false;
    }

}
