package com.sn.blackdianqi.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Network;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.TranslucentActionBar;

import java.nio.ByteOrder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NetworkActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener {


    public static final String TAG = "NetworkActivity";

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;
    @BindView(R.id.edtWifiName)
    EditText edtWifiName;
    @BindView(R.id.edtWifiPassword)
    EditText edtWifiPassword;
    @BindView(R.id.ivSelectWifi)
    ImageView ivSelectWifi;
    @BindView(R.id.ivEye)
    ImageView ivEye;
    @BindView(R.id.ivSelect)
    ImageView ivSelect;
    @BindView(R.id.tvNextStep)
    TextView tvNextStep;

    @BindView(R.id.llConnect1)
    LinearLayout llConnect1;
    @BindView(R.id.llConnect2)
    LinearLayout llConnect2;
    @BindView(R.id.llConnect3)
    LinearLayout llConnect3;
    @BindView(R.id.llConnect4)
    LinearLayout llConnect4;
    @BindView(R.id.tvRetry)
    TextView tvRetry;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.tvFinish)
    TextView tvFinish;

    private String cWifiName = "";
    private boolean isRememberPassword = true;
    private boolean isShowPassword;

    public static int REQUEST_CODE = 107;

    private LocationManager locationManager;// 位置管理类
    private String provider;// 位置提供器
    private float longitude;
    private float latitude;
    private int askRetryTimes = 0; // 配网询问政策

    // 特征值
    protected BluetoothGattCharacteristic characteristic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_network);
        ButterKnife.bind(this);
        actionBar.setData(getResources().getString(R.string.distribution_network), R.mipmap.ic_back, null, 0, "", this);
        actionBar.setStatusBarHeight(getStatusBarHeight());
        initView();
        refreshWifi();
        getLocation();
    }

    private void getLocation() {
        // 获得LocationManager的实例
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            //优先使用gps
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            // 没有可用的位置提供器
            Toast.makeText(NetworkActivity.this, "无法获取经纬度!", Toast.LENGTH_LONG).show();
            return;
        }
        if (RunningContext.checkLocationPermission(this, true)) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                // 显示当前设备的位置信息
                longitude = (float) location.getLongitude();
                latitude = (float) location.getLatitude();
                Log.e("============", location.toString());
            } else {
                Toast.makeText(NetworkActivity.this, "获取经纬度失败!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void refreshWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (Network.isWifiAvailable(this)) {
            cWifiName = wifiInfo.getSSID().replaceAll("\"", "");
            if (TextUtils.isEmpty(cWifiName) || "null".equalsIgnoreCase(cWifiName)) {
                return;
            }
            getWifiState(cWifiName);
        }
    }

    /**
     * 获取是否已经保存Wifi密码
     *
     * @param wifiName
     */
    private void getWifiState(String wifiName) {
        String password = Prefer.getInstance().getWifiPassword(wifiName);
        edtWifiName.setText(wifiName);
        edtWifiPassword.setText(password);
    }

    private void initView() {
        ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRememberPassword = !isRememberPassword;
                if (isRememberPassword) {
                    ivSelect.setImageDrawable(getResources().getDrawable(R.mipmap.ic_checkbox_selected_sq));
                } else {
                    ivSelect.setImageDrawable(getResources().getDrawable(R.mipmap.ic_checkbox_normal_sq));
                }
            }
        });
        ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isShowPassword = !isShowPassword; // 切换状态
                if (isShowPassword) {
                    edtWifiPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    edtWifiPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        ivSelectWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NetworkActivity.this, WifiListActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llConnect1.setVisibility(View.VISIBLE);
                llConnect2.setVisibility(View.GONE);
                llConnect3.setVisibility(View.GONE);
            }
        });

        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String wifiName = edtWifiName.getText().toString().trim();
                String password = edtWifiPassword.getText().toString().trim();
                sendNetwork(wifiName, password);
            }
        });

        tvNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String wifiName = edtWifiName.getText().toString().trim();
                String password = edtWifiPassword.getText().toString().trim();
                if (TextUtils.isEmpty(wifiName) || TextUtils.isEmpty(password)) {
                    ToastUtils.showToast(NetworkActivity.this, "WIFI名称或密码不能为空");
                    return;
                }
                if (isRememberPassword) {
                    Prefer.getInstance().setWifiPassword(wifiName, password);
                }
                sendNetwork(wifiName, password);
            }
        });
    }

    /**
     * 发送配网信息
     *
     * @param wifiName
     * @param password
     */
    private void sendNetwork(String wifiName, String password) {
        String sendPrefix = "FFFFFFFF02001813";
        String wifiNameHex = BlueUtils.str2HexStr(wifiName);
        String passwordHex = BlueUtils.str2HexStr(password);
        String locationHex = BlueUtils.floatToHex(longitude, ByteOrder.BIG_ENDIAN) + BlueUtils.floatToHex(latitude, ByteOrder.BIG_ENDIAN);
        Log.e("sendNetwork", wifiNameHex + "\n" + passwordHex + "\n" + locationHex + "\n" + longitude + " \n" + latitude);

        String cmdWifiSSID01 = sendPrefix + "01";
        String cmdWifiSSID02 = sendPrefix + "02";
        String cmdWifiSSID03 = sendPrefix + "03";
        String cmdWifiSSID04 = sendPrefix + "04";
        String cmdWifiPwd01 = sendPrefix + "05";
        String cmdWifiPwd02 = sendPrefix + "06";
        String cmdWifiLocation = sendPrefix + "07" + locationHex;
        cmdWifiLocation = cmdWifiLocation + BlueUtils.makeChecksum(cmdWifiLocation);

        for (int i = 0; i < 64; i++) {
            if (i < 16) {
                if (wifiNameHex.length() - 1 >= i) {
                    cmdWifiSSID01 = cmdWifiSSID01 + wifiNameHex.charAt(i);
                } else {
                    cmdWifiSSID01 = cmdWifiSSID01 + 'F';
                }
                if (i == 15) {
                    cmdWifiSSID01 = cmdWifiSSID01 + BlueUtils.makeChecksum(cmdWifiSSID01);
                }
            } else if (16 <= i && i < 32) {
                if (wifiNameHex.length() - 1 >= i) {
                    cmdWifiSSID02 = cmdWifiSSID02 + wifiNameHex.charAt(i);
                } else {
                    cmdWifiSSID02 = cmdWifiSSID02 + 'F';
                }
                if (i == 31) {
                    cmdWifiSSID02 = cmdWifiSSID02 + BlueUtils.makeChecksum(cmdWifiSSID02);
                }
            } else if (32 <= i && i < 48) {
                if (wifiNameHex.length() - 1 >= i) {
                    cmdWifiSSID03 = cmdWifiSSID03 + wifiNameHex.charAt(i);
                } else {
                    cmdWifiSSID03 = cmdWifiSSID03 + 'F';
                }
                if (i == 47) {
                    cmdWifiSSID03 = cmdWifiSSID03 + BlueUtils.makeChecksum(cmdWifiSSID03);
                }
            } else {
                if (wifiNameHex.length() - 1 >= i) {
                    cmdWifiSSID04 = cmdWifiSSID04 + wifiNameHex.charAt(i);
                } else {
                    cmdWifiSSID04 = cmdWifiSSID04 + 'F';
                }
                if (i == 63) {
                    cmdWifiSSID04 = cmdWifiSSID04 + BlueUtils.makeChecksum(cmdWifiSSID04);
                }
            }
        }

        for (int j = 0; j < 32; j++) {
            if (j < 16) {
                if (passwordHex.length() - 1 >= j) {
                    cmdWifiPwd01 = cmdWifiPwd01 + passwordHex.charAt(j);
                } else {
                    cmdWifiPwd01 = cmdWifiPwd01 + 'F';
                }
                if (j == 15) {
                    cmdWifiPwd01 = cmdWifiPwd01 + BlueUtils.makeChecksum(cmdWifiPwd01);
                }
            } else {
                if (passwordHex.length() - 1 >= j) {
                    cmdWifiPwd02 = cmdWifiPwd02 + passwordHex.charAt(j);
                } else {
                    cmdWifiPwd02 = cmdWifiPwd02 + 'F';
                }
                if (j == 31) {
                    cmdWifiPwd02 = cmdWifiPwd02 + BlueUtils.makeChecksum(cmdWifiPwd02);
                }
            }
        }

        llConnect1.setVisibility(View.GONE);
        llConnect2.setVisibility(View.VISIBLE);
        llConnect3.setVisibility(View.GONE);
        llConnect4.setVisibility(View.GONE);

        //开始发送指令
        try {
            sendBlueCmd(cmdWifiSSID01);
            Thread.sleep(300);
            sendBlueCmd(cmdWifiSSID02);
            Thread.sleep(300);
            sendBlueCmd(cmdWifiSSID03);
            Thread.sleep(300);
            sendBlueCmd(cmdWifiSSID04);
            Thread.sleep(300);
            sendBlueCmd(cmdWifiPwd01);
            Thread.sleep(300);
            sendBlueCmd(cmdWifiPwd02);
            Thread.sleep(300);
            sendBlueCmd(cmdWifiLocation);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送配网询问码
     */
    public void sendNetworkAskReply() {
        Log.e("发送配网状态", askRetryTimes + "");
        String sendCmd = "FFFFFFFF02000A0A1204";
        sendBlueCmd(sendCmd);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode && resultCode == WifiListActivity.RESULT_CODE) {
            String wifiName = data.getStringExtra("wifiName");
            edtWifiName.setText(wifiName);
        }
    }

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
        // 启动蓝牙service
        Intent blueServiceIntent = new Intent(NetworkActivity.this, BluetoothLeService.class);
        startService(blueServiceIntent);
        bindService(blueServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter(), Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
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
            ToastUtils.showToast(NetworkActivity.this, getString(R.string.device_no_connected));
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
        Log.e("回复cmd:", cmd);
        if (cmd.indexOf("FFFFFFFF02001913") >= 0) {
            String status = cmd.substring(18, 20);
            if (!TextUtils.equals(status, "01")) {
                if (TextUtils.equals(status, "00")) {
                    llConnect1.setVisibility(View.GONE);
                    llConnect2.setVisibility(View.GONE);
                    llConnect3.setVisibility(View.VISIBLE);
                    llConnect4.setVisibility(View.GONE);
                } else if (TextUtils.equals(status, "0F")) {
                    llConnect1.setVisibility(View.GONE);
                    llConnect2.setVisibility(View.GONE);
                    llConnect3.setVisibility(View.GONE);
                    llConnect4.setVisibility(View.VISIBLE);
                }
                sendNetworkAskReply();
            } else {
                try {
                    Thread.sleep(6000);
                    sendNetworkAskReply();
                    askRetryTimes++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (cmd.indexOf("FFFFFFFF02000A14") >= 0) {
            String statusCmd = cmd.substring(28, 30);
            if (TextUtils.equals(statusCmd, "01") || TextUtils.equals(statusCmd, "00")) {
                if (askRetryTimes >= 10) {
                    llConnect1.setVisibility(View.GONE);
                    llConnect2.setVisibility(View.GONE);
                    llConnect3.setVisibility(View.VISIBLE);
                    llConnect4.setVisibility(View.GONE);
                } else {
                    try {
                        Thread.sleep(6000);
                        sendNetworkAskReply();
                        askRetryTimes++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else if (TextUtils.equals(statusCmd, "0F")) {
                llConnect1.setVisibility(View.GONE);
                llConnect2.setVisibility(View.GONE);
                llConnect3.setVisibility(View.GONE);
                llConnect4.setVisibility(View.VISIBLE);
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
                ToastUtils.showToast(NetworkActivity.this, R.string.device_disconnect);
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