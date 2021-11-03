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
import android.widget.ImageView;
import android.widget.TextView;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.base.BaseFragment;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.AnjianAnmoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DengguangFragment extends BaseFragment implements View.OnClickListener {
    
    public static final String TAG = "DengguangFragment";

    @BindView(R.id.img_anjian_top_icon)
    ImageView topIconImgView;
    @BindView(R.id.text_anjian_top_title)
    TextView topTitleTextView;

    @BindView(R.id.tv_10fenzhong)
    TextView tenMinsTextView;
    @BindView(R.id.tv_8xiaoshi)
    TextView eightHoursTextView;
    @BindView(R.id.tv_10xiaoshi)
    TextView tenHoursTextView;

    @BindView(R.id.view_dengguang_level)
    AnjianAnmoView dengguangLevel;

    /**
     * 默认间隔
     */
    protected final static long DEFAULT_INTERVAL = 2000;

    // 特征值
    protected BluetoothGattCharacteristic characteristic;

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mDengguangReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        getActivity().registerReceiver(mDengguangReceiver, makeGattUpdateIntentFilter());
        View view = inflater.inflate(R.layout.fragment_dengguang, container, false);
        ButterKnife.bind(this, view);
        initView();
        RunningContext.threadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000L);
                    askStatus();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    private void initView() {
        tenMinsTextView.setOnClickListener(this);
        eightHoursTextView.setOnClickListener(this);
        tenHoursTextView.setOnClickListener(this);

        dengguangLevel.setVisibility(View.INVISIBLE);
        dengguangLevel.setChildClickListener(new AnjianAnmoView.ChildClickListener() {
            @Override
            public void minusClick() {
                if (dengguangLevel.getLevel() == 0) {
                    ToastUtils.showToast(RunningContext.sAppContext,R.string.dengguangliangdu_min_tips);
                    return;
                }
                sendDengguangLevelCmd(dengguangLevel.getLevel() - 1);
            }

            @Override
            public void plusClick() {
                if (dengguangLevel.getLevel() == 10) {
                    ToastUtils.showToast(RunningContext.sAppContext,R.string.dengguangliangdu_max_tips);
                    return;
                }
                sendDengguangLevelCmd(dengguangLevel.getLevel() + 1);
            }
        });
    }

    private void sendDengguangLevelCmd(int level) {
        String cmd = "";
        switch (level) {
            case 0:
                cmd = "FF FF FF FF 05 00 00 00 23 96 D9";
                break;
            case 1:
                cmd = "FF FF FF FF 05 00 00 01 23 97 49";
                break;
            case 2:
                cmd = "FF FF FF FF 05 00 00 02 23 97 B9";
                break;
            case 3:
                cmd = "FF FF FF FF 05 00 00 03 23 96 29";
                break;
            case 4:
                cmd = "FF FF FF FF 05 00 00 04 23 94 19";
                break;
            case 5:
                cmd = "FF FF FF FF 05 00 00 05 23 95 89";
                break;
            case 6:
                cmd = "FF FF FF FF 05 00 00 06 23 95 79";
                break;
            case 7:
                cmd = "FF FF FF FF 05 00 00 07 23 94 E9";
                break;
            case 8:
                cmd = "FF FF FF FF 05 00 00 08 23 91 19";
                break;
            case 9:
                cmd = "FF FF FF FF 05 00 00 09 23 90 89";
                break;
            case 10:
                cmd = "FF FF FF FF 05 00 00 0A 23 90 79";
                break;
        }
        if (!TextUtils.isEmpty(cmd)) {
            sendBlueCmd(cmd);
        }
    }

    /**
     * 灯光初始化询问码
     */
    private void askStatus() {
        // 发送灯光指令
        sendBlueCmd("FF FF FF FF 05 00 05 FF 23 C7 28");
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
            case R.id.tv_10fenzhong:
                sendBlueCmd("FF FF FF FF 05 00 00 00 19 16 CA");
                if (tenMinsTextView.isSelected()) {
                    tenMinsTextView.setSelected(false);
                } else {
                    tenMinsTextView.setSelected(true);
                    eightHoursTextView.setSelected(false);
                    tenHoursTextView.setSelected(false);
                }
                break;
            case R.id.tv_8xiaoshi:
                sendBlueCmd("FF FF FF FF 05 00 00 00 1A 56 CB");
                // 8小时
                if (eightHoursTextView.isSelected()) {
                    eightHoursTextView.setSelected(false);
                } else {
                    tenMinsTextView.setSelected(false);
                    eightHoursTextView.setSelected(true);
                    tenHoursTextView.setSelected(false);
                }
                break;
            case R.id.tv_10xiaoshi:
                sendBlueCmd("FF FF FF FF 05 00 00 00 1B 97 0B");
                // 10小时
                if (tenHoursTextView.isSelected()) {
                    tenHoursTextView.setSelected(false);
                } else {
                    tenMinsTextView.setSelected(false);
                    eightHoursTextView.setSelected(false);
                    tenHoursTextView.setSelected(true);
                }
                break;

        }
    }

    /**
     * 蓝牙回收命令
     * @param receivedCmd
     */
    private void handleReceiveData(String receivedCmd) {
        if (TextUtils.isEmpty(receivedCmd)) {
            return;
        }
        if (!receivedCmd.contains("FF FF FF FF 05 00 01")) {
            return;
        }
        dengguangLevel.setVisibility(View.VISIBLE);
        // 去除空格
        receivedCmd = receivedCmd.replaceAll(" ", "");
        String level = receivedCmd.substring(14,16);
        int levelNum = BlueUtils.covert16TO10(level);
        dengguangLevel.setLevel(levelNum);
        if (levelNum == 0) {
            tenMinsTextView.setSelected(false);
            eightHoursTextView.setSelected(false);
            tenHoursTextView.setSelected(false);
        }
    }

    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mDengguangReceiver = new BroadcastReceiver() {
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
                        LogUtils.e("DengguangFragment","==灯光  接收设备返回的数据==", data);
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


}
