package com.sn.blackdianqi.fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.activity.SleepAdjustActivity;
import com.sn.blackdianqi.activity.SleepDataEntryActivity;
import com.sn.blackdianqi.activity.SleepReportMainActivity;
import com.sn.blackdianqi.activity.SleepTimerSelectActivity;
import com.sn.blackdianqi.base.BaseFragment;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.ProlateItemView;
import com.sn.blackdianqi.view.ProlateSwitchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 智能睡眠页面
 */
public class SmartSleepFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "SmartSleepFragment";

    public static final int TIMER_REQUEST_CODE = 109;

    @BindView(R.id.item_shujuluru)
    ProlateItemView mShujuluruItemView;
    @BindView(R.id.item_jiaodutiaozheng)
    ProlateItemView mJiaodutiaozhengItemView;
    @BindView(R.id.item_shuimiandingshi)
    ProlateItemView mShuimiandingshiItemView;
    @BindView(R.id.item_shuimianbaogao)
    ProlateItemView mShuimianbaogaoItemView;
    @BindView(R.id.switch_shuimian)
    ProlateSwitchView mShuimianSwitchView;
    @BindView(R.id.switch_yedeng)
    ProlateSwitchView mYedengSwitchView;

    /**
     * 智能睡眠开关
     */
    boolean zhinengShuimianOpen = false;
    /**
     * 智能夜灯开关
     */
    boolean zhinengYedengOpen = false;
    /**
     * 睡眠定时标记
     */
    String sleepTimer = "00";

    // 特征值
    protected BluetoothGattCharacteristic characteristic;

    @Override
    public void onTongbukzEvent(boolean show, boolean open) {

    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mSmartSleepReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        getActivity().registerReceiver(mSmartSleepReceiver, makeGattUpdateIntentFilter());
        View view = inflater.inflate(R.layout.fragment_smartsleep, container, false);
        ButterKnife.bind(this, view);
        initView();
        sleepTimer = RunningContext.sleepTimer;
        setSleepTimerDesc(sleepTimer);
        return view;
    }

    private void initView() {
        mShujuluruItemView.setOnClickListener(this);
        mJiaodutiaozhengItemView.setOnClickListener(this);
        mShuimiandingshiItemView.setOnClickListener(this);
        mShuimianbaogaoItemView.setOnClickListener(this);
        mShuimianSwitchView.setOnClickListener(this);
        mYedengSwitchView.setOnClickListener(this);
    }


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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_shujuluru:
                Intent sleepDataEntryIntent = new Intent();
                sleepDataEntryIntent.setClass(getActivity(), SleepDataEntryActivity.class);
                startActivity(sleepDataEntryIntent);
                break;
            case R.id.item_jiaodutiaozheng:
                Intent sleepAdjustIntent = new Intent();
                sleepAdjustIntent.setClass(getActivity(), SleepAdjustActivity.class);
                startActivity(sleepAdjustIntent);
                break;
            case R.id.item_shuimiandingshi:
                Intent intent = new Intent();
                intent.setClass(getActivity(), SleepTimerSelectActivity.class);
                intent.putExtra(SleepTimerSelectActivity.EXTRA_KEY, sleepTimer);
                startActivityForResult(intent,TIMER_REQUEST_CODE);
                break;
            case R.id.item_shuimianbaogao:
                Intent sleepMainIntent = new Intent();
                sleepMainIntent.setClass(getActivity(), SleepReportMainActivity.class);
                startActivity(sleepMainIntent);
                break;
            case R.id.switch_shuimian:
                if (zhinengShuimianOpen) {
                    sendBlueCmd("FFFFFFFF050000F03FD310");
                    mShuimianSwitchView.setTitle(getString(R.string.ss_item_zhinengshuimian_close));
                } else {
                    sendBlueCmd("FFFFFFFF050000003F9710");
                    mShuimianSwitchView.setTitle(getString(R.string.ss_item_zhinengshuimian_open));
                }
                zhinengShuimianOpen = !zhinengShuimianOpen;
                mShuimianSwitchView.setSelected(zhinengShuimianOpen);
                break;
            case R.id.switch_yedeng:
                if (zhinengYedengOpen) {
                    sendBlueCmd("FFFFFFFF0200110B001A04");
                    mYedengSwitchView.setTitle(getString(R.string.ss_item_zhinengyedeng_close));
                } else {
                    sendBlueCmd("FFFFFFFF0200110B011B04");
                    mYedengSwitchView.setTitle(getString(R.string.ss_item_zhinengyedeng_open));
                }
                zhinengYedengOpen = !zhinengYedengOpen;
                mYedengSwitchView.setSelected(zhinengYedengOpen);
                break;
        }
    }

    /**
     * 蓝牙回收命令
     *
     * @param receivedCmd
     */
    private void handleReceiveData(String receivedCmd) {
        if (TextUtils.isEmpty(receivedCmd)) {
            return;
        }
        if (receivedCmd.contains("FFFFFFFF02000A14") && receivedCmd.length() > 36) {
            // 智能页面开关回码
            zhinengShuimianOpen = receivedCmd.substring(32, 34).equals("01") ? true : false;
            mShuimianSwitchView.setSelected(zhinengShuimianOpen);
            if (zhinengShuimianOpen) {
                mShuimianSwitchView.setTitle(getString(R.string.ss_item_zhinengshuimian_open));
            } else {
                mShuimianSwitchView.setTitle(getString(R.string.ss_item_zhinengshuimian_close));
            }
            zhinengYedengOpen = receivedCmd.substring(34, 36).equals("01") ? true : false;
            mYedengSwitchView.setSelected(zhinengYedengOpen);
            if (zhinengYedengOpen) {
                mYedengSwitchView.setTitle(getString(R.string.ss_item_zhinengyedeng_open));
            } else {
                mYedengSwitchView.setTitle(getString(R.string.ss_item_zhinengyedeng_close));
            }
        } else if (receivedCmd.contains("FFFFFFFF02000E0B") && receivedCmd.length() > 18) {
            // 睡眠定时回码
            String sleepTimer = receivedCmd.substring(16, 18);
            RunningContext.sleepTimer = sleepTimer;
            setSleepTimerDesc(sleepTimer);
        }
    }


    private void setSleepTimerDesc(String sleepTimer) {
        if (sleepTimer.equals("00")) {
            mShuimiandingshiItemView.setDesc(getString(R.string.ss_item_no_time));
        } else if (sleepTimer.equals("01")) {
            mShuimiandingshiItemView.setDesc("20:00");
        } else if (sleepTimer.equals("02")) {
            mShuimiandingshiItemView.setDesc("20:30");
        } else if (sleepTimer.equals("03")) {
            mShuimiandingshiItemView.setDesc("21:00");
        } else if (sleepTimer.equals("04")) {
            mShuimiandingshiItemView.setDesc("21:30");
        } else if (sleepTimer.equals("05")) {
            mShuimiandingshiItemView.setDesc("22:00");
        } else if (sleepTimer.equals("06")) {
            mShuimiandingshiItemView.setDesc("22:30");
        } else if (sleepTimer.equals("07")) {
            mShuimiandingshiItemView.setDesc("23:00");
        } else if (sleepTimer.equals("08")) {
            mShuimiandingshiItemView.setDesc("23:30");
        }
    }

    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mSmartSleepReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) { //发现GATT服务器
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //处理发送过来的数据  (//有效数据)
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String data = bundle.getString(BluetoothLeService.EXTRA_DATA);
                    if (data != null) {
                        LogUtils.e(TAG, "==智能睡眠  接收设备返回的数据==", data);
                        data = data.replaceAll(" ", "");
                        handleReceiveData(data);
                    }
                }
            }
        }
    };


    /* 意图过滤器 */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == TIMER_REQUEST_CODE && data != null) {
            String extraVal = data.getStringExtra(SleepTimerSelectActivity.EXTRA_KEY);
            if (!TextUtils.isEmpty(extraVal)) {
                sleepTimer = extraVal;
                setSleepTimerDesc(sleepTimer);
            }
        }
    }
}
