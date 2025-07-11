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
import com.sn.blackdianqi.bean.AskStatusgeEvent;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.AnjianAnmoYuanView;
import com.sn.blackdianqi.view.AnjianTextView;
import com.sn.blackdianqi.view.TranslucentActionBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnmoSetActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    public static final String TAG = "AnmoSetActivity";


    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.view_quanshen)
    AnjianTextView quanshenView;
    @BindView(R.id.view_beibu)
    AnjianTextView beibuView;
    @BindView(R.id.view_yaobu)
    AnjianTextView yaobuView;
    @BindView(R.id.view_jingbu)
    AnjianTextView jingbuView;
    @BindView(R.id.view_yujia)
    AnjianTextView yujiaView;

    @BindView(R.id.tv_plus)
    TextView tvPlus;
    @BindView(R.id.ll_plus)
    LinearLayout llPlus;
    @BindView(R.id.tv_minus)
    TextView tvMinus;
    @BindView(R.id.ll_minus)
    LinearLayout llMinus;

    @BindView(R.id.view_10time)
    AnjianAnmoYuanView view10time;
    @BindView(R.id.view_20time)
    AnjianAnmoYuanView view20time;
    @BindView(R.id.view_30time)
    AnjianAnmoYuanView view30time;

    @BindView(R.id.tvConfirm)
    TextView tvConfirm;

    private List<View> weitiaoViews = new ArrayList<>();
    private List<View> timeViews = new ArrayList<>();

    private int weitiaoMode = 0;//微调模式、默认全身按摩
    private String weitiaoValue = "03";//微调模式值、默认全身按摩值
    private int timeMode = -1;//按摩时间
    private String timeValue = "";//按摩时间值
    private int upperValue = 5;//按摩强度上限
    private int lowerValue = 1;//按摩强度下限

    private String sendPrefix = "FFFFFFFFFF0D020800";//微调前缀

    // 特征值
    protected BluetoothGattCharacteristic characteristic;

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_anmo_set);
        ButterKnife.bind(this);
        actionBar.setData(null, R.mipmap.ic_back, null, 0, "", this);
        actionBar.setStatusBarHeight(getStatusBarHeight());
        initView();
    }

    private void initView() {
        weitiaoViews.add(quanshenView);
        weitiaoViews.add(beibuView);
        weitiaoViews.add(yaobuView);
        weitiaoViews.add(jingbuView);
        weitiaoViews.add(yujiaView);

        timeViews.add(view10time);
        timeViews.add(view20time);
        timeViews.add(view30time);


        quanshenView.setOnClickListener(this);
        beibuView.setOnClickListener(this);
        yaobuView.setOnClickListener(this);
        jingbuView.setOnClickListener(this);
        yujiaView.setOnClickListener(this);

        view10time.setOnClickListener(this);
        view20time.setOnClickListener(this);
        view30time.setOnClickListener(this);

        llPlus.setOnClickListener(this);
        llMinus.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);

        setWeitiaoSelected(weitiaoMode);
    }

    private void setWeitiaoSelected(int index) {
        weitiaoMode = index;
        for (int i = 0; i < weitiaoViews.size(); i++) {
            if (i == index) {
                weitiaoViews.get(i).setSelected(true);
            } else {
                weitiaoViews.get(i).setSelected(false);
            }
        }
    }

    private void setTimeSelected(int index) {
        timeMode = index;
        for (int i = 0; i < timeViews.size(); i++) {
            if (i == index) {
                timeViews.get(i).setSelected(true);
            } else {
                timeViews.get(i).setSelected(false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        String cmd = "";
        switch (view.getId()) {
            case R.id.view_quanshen:
                setWeitiaoSelected(0);
                weitiaoValue = "03";
                cmd = sendPrefix + weitiaoValue + "00";
                sendBlueCmd(cmd);
                break;
            case R.id.view_beibu:
                setWeitiaoSelected(1);
                weitiaoValue = "12";
                cmd = sendPrefix + weitiaoValue + "00";
                sendBlueCmd(cmd);
                break;
            case R.id.view_yaobu:
                setWeitiaoSelected(2);
                weitiaoValue = "05";
                cmd = sendPrefix + weitiaoValue + "00";
                sendBlueCmd(cmd);
                break;
            case R.id.view_jingbu:
                setWeitiaoSelected(3);
                weitiaoValue = "04";
                cmd = sendPrefix + weitiaoValue + "00";
                sendBlueCmd(cmd);
                break;
            case R.id.view_yujia:
                setWeitiaoSelected(4);
                weitiaoValue = "0C";
                cmd = sendPrefix + weitiaoValue + "00";
                sendBlueCmd(cmd);
                break;
            case R.id.view_10time:
                setTimeSelected(0);
                timeValue = "00";
                break;
            case R.id.view_20time:
                setTimeSelected(1);
                timeValue = "01";
                break;
            case R.id.view_30time:
                setTimeSelected(2);
                timeValue = "02";
                break;
            case R.id.ll_plus:
                if (upperValue >= 8) {
                    upperValue = 5;
                } else {
                    upperValue++;
                }
                tvPlus.setText(String.valueOf(upperValue));
                break;
            case R.id.ll_minus:
                if (lowerValue >= 4) {
                    lowerValue = 1;
                } else {
                    lowerValue++;
                }
                tvMinus.setText(String.valueOf(lowerValue));
                break;
            case R.id.tvConfirm:
                cmd = "FFFFFFFFFF14030E00" + weitiaoValue + BlueUtils.covert10TO16(upperValue * 10) + "00" + BlueUtils.covert10TO16(lowerValue * 10) + "00" + timeValue + "000000";
                sendBlueCmd(cmd);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 启动蓝牙service
        Intent blueServiceIntent = new Intent(AnmoSetActivity.this, BluetoothLeService.class);
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
            String cmd = "FFFFFFFFFF0D0208000300";
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
        cmd = cmd + BlueUtils.crc16Modbus(cmd);
        Log.i(TAG, "sendBlueCmd: " + cmd);
        // 判断蓝牙是否连接
        if (!BlueUtils.isConnected()) {
            ToastUtils.showToast(AnmoSetActivity.this, getString(R.string.device_no_connected));
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
        if (cmd.indexOf("FFFFFFFFFF14020801") > -1) {//询问状态回复
            //按摩模式
            String modeStatus = cmd.substring(18, 20).toUpperCase();
            if (TextUtils.equals(modeStatus, "03")) {//全身按摩
                weitiaoMode = 0;
                weitiaoValue = "03";
            } else if (TextUtils.equals(modeStatus, "12")) {//背部按摩
                weitiaoMode = 1;
                weitiaoValue = "12";
            } else if (TextUtils.equals(modeStatus, "05")) {//腰部按摩
                weitiaoMode = 2;
                weitiaoValue = "05";
            } else if (TextUtils.equals(modeStatus, "04")) {//颈部按摩
                weitiaoMode = 3;
                weitiaoValue = "04";
            } else if (TextUtils.equals(modeStatus, "0C")) {//瑜伽
                weitiaoMode = 4;
                weitiaoValue = "0C";
            }

            //按摩强度
            String upperStatus = cmd.substring(22, 24).toUpperCase() + cmd.substring(20, 22).toUpperCase();
            String lowerStatus = cmd.substring(26, 28).toUpperCase() + cmd.substring(24, 26).toUpperCase();

            upperValue = BlueUtils.covert16TO10(upperStatus) / 10;
            lowerValue = BlueUtils.covert16TO10(lowerStatus) / 10;

            //按摩时间
            String timeStatus = cmd.substring(28, 30).toUpperCase();
            if (TextUtils.equals(timeStatus, "00")) {
                timeMode = 0;
                timeValue = "00";
            } else if (TextUtils.equals(timeStatus, "01")) {
                timeMode = 1;
                timeValue = "01";
            } else if (TextUtils.equals(timeStatus, "02")) {
                timeMode = 2;
                timeValue = "02";
            }


            setWeitiaoSelected(weitiaoMode);
            setTimeSelected(timeMode);
            tvPlus.setText(String.valueOf(upperValue));
            tvMinus.setText(String.valueOf(lowerValue));

        } else if (cmd.indexOf("FFFFFFFFFF14030E01") > -1) {//设置成功
            ToastUtils.showToast(this, getResources().getString(R.string.success));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 500);
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
                ToastUtils.showToast(AnmoSetActivity.this, R.string.device_disconnect);
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