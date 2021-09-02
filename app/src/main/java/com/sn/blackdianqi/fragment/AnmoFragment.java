package com.sn.blackdianqi.fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseFragment;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.AnjianAnmoView;
import com.sn.blackdianqi.view.AnjianAnmoYuanView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AnmoFragment extends BaseFragment implements View.OnClickListener {
    
    public static final String TAG = "DengguangFragment";

    @BindView(R.id.img_anjian_top_icon)
    ImageView topIconImgView;
    @BindView(R.id.text_anjian_top_title)
    TextView topTitleTextView;

    @BindView(R.id.view_10time)
    AnjianAnmoYuanView min10View;
    @BindView(R.id.view_20time)
    AnjianAnmoYuanView min20View;
    @BindView(R.id.view_30time)
    AnjianAnmoYuanView min30View;

    @BindView(R.id.view_anmo_pinglv)
    AnjianAnmoView anmoPinglvView;
    @BindView(R.id.view_anmo_toubu)
    AnjianAnmoView anmoToubuView;
    @BindView(R.id.view_anmo_zubu)
    AnjianAnmoView anmoZubuView;

    /**
     * 默认间隔
     */
    protected final static long DEFAULT_INTERVAL = 2000;

    // 特征值
    protected BluetoothGattCharacteristic characteristic;




    /* 意图过滤器 */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mAnmoReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        getActivity().registerReceiver(mAnmoReceiver, makeGattUpdateIntentFilter());
        View view = inflater.inflate(R.layout.fragment_anmo, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        min10View.setOnClickListener(this);
        min20View.setOnClickListener(this);
        min30View.setOnClickListener(this);
        anmoPinglvView.setChildClickListener(new AnjianAnmoView.ChildClickListener() {
            @Override
            public void minusClick() {
                sendBlueCmd("FF FF FF FF 05 00 00 00 15 16 CF");
            }

            @Override
            public void plusClick() {
                sendBlueCmd("FF FF FF FF 05 00 00 00 14 D7 0F");
            }
        });

        anmoToubuView.setChildClickListener(new AnjianAnmoView.ChildClickListener() {
            @Override
            public void minusClick() {
                sendBlueCmd("FF FF FF FF 05 00 00 00 11 17 0C");
            }

            @Override
            public void plusClick() {
                sendBlueCmd("FF FF FF FF 05 00 00 00 10 D6 CC");
            }
        });

        anmoZubuView.setChildClickListener(new AnjianAnmoView.ChildClickListener() {
            @Override
            public void minusClick() {
                sendBlueCmd("FF FF FF FF 05 00 00 00 13 96 CD");
            }

            @Override
            public void plusClick() {
                sendBlueCmd("FF FF FF FF 05 00 00 00 12 57 0D");
            }
        });
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
            case R.id.view_10time:
                if (min10View.isSelected()) {
                    clear();
                    sendBlueCmd("FF FF FF FF 05 00 00 00 1C D6 C9");
                } else {
                    sendBlueCmd("FF FF FF FF 05 00 00 00 16 56 CE");
                }
                min10View.setSelected(!min10View.isSelected());
                min20View.setSelected(false);
                min30View.setSelected(false);
                break;
            case R.id.view_20time:
                if (min20View.isSelected()) {
                    clear();
                    sendBlueCmd("FF FF FF FF 05 00 00 00 1C D6 C9");
                } else {
                    sendBlueCmd("FF FF FF FF 05 00 00 00 17 97 0E");
                }
                min10View.setSelected(false);
                min20View.setSelected(!min20View.isSelected());
                min30View.setSelected(false);
                break;
            case R.id.view_30time:
                if (min30View.isSelected()) {
                    clear();
                    sendBlueCmd("FF FF FF FF 05 00 00 00 1C D6 C9");
                } else {
                    sendBlueCmd("FF FF FF FF 05 00 00 00 18 D7 0A");
                }
                min10View.setSelected(false);
                min20View.setSelected(false);
                min30View.setSelected(!min30View.isSelected());
                break;

        }
    }


    private void clear() {
        // 头部按摩停止
        anmoToubuView.setLevel(0);
        // 足部按摩停止
        anmoZubuView.setLevel(0);
        // 按摩频率1档
        anmoPinglvView.setLevel(0);
    }



    protected void handleReceiveData(String data) {
        if (data.contains("FF FF FF FF 05 00 00 01")) {
            if (data.contains("FF FF FF FF 05 00 00 01 00 D6 90")) {
                // 头部按摩停止
                anmoToubuView.setLevel(0);
            }
            if (data.contains("FF FF FF FF 05 00 00 01 1E 56 98")) {
                // 头部按摩一档
                anmoToubuView.setLevel(1);
            }
            if (data.contains("FF FF FF FF 05 00 00 01 1F 97 58")) {
                // 头部按摩二档
                anmoToubuView.setLevel(2);
            }
            if (data.contains("FF FF FF FF 05 00 00 01 20 D7 48")) {
                // 头部按摩三档
                anmoToubuView.setLevel(3);
            }
        } else if (data.contains("FF FF FF FF 05 00 00 02")) {
            if (data.contains("FF FF FF FF 05 00 00 02 00 D6 60")) {
                // 足部按摩停止
                anmoZubuView.setLevel(0);
            }
            if (data.contains("FF FF FF FF 05 00 00 02 21 16 78")) {
                // 足部按摩一档
                anmoZubuView.setLevel(1);
            }
            if (data.contains("FF FF FF FF 05 00 00 02 22 56 79")) {
                // 足部按摩二档
                anmoZubuView.setLevel(2);
            }
            if (data.contains("FF FF FF FF 05 00 00 02 23 97 B9")) {
                // 足部按摩三档
                anmoZubuView.setLevel(3);
            }
        } else if (data.contains("FF FF FF FF 05 00 00 03")) {
            if (data.contains("FF FF FF FF 05 00 00 03 24 D7 EB")) {
                // 按摩频率1档
                anmoPinglvView.setLevel(1);
            }
            if (data.contains("FF FF FF FF 05 00 00 03 25 16 2B")) {
                // 按摩频率2档
                anmoPinglvView.setLevel(2);
            }
            if (data.contains("FF FF FF FF 05 00 00 03 26 56 2A")) {
                // 按摩频率3档
                anmoPinglvView.setLevel(3);
            }
            if (data.contains("FF FF FF FF 05 00 00 03 27 97 EA")) {
                // 按摩频率4档
                anmoPinglvView.setLevel(4);
            }
        }
    }


    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mAnmoReceiver = new BroadcastReceiver() {
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
                        LogUtils.e("==快捷  接收设备返回的数据==", data);
                        handleReceiveData(data);
                    }
                }
            }
        }
    };


}
