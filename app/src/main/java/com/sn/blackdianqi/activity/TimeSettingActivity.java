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

import androidx.annotation.Nullable;

import com.github.gzuliyujiang.wheelpicker.TimePicker;
import com.github.gzuliyujiang.wheelpicker.annotation.TimeMode;
import com.github.gzuliyujiang.wheelpicker.contract.OnTimePickedListener;
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity;
import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.bean.LengNuanTimeBean;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.TranslucentActionBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeSettingActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    public static final String TAG = "TimeSettingActivity";
    public static int MODE_REQUEST_CODE = 106;
    public static int GEAR_REQUEST_CODE = 107;
    // 特征值
    protected BluetoothGattCharacteristic characteristic;

    public String hourStr = "00";
    public String minuteStr = "00";
    private String modeCode = "01";
    private int gear = 0;
    private LengNuanTimeBean lengNuanTimeBean;

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.ll_mode)
    LinearLayout modeLL;
    @BindView(R.id.tv_mode)
    TextView tvMode;
    @BindView(R.id.ll_gear)
    LinearLayout gearLL;
    @BindView(R.id.tv_gear)
    TextView tvGear;
    @BindView(R.id.ll_time)
    LinearLayout timeLL;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_save)
    LinearLayout llSave;

    private List<String> hotGearList = new ArrayList<>();
    private List<String> coolGearList = new ArrayList<>();

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
        Intent blueServiceIntent = new Intent(TimeSettingActivity.this, BluetoothLeService.class);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_time_setting);
        ButterKnife.bind(this);
        actionBar.setData(getResources().getString(R.string.time_set), R.mipmap.ic_back, null, 0, "", this);
        actionBar.setStatusBarHeight(getStatusBarHeight());

        initView();

        lengNuanTimeBean = Prefer.getInstance().getLengNuanTime(Prefer.getInstance().getLatelyConnectedDevice());
        if (lengNuanTimeBean != null) {
            modeCode = lengNuanTimeBean.getMode();
            tvMode.setText(getModeStrByCode(modeCode));

            String gear16 = lengNuanTimeBean.getGear();
            gear = BlueUtils.covert16TO10(gear16);
            tvGear.setText(getGearStrByCode(gear));

            String hourStr = lengNuanTimeBean.getHour();
            String minuteStr = lengNuanTimeBean.getMins();

            tvTime.setText(hourStr + ":" + minuteStr);
        }
    }

    private void initView() {
        modeLL.setOnClickListener(this);
        gearLL.setOnClickListener(this);
        timeLL.setOnClickListener(this);
        llSave.setOnClickListener(this);

        hotGearList.add("30°c");
        hotGearList.add("35°c");
        hotGearList.add("40°c");
        hotGearList.add("45°c");

        coolGearList.add("20°c");
        coolGearList.add("15°c");
        coolGearList.add("10°c");
        coolGearList.add("5°c");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_mode:
                Intent intentMode = new Intent(this, Mode3Activity.class);
                intentMode.putExtra("modeCode", modeCode);
                startActivityForResult(intentMode, MODE_REQUEST_CODE);
                break;
            case R.id.ll_gear:
                Intent intentGear = new Intent(this, GearActivity.class);
                intentGear.putExtra("modeCode", modeCode);
                intentGear.putExtra("gear", gear);
                startActivityForResult(intentGear, GEAR_REQUEST_CODE);
                break;
            case R.id.ll_time:
                TimePicker picker = new TimePicker(this);
                picker.getCancelView().setText(R.string.dialog_cancel);
                picker.getOkView().setTextColor(getResources().getColor(R.color.text_green));
                picker.getOkView().setText(getText(R.string.dialog_confirm));
                picker.getWheelLayout().setTimeMode(TimeMode.HOUR_24_NO_SECOND);
                picker.getWheelLayout().setRange(TimeEntity.target(0, 0, 0), TimeEntity.target(23, 59, 59));
                TimeEntity timeEntity = TimeEntity.now();

                timeEntity.setHour(Integer.parseInt(hourStr));
                timeEntity.setMinute(Integer.parseInt(minuteStr));
                picker.getWheelLayout().setDefaultValue(timeEntity);

                picker.setOnTimePickedListener(new OnTimePickedListener() {
                    @Override
                    public void onTimePicked(int hour, int minute, int second) {
                        if (hour < 10) {
                            hourStr = "0" + hour;
                        } else {
                            hourStr = hour + "";
                        }
                        if (minute < 10) {
                            minuteStr = "0" + minute;
                        } else {
                            minuteStr = minute + "";
                        }
                        tvTime.setText(hourStr + ":" + minuteStr);
                    }
                });
                picker.show();
                break;
            case R.id.ll_save:
                String cmd = "FFFFFFFFFE1400020000" + hourStr + minuteStr + "00" + modeCode + "0" + gear + "000000";
                cmd = cmd + BlueUtils.crc16Modbus(cmd);
                sendBlueCmd(cmd);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (MODE_REQUEST_CODE == requestCode && resultCode == Mode3Activity.RESULT_CODE) {
            modeCode = data.getStringExtra(Mode3Activity.EXTRA_KEY);
            tvMode.setText(getModeStrByCode(modeCode));
            tvGear.setText(getGearStrByCode(gear));
        } else if (GEAR_REQUEST_CODE == requestCode && resultCode == GearActivity.RESULT_CODE) {
            gear = data.getIntExtra(GearActivity.EXTRA_KEY, 0);
            tvGear.setText(getGearStrByCode(gear));
        }
    }

    //模式选择
    public String getModeStrByCode(String mode) {
        if (mode.equals("01")) {
            return getResources().getString(R.string.jiare);
        }
        if (mode.equals("02")) {
            return getResources().getString(R.string.zhileng);
        }
        return getString(R.string.alarm_mode_budongzuo);
    }

    //档位选择
    public String getGearStrByCode(int gear) {
        if (TextUtils.equals(modeCode, "01")) {//制热
         return    hotGearList.get(gear-1);
        } else {//制冷
            return  coolGearList.get(gear - 1);
        }

//        String gearStr = "";
//        if (gear == 1) {
//            return getResources().getString(R.string.dangwei1);
//        }
//        if (gear == 2) {
//            return getResources().getString(R.string.dangwei2);
//        }
//        if (gear == 3) {
//            return getResources().getString(R.string.dangwei3);
//        }
//        if (gear == 4) {
//            return getResources().getString(R.string.dangwei4);
//        }
//        return gearStr;
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
            ToastUtils.showToast(TimeSettingActivity.this, getString(R.string.device_no_connected));
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
        if (cmd.indexOf("FFFFFFFFFE14000201") > -1) {//設置定时数据回复
            ToastUtils.showToast(this, getResources().getString(R.string.success));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 500);
//            //冷暖合并询问码
//            cmd = "FFFFFFFFFE1000000000000000AA";
//            cmd = cmd + BlueUtils.crc16Modbus(cmd);
//            sendBlueCmd(cmd);
        }
//        else if (cmd.contains("FFFFFFFFFE14000001")) {//询问状态回复
//            cmd = cmd.toUpperCase().replaceAll(" ", "");
//
//            String timerHour = cmd.substring(22, 24);//若已开启定时，则表示设定的小时位，0~23
//            String timerMin = cmd.substring(24, 26);//若已开启定时，则表示设定的分钟位，0~59
//            String workMode = cmd.substring(26, 28);//设置的定时工作模式00：无01：加热02：制冷
//            String workGear = cmd.substring(28, 30);//设置的定时工作挡位0~4：挡位
//
//            LengNuanTimeBean lengNuanTimeBean = new LengNuanTimeBean();
//            lengNuanTimeBean.setHour(timerHour);
//            lengNuanTimeBean.setMins(timerMin);
//            lengNuanTimeBean.setMode(workMode);
//            lengNuanTimeBean.setGear(workGear);
//
//            Prefer.getInstance().setLengNuanTime(Prefer.getInstance().getLatelyConnectedDevice(), lengNuanTimeBean);
//
//            ToastUtils.showToast(this, "设置成功");
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    finish();
//                }
//            }, 500);
//        }
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
                ToastUtils.showToast(TimeSettingActivity.this, R.string.device_disconnect);
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