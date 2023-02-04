package com.sn.blackdianqi.activity;

import static com.sn.blackdianqi.BuildConfig.Debuggable;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.bean.AlarmBean;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.dialog.FaultDebugDialog;
import com.sn.blackdianqi.dialog.LanguageDialog;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.LoggerView;
import com.sn.blackdianqi.view.TranslucentActionBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    public static final String TAG = "SettingActivity";

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.ll_connect)
    LinearLayout llConnect;
    @BindView(R.id.tv_connect)
    TextView tvConnect;

    @BindView(R.id.ll_language)
    LinearLayout llLanguage;
    @BindView(R.id.tv_language)
    TextView tvLanguage;


    @BindView(R.id.ll_version)
    LinearLayout llVersion;
    @BindView(R.id.ll_privacy)
    LinearLayout llPrivacy;

    @BindView(R.id.ll_fault)
    LinearLayout llFaultDebug;

    @BindView(R.id.ll_debug)
    LinearLayout llDebug;

    @BindView(R.id.ll_alarm)
    LinearLayout llAlarm;
    @BindView(R.id.tv_alarm)
    TextView tvAlarm;

    @BindView(R.id.ll_sync_control)
    LinearLayout llSync;
    @BindView(R.id.cb_sync)
    CheckBox cbSync;

    // 特征值
    protected BluetoothGattCharacteristic characteristic;

    // 故障调试对话框
    private FaultDebugDialog faultDebugDialog;


    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mSetReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(mSetReceiver, makeGattUpdateIntentFilter());
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.blue_equipment), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }

        initView();
        setData();
//        // 发送同步控制指令
//        String cmd = "FFFFFFFF01000A0B0F";
//        cmd += BlueUtils.makeChecksum(cmd);
//        sendBlueCmd(cmd);
    }

    private void initView() {
        llConnect.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        llPrivacy.setOnClickListener(this);
        llAlarm.setOnClickListener(this);
        llDebug.setOnClickListener(this);
        if (Debuggable) {
            llDebug.setVisibility(View.VISIBLE);
        }
        llAlarm.setVisibility(View.GONE);
        llSync.setVisibility(View.GONE);
        // 获取当前系统的语言
        String language = Prefer.getInstance().getSelectedLanguage();
        if (language.equals("fr")) {
            tvLanguage.setText(R.string.french);
        } else if (language.equals("ja")) {
            tvLanguage.setText(R.string.japan);
        } else {
            tvLanguage.setText(R.string.english); // 默认是英文
        }

        llFaultDebug.setOnClickListener(this);
        faultDebugDialog = new FaultDebugDialog(this);
        if (isNeedShowFaultDebug()) {
            llFaultDebug.setVisibility(View.VISIBLE);
        }

        cbSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendBlueCmd("FF FF FF FF 01 00 09 0B 01 AA BB");
                } else {
                    sendBlueCmd("FF FF FF FF 01 00 09 0B 00 AA BB");
                }
            }
        });
    }


    /**
     * 是否显示故障调试
     *
     * @return
     */
    private boolean isNeedShowFaultDebug() {
        DeviceBean deviceBean = Prefer.getInstance().getConnectedDevice();
        if (deviceBean == null || TextUtils.isEmpty(deviceBean.getTitle())) {
            return false;
        }
        String title = deviceBean.getTitle();
        List<String> blueNames = new ArrayList<>();
        blueNames.add("QMS-IQ");
        blueNames.add("QMS-I06");
        blueNames.add("QMS-I16");
        blueNames.add("QMS-I26");
        blueNames.add("QMS-I36");
        blueNames.add("QMS-I46");
        blueNames.add("QMS-I56");
        blueNames.add("QMS-I66");
        blueNames.add("QMS-I76");
        blueNames.add("QMS-I86");
        blueNames.add("QMS-I96");
        blueNames.add("QMS-L04");
        blueNames.add("QMS-L14");
        blueNames.add("QMS-L24");
        blueNames.add("QMS-L34");
        blueNames.add("QMS-L44");
        blueNames.add("QMS-L54");
        blueNames.add("QMS-L64");
        blueNames.add("QMS-L74");
        blueNames.add("QMS-L84");
        blueNames.add("QMS-L94");
        boolean isNeedShow = false;
        for (String blueName : blueNames) {
            if (title.toUpperCase().contains(blueName)) {
                isNeedShow = true;
                break;
            }
        }
        return isNeedShow;
    }


    private void setData() {
        if (BlueUtils.isConnected()) {
            AlarmBean alarmBean = Prefer.getInstance().getAlarm(Prefer.getInstance().getLatelyConnectedDevice());
            if (alarmBean != null) {
                llAlarm.setVisibility(View.VISIBLE);
                if (alarmBean.isAlarmSwitch()) {
                    tvAlarm.setText(alarmBean.getHourStr()+":"+alarmBean.getMinuteStr());
                } else {
                    tvAlarm.setText("");
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BlueUtils.isConnected()) {
            tvConnect.setText(R.string.connected);
            setData();
        } else {
            tvConnect.setText(R.string.not_connected);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_connect:
                Intent intent = new Intent(SettingActivity.this, ConnectActivity.class);
                intent.putExtra("from", "set");
                startActivity(intent);
                break;
            case R.id.ll_language:
                LanguageDialog languageDialog = new LanguageDialog(this);
                languageDialog.show();
                break;
            case R.id.ll_privacy:
                Intent webIntent = new Intent(SettingActivity.this, WebActivity.class);
                startActivity(webIntent);
                break;
            case R.id.ll_fault:
                if (BlueUtils.isConnected()) {
                    sendBlueCmd("FF FF FF FF 03 00 5A 00 02 FE D2");
                    faultDebugDialog.show();
                } else {
                    ToastUtils.showToast(RunningContext.sAppContext, R.string.device_no_connected);
                }
                break;
            case R.id.ll_alarm:
                Intent intentAlarm = new Intent();
                intentAlarm.setClass(SettingActivity.this, AlarmActivity.class);
                startActivity(intentAlarm);
                break;
            case R.id.ll_debug:
                LoggerView.me.loggerSwitch();
                break;
            case R.id.ll_sync_control:
                String cmd = "";
                if (cbSync.isChecked()) {
                    cmd = "FFFFFFFF0100090B00";
                } else {
                    cmd = "FFFFFFFF0100090B01";
                }
                cmd += BlueUtils.makeChecksum(cmd);
                sendBlueCmd(cmd);
                break;
        }
    }

    /**
     * 回码
     *
     * @param cmd
     */
    private void handleReceiveData(String cmd) {
        cmd = cmd.toUpperCase().replaceAll(" ", "");
//        if (cmd.contains("FFFFFFFF01000A0B")) {
//            llSync.setVisibility(View.VISIBLE);
//            if (cmd.substring(16, 18).equals("01")) {
//                cbSync.setChecked(true);
//            } else {
//                cbSync.setChecked(false);
//            }
//            return;
//        }
        if (!cmd.contains("FFFFFFFF0304")) {
            return;
        }
        LogUtils.i(TAG, "故障回复码", cmd);
        String faultPartVal = cmd.substring(12, 16);
        String faultTypeVal = cmd.substring(16, 20);
        faultDebugDialog.setFaultBody(faultPartVal, faultTypeVal);
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
            ToastUtils.showToast(RunningContext.sAppContext, getString(R.string.device_no_connected));
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


    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mSetReceiver = new BroadcastReceiver() {
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
                        LogUtils.e("SettingActivity", "==设置  接收设备返回的数据==", data);
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
