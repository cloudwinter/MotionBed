package com.sn.blackdianqi.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.dialog.WaitDialog;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.TranslucentActionBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeDeviceActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    public static final String TAG = "ChangeDeviceActivity";

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.llDianDong)
    LinearLayout llDianDong;

    @BindView(R.id.tvDisconnectDianDong)
    TextView tvDisconnectDianDong;

    @BindView(R.id.llQiNang)
    LinearLayout llQiNang;

    @BindView(R.id.tvDisconnectQiNang)
    TextView tvDisconnectQiNang;

    @BindView(R.id.llLengNuan)
    LinearLayout llLengNuan;

    @BindView(R.id.tvDisconnectLengNuan)
    TextView tvDisconnectLengNuan;

    // 加载中对话框
    private WaitDialog mWaitDialog;

    // 特征值
    protected BluetoothGattCharacteristic characteristic;

    private String cmdMain;
    private String type;
    private String deviceType;

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter(), Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_change_device);
        ButterKnife.bind(this);
        actionBar.setData(getResources().getString(R.string.change_device), R.mipmap.ic_back, null, 0, "", this);
        actionBar.setStatusBarHeight(getStatusBarHeight());

        // 启动蓝牙service
        Intent blueServiceIntent = new Intent(ChangeDeviceActivity.this, BluetoothLeService.class);
        startService(blueServiceIntent);
        bindService(blueServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        mWaitDialog = new WaitDialog(this);
        //启动连接动画
        mWaitDialog.setCanceledOnTouchOutside(true);
        mWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                LogUtils.e(TAG, "mWaitDialog 执行 onCancel");
            }
        });

        cmdMain = getIntent().getStringExtra("cmd");
        type = getIntent().getStringExtra("type");
        iniView();
    }

    private void iniView() {
        if (!TextUtils.isEmpty(cmdMain)) {
            String bedState = cmdMain.substring(18, 20);
            String m1State = cmdMain.substring(24, 26);
            String m2State = cmdMain.substring(30, 32);

            if (TextUtils.equals(bedState, "0A")) {
                llDianDong.setVisibility(View.VISIBLE);
            } else {
                llDianDong.setVisibility(View.GONE);
            }
            if (TextUtils.equals(m1State, "0B")) {
                llQiNang.setVisibility(View.VISIBLE);
            } else {
                llQiNang.setVisibility(View.GONE);
            }
            if (TextUtils.equals(m2State, "0C")) {
                llLengNuan.setVisibility(View.VISIBLE);
            } else {
                llLengNuan.setVisibility(View.GONE);
            }
        }
        if (!TextUtils.isEmpty(type)) {
            llDianDong.setVisibility(View.GONE);
            llQiNang.setVisibility(View.GONE);
            llLengNuan.setVisibility(View.GONE);
        }
        tvDisconnectDianDong.setOnClickListener(this);
        tvDisconnectQiNang.setOnClickListener(this);
        tvDisconnectLengNuan.setOnClickListener(this);
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
            ToastUtils.showToast(ChangeDeviceActivity.this, getString(R.string.device_no_connected));
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

    private void handleReceiveData(String cmd) {
        mWaitDialog.dismiss();
        cmd = cmd.toUpperCase().replaceAll(" ", "");
        if (cmd.contains("FFFFFFFF01002714")) {//询问状态回复
            cmdMain = cmd;
            try {
                Thread.sleep(200L);
                Intent intent = new Intent(this, ConnectMcuActivity.class);
                intent.putExtra("deviceType",deviceType);
                startActivity(intent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (cmd.contains("FFFFFFFF01002814")) {//设备断开连接
            ToastUtils.showToast(this, getResources().getString(R.string.success));
            cmd = "FFFFFFFF010026140F000000000000000000";
            cmd = cmd + BlueUtils.makeChecksum(cmd);
            sendBlueCmd(cmd);//发送询问状态
            mWaitDialog.setHint("状态查询中...");
            mWaitDialog.show();
        }
    }


    /* 意图过滤器 */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            LogUtils.i(TAG, "监听到蓝牙状态 ：" + action);
            if (action == BluetoothLeService.ACTION_GATT_DISCONNECTED) {
                // 监听到蓝牙已断开
                LogUtils.e(TAG, "监听到蓝牙状态 ：已断开");
                Prefer.getInstance().disConnected();
                ToastUtils.showToast(ChangeDeviceActivity.this, R.string.device_disconnect);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //处理发送过来的数据  (//有效数据)
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String data = bundle.getString(BluetoothLeService.EXTRA_DATA);
                    if (data != null) {
//                            data = "FFFFFFFF01000A0B011304";
                        LogUtils.e(TAG, "==首页  接收设备返回的数据==", data);
                        handleReceiveData(data);
                    }
                }
            }
        }
    };


    /* BluetoothLeService绑定的回调函数 */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            LogUtils.d(TAG, "BluetoothLeService 已绑定 HomeActivity");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtils.i(TAG, "BluetoothLeService 已断开");
        }
    };


    @Override
    public void onClick(View view) {
        String cmd = "";
        switch (view.getId()) {
            case R.id.tvDisconnectDianDong:
                cmd = "FFFFFFFF010028140A000000000000000100";
                cmd = cmd + BlueUtils.makeChecksum(cmd);
                sendBlueCmd(cmd);//发送询问状态
                mWaitDialog.setHint("设备断开中...");
                mWaitDialog.show();
                deviceType = "0A";
                break;
            case R.id.tvDisconnectQiNang:
                cmd = "FFFFFFFF010028140B000000000000000100";
                cmd = cmd + BlueUtils.makeChecksum(cmd);
                sendBlueCmd(cmd);//发送询问状态
                mWaitDialog.setHint("设备断开中...");
                mWaitDialog.show();
                deviceType = "0B";
                break;
            case R.id.tvDisconnectLengNuan:
                cmd = "FFFFFFFF010028140C000000000000000100";
                cmd = cmd + BlueUtils.makeChecksum(cmd);
                sendBlueCmd(cmd);//发送询问状态
                mWaitDialog.setHint("设备断开中...");
                mWaitDialog.show();
                deviceType = "0C";
                break;
        }
    }
}