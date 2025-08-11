package com.sn.blackdianqi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.dialog.DoubleConfirmDialog;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.DateUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.TranslucentActionBar;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class XinLvDaiActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener {

    public static final String TAG = "XinLvDaiActivity";

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.ll_set_wifi)
    LinearLayout llSetWifi;
    @BindView(R.id.ll_shishixinlvdata)
    LinearLayout llShishixinlvdata;
    @BindView(R.id.ll_shuimianbaogao)
    LinearLayout llShuimianbaogao;
    @BindView(R.id.tvNetWorkTitle)
    TextView tvNetWorkTitle;
    @BindView(R.id.tvNetworkDesc)
    TextView tvNetworkDesc;

    String networkTitle = "";
    String networkDesc = "";
    String networkDialogTitle = "";
    boolean networkShow = false;

    // 特征值
    protected BluetoothGattCharacteristic characteristic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_xin_lv_dai);
        ButterKnife.bind(this);
        actionBar.setData(getResources().getString(R.string.zhinengjiance), R.mipmap.ic_back, null, 0, "", this);
        actionBar.setStatusBarHeight(getStatusBarHeight());
        initView();
    }

    private void initView() {
        llSetWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(XinLvDaiActivity.this, NetworkActivity.class);
                startActivity(intent);
            }
        });
        llShishixinlvdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String languageTag = Locale.getDefault().toLanguageTag();
                String xinlvdaiMac = Prefer.getInstance().getXinlvdaiMac(Prefer.getInstance().getLatelyConnectedDevice());
                String url = "";
                if (TextUtils.equals("zh-CN", languageTag) || TextUtils.equals("zh-Hans-CN", languageTag)) {
                    url = "https://alltoone.he-info.cn/h5/#/mattress/oneDevice/oneDevice?mac=" + xinlvdaiMac + "&token=2E7JNgIe61QEiP1dZVmNCyqOm4Oz77eVx6RljYQjHR2GkZMrXAK5gpCSmZYg9dXFgRdreG9FdWX7qgBB6yNfY7ks9Tdq9E39A9ZNoSSZGN033RJijayPOmNM3kHsakKVTHKC5I6lNtvgM1KN5HCDN9golGpOWWCvY0auuZQRcoBJ8nGG7TcqMWkEkGyOV6Ghu7uFvpcn0YWXLe1use49YZRkQau6ONaN7f8KvLKzmSRSBw4s6xbR0MpiXBPPs2Y6bmyLH2LK4sSMmSnebLBqCk0oM5gVDGqagY9GMJaZQbzkdZuiZuRqLDh2p5gAbPt8xbhZhGyKW7YaEcfNqx8Q5cOP7NgXUMKZblNc3NFVDzVuAKbl0TzRsnsY7hsLguKT5Axru56VYEDSNPqdla6xzHk9WIFEPUF3qLZQvKb2NbE7BktuCEnXCqIWwm4yTpfw3VzgXmln4pE3pNTBlDBOgaWSYZDaYkjKWKO0y5TULKOjtks2aBQRyw65I1Az8NEM";
                } else {
                    url = "https://alltoone.he-info.cn/h5/#/mattress/oneDevice/oneDevice?mac=" + xinlvdaiMac + "&token=2E7JNgIe61QEiP1dZVmNCyqOm4Oz77eVx6RljYQjHR2GkZMrXAK5gpCSmZYg9dXFgRdreG9FdWX7qgBB6yNfY7ks9Tdq9E39A9ZNoSSZGN033RJijayPOmNM3kHsakKVTHKC5I6lNtvgM1KN5HCDN9golGpOWWCvY0auuZQRcoBJ8nGG7TcqMWkEkGyOV6Ghu7uFvpcn0YWXLe1use49YZRkQau6ONaN7f8KvLKzmSRSBw4s6xbR0MpiXBPPs2Y6bmyLH2LK4sSMmSnebLBqCk0oM5gVDGqagY9GMJaZQbzkdZuiZuRqLDh2p5gAbPt8xbhZhGyKW7YaEcfNqx8Q5cOP7NgXUMKZblNc3NFVDzVuAKbl0TzRsnsY7hsLguKT5Axru56VYEDSNPqdla6xzHk9WIFEPUF3qLZQvKb2NbE7BktuCEnXCqIWwm4yTpfw3VzgXmln4pE3pNTBlDBOgaWSYZDaYkjKWKO0y5TULKOjtks2aBQRyw65I1Az8NEM";
                }
                Intent intent = new Intent(XinLvDaiActivity.this, WebCommonActivity.class);
                intent.putExtra("title", getResources().getString(R.string.heart_rate_data));
                intent.putExtra("url", url);
                startActivity(intent);

            }
        });
        llShuimianbaogao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String languageTag = Locale.getDefault().toLanguageTag();
                String xinlvdaiMac = Prefer.getInstance().getXinlvdaiMac(Prefer.getInstance().getLatelyConnectedDevice());
                String date = DateUtils.getYesToday();
                String url = "";
                if (TextUtils.equals("zh-CN", languageTag) || TextUtils.equals("zh-Hans-CN", languageTag)) {
                    url = "https://alltoone.he-info.cn/h5/#/mattress/sleep/sleep?date=" + date + "&mac=" + xinlvdaiMac + "&token=2E7JNgIe61QEiP1dZVmNCyqOm4Oz77eVx6RljYQjHR2GkZMrXAK5gpCSmZYg9dXFgRdreG9FdWX7qgBB6yNfY7ks9Tdq9E39A9ZNoSSZGN033RJijayPOmNM3kHsakKVTHKC5I6lNtvgM1KN5HCDN9golGpOWWCvY0auuZQRcoBJ8nGG7TcqMWkEkGyOV6Ghu7uFvpcn0YWXLe1use49YZRkQau6ONaN7f8KvLKzmSRSBw4s6xbR0MpiXBPPs2Y6bmyLH2LK4sSMmSnebLBqCk0oM5gVDGqagY9GMJaZQbzkdZuiZuRqLDh2p5gAbPt8xbhZhGyKW7YaEcfNqx8Q5cOP7NgXUMKZblNc3NFVDzVuAKbl0TzRsnsY7hsLguKT5Axru56VYEDSNPqdla6xzHk9WIFEPUF3qLZQvKb2NbE7BktuCEnXCqIWwm4yTpfw3VzgXmln4pE3pNTBlDBOgaWSYZDaYkjKWKO0y5TULKOjtks2aBQRyw65I1Az8NEM";;
                } else {
                    url = "https://alltoone.he-info.cn/h5/#/mattress/sleep/sleep?date=" + date + "&mac=" + xinlvdaiMac + "&token=2E7JNgIe61QEiP1dZVmNCyqOm4Oz77eVx6RljYQjHR2GkZMrXAK5gpCSmZYg9dXFgRdreG9FdWX7qgBB6yNfY7ks9Tdq9E39A9ZNoSSZGN033RJijayPOmNM3kHsakKVTHKC5I6lNtvgM1KN5HCDN9golGpOWWCvY0auuZQRcoBJ8nGG7TcqMWkEkGyOV6Ghu7uFvpcn0YWXLe1use49YZRkQau6ONaN7f8KvLKzmSRSBw4s6xbR0MpiXBPPs2Y6bmyLH2LK4sSMmSnebLBqCk0oM5gVDGqagY9GMJaZQbzkdZuiZuRqLDh2p5gAbPt8xbhZhGyKW7YaEcfNqx8Q5cOP7NgXUMKZblNc3NFVDzVuAKbl0TzRsnsY7hsLguKT5Axru56VYEDSNPqdla6xzHk9WIFEPUF3qLZQvKb2NbE7BktuCEnXCqIWwm4yTpfw3VzgXmln4pE3pNTBlDBOgaWSYZDaYkjKWKO0y5TULKOjtks2aBQRyw65I1Az8NEM";;
                }
                Intent intent = new Intent(XinLvDaiActivity.this, WebCommonActivity.class);
                intent.putExtra("title", getResources().getString(R.string.sleep_report));
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
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
        Intent blueServiceIntent = new Intent(XinLvDaiActivity.this, BluetoothLeService.class);
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

    private void askStatus() {
        try {
            Thread.sleep(500L);
            String cmd = "FFFFFFFF02000A0A1204";
            sendBlueCmd(cmd);//发送询问wifi配网状态询问码：
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
        cmd = cmd + BlueUtils.crc16Modbus(cmd);
        Log.i(TAG, "sendBlueCmd: " + cmd);
        // 判断蓝牙是否连接
        if (!BlueUtils.isConnected()) {
            ToastUtils.showToast(XinLvDaiActivity.this, getString(R.string.device_no_connected));
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
        if (cmd.indexOf("FFFFFFFF02000A14") > -1) {//询问WIFI状态回复
            //WIFi网络状态
            String networkGH = cmd.substring(28, 30).toUpperCase();
            switch (networkGH) {
                case "00":
                    networkTitle = getResources().getString(R.string.network_not_connected);
                    networkDesc = getResources().getString(R.string.configure_wifi);
                    networkDialogTitle = getResources().getString(R.string.network_hint1);
                    networkShow = true;
                    break;
                case "01":
                    networkTitle = getResources().getString(R.string.network_not_connected);
                    networkDesc = getResources().getString(R.string.replace_wifi);
                    networkDialogTitle = getResources().getString(R.string.network_hint3);
                    networkShow = true;
                    break;
                case "0A":
                    networkTitle = getResources().getString(R.string.network_instability);
                    networkDesc = getResources().getString(R.string.replace_wifi);
                    networkDialogTitle = getResources().getString(R.string.network_hint2);
                    networkShow = true;
                    break;
                case "0F":
                    networkTitle = getResources().getString(R.string.network_connected);
                    networkDesc = getResources().getString(R.string.replace_wifi);
                    networkDialogTitle = "";
                    networkShow = false;
                    break;
            }

            tvNetWorkTitle.setText(networkTitle);
            tvNetworkDesc.setText(networkDesc);

            if (networkShow) {
                DoubleConfirmDialog.builder(XinLvDaiActivity.this)
                        .setContent(networkDialogTitle)
                        .setLeftButName(getResources().getString(R.string.replace))
                        .setRightButName(getResources().getString(R.string.got_it))
                        .setListener(new DoubleConfirmDialog.OnPermissionsDialogListener() {
                            @Override
                            public void cancleOnClick(DoubleConfirmDialog dialog) {
                                dialog.dismiss();
                                Intent intent = new Intent(XinLvDaiActivity.this, NetworkActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void determineOnClick(DoubleConfirmDialog dialog, String content) {
                                dialog.dismiss();
                            }
                        }).show();
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
                ToastUtils.showToast(XinLvDaiActivity.this, R.string.device_disconnect);
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