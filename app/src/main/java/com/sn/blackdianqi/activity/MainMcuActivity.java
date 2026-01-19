package com.sn.blackdianqi.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
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

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.bean.AskStatusgeEvent;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.TranslucentActionBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainMcuActivity extends BaseActivity implements View.OnClickListener, TranslucentActionBar.ActionBarClickListener {

    public static final String TAG = "MainMcuActivity";

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;


    @BindView(R.id.llModule1)
    LinearLayout llModule1;

    @BindView(R.id.llModule2)
    LinearLayout llModule2;

    @BindView(R.id.llModule3)
    LinearLayout llModule3;

    private String blueName;
    private String deviceAddress;
    private String cmdMain;

    // 特征值
    protected BluetoothGattCharacteristic characteristic;
    private boolean isFirst;//是否从搜索页过来的

    @Override
    public void onLeftClick() {
//        finish();
        Intent intent = new Intent(MainMcuActivity.this, ConnectActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRightClick() {
        Intent intent = new Intent(MainMcuActivity.this, Setting2Activity.class);
        intent.putExtra("cmd", cmdMain);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_main_mcu);
        ButterKnife.bind(this);
        actionBar.setData(null, R.mipmap.ic_back, null, R.mipmap.ic_set, getString(R.string.setting), this);
        actionBar.setStatusBarHeight(getStatusBarHeight());
        DeviceBean deviceBean = Prefer.getInstance().getConnectedDevice();
        if (deviceBean != null) {
            blueName = deviceBean.getTitle();
            deviceAddress = deviceBean.getAddress();
        }
        isFirst = getIntent().getBooleanExtra("isFirst", false);
        LogUtils.e(TAG, "当前连接的蓝牙名称为：" + blueName);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 启动蓝牙service
        Intent blueServiceIntent = new Intent(MainMcuActivity.this, BluetoothLeService.class);
        startService(blueServiceIntent);
        bindService(blueServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter(), Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }

        RunningContext.threadPool().execute(new Runnable() {
            @Override
            public void run() {
                askStatus();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
    }


    private void initView() {
        llModule1.setOnClickListener(this);
        llModule2.setOnClickListener(this);
        llModule3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (!TextUtils.isEmpty(cmdMain)) {
            Intent intent = new Intent();
            switch (view.getId()) {
                case R.id.llModule1:
                    String bedState = cmdMain.substring(18, 20);
                    if (TextUtils.equals(bedState, "0A")) {//已连接电动床
                        intent.setClass(MainMcuActivity.this, HomeMcuActivity.class);
                    } else {//未连接电动床----->去MCU的搜索页面
                        intent.setClass(MainMcuActivity.this, ConnectMcuActivity.class);
                    }
                    intent.putExtra("cmd", cmdMain);
                    intent.putExtra("deviceType", "0A");
                    break;
                case R.id.llModule2:
                    String m1State = cmdMain.substring(24, 26);
                    if (TextUtils.equals(m1State, "0B")) {//已连接气囊
                        intent.setClass(MainMcuActivity.this, HomeMcuActivity.class);
                    } else {//未连接气囊----->去MCU的搜索页面
                        intent.setClass(MainMcuActivity.this, ConnectMcuActivity.class);
                    }
                    intent.putExtra("cmd", cmdMain);
                    intent.putExtra("deviceType", "0B");
                    break;
                case R.id.llModule3:
                    String m2State = cmdMain.substring(30, 32);
                    if (TextUtils.equals(m2State, "0C")) {//已连接冷暖
                        intent.setClass(MainMcuActivity.this, HomeMcuActivity.class);
                    } else {//未连接冷暖----->去MCU的搜索页面
                        intent.setClass(MainMcuActivity.this, ConnectMcuActivity.class);
                    }
                    intent.putExtra("cmd", cmdMain);
                    intent.putExtra("deviceType", "0C");
                    break;
            }
            isFirst = false;
            startActivity(intent);
        } else {
            askStatus();
        }
    }

    private void askStatus() {
        try {
            Thread.sleep(300L);
            String cmd = "FFFFFFFF010026140F000000000000000000";
            cmd = cmd + BlueUtils.makeChecksum(cmd);
            sendBlueCmd(cmd);//发送询问状态
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            ToastUtils.showToast(MainMcuActivity.this, getString(R.string.device_no_connected));
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
        cmd = cmd.toUpperCase().replaceAll(" ", "");
        if (cmd.contains("FFFFFFFF01002714")) {//询问状态回复
            cmdMain = cmd;
            Log.e("MainMcuActivity收到回复cmd:", cmd);
            String bedState = cmd.substring(18, 20);
            String m1State = cmd.substring(24, 26);
            String m2State = cmd.substring(30, 32);

            String deviceType = "";
            if (TextUtils.equals(bedState, "0A")) {
                deviceType = "0A";
            } else if (TextUtils.equals(m1State, "0B")) {
                deviceType = "0B";
            } else if (TextUtils.equals(m2State, "0C")) {
                deviceType = "0C";
            }

            if (!TextUtils.isEmpty(deviceType) && isFirst) {
                isFirst = false;
                Intent intent = new Intent(MainMcuActivity.this, HomeMcuActivity.class);
                intent.putExtra("cmd", cmd);
                intent.putExtra("deviceType", deviceType);
                startActivity(intent);
            }
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
                ToastUtils.showToast(MainMcuActivity.this, R.string.device_disconnect);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //处理发送过来的数据  (//有效数据)
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String data = bundle.getString(BluetoothLeService.EXTRA_DATA);
                    if (data != null) {
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
}