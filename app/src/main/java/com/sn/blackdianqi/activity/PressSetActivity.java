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
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.adapter.PressSetAdapter;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.bean.AskStatusgeEvent;
import com.sn.blackdianqi.bean.PressBean;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.TranslucentActionBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PressSetActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {
    public static final String TAG = "PressSetActivity";

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.rvList)
    RecyclerView rvList;
    @BindView(R.id.ivPlus)
    ImageView ivPlus;
    @BindView(R.id.ivMinus)
    ImageView ivMinus;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;

    private List<PressBean> pressList = new ArrayList<>();
    private PressSetAdapter pressSetAdapter;

    // 特征值
    protected BluetoothGattCharacteristic characteristic;

    private int isAutoSave = -1;//是否自动保存
    private boolean isFinish;//是否关闭当前界面

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (isAutoSave >= 3) {
                StringBuilder sb = new StringBuilder();
                sb.append("FFFFFFFFFF2F030500");
                List<PressBean> data = pressSetAdapter.getData();
                for (int i = 0; i < data.size(); i++) {
                    sb.append("01");
                    sb.append(BlueUtils.covert10TO16(data.get(i).getValue() * 10));
                    sb.append("00");
                }
                String cmd = sb.toString();
                cmd = cmd + BlueUtils.crc16Modbus(cmd);
                LogUtils.e("cmd", cmd);
                sendBlueCmd(cmd);//发送设置气囊压力值
                isFinish = false;
            } else {
                if (isAutoSave >= 0) {
                    isAutoSave++;
                }
            }
            mHandler.sendEmptyMessageDelayed(1, 1000);
        }
    };


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
        setContentView(R.layout.activity_press_set);
        ButterKnife.bind(this);
        actionBar.setData(null, R.mipmap.ic_back, null, 0, "", this);
        actionBar.setStatusBarHeight(getStatusBarHeight());

        initData();
        initView();

        mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    private void initData() {
        for (int i = 0; i < 12; i++) {
            PressBean pressBean = new PressBean();
            pressBean.setName(String.valueOf(i + 1));
            pressList.add(pressBean);
        }
    }

    private void initView() {
        rvList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        pressSetAdapter = new PressSetAdapter(this, pressList);
        rvList.setAdapter(pressSetAdapter);

        pressSetAdapter.setOnItemClickListener(new PressSetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                pressSetAdapter.setSelectIndex(position);
            }
        });

        ivPlus.setOnClickListener(this);
        ivMinus.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int selectIndex = pressSetAdapter.getSelectIndex();
        int value = pressSetAdapter.getSelectValue();
        switch (view.getId()) {
            case R.id.ivMinus:
                isAutoSave = 0;
                if (value > 0) {
                    value--;
                }
                pressSetAdapter.setSelectValue(selectIndex, value);
                break;
            case R.id.ivPlus:
                isAutoSave = 0;
                if (value < 9) {
                    value++;
                }
                pressSetAdapter.setSelectValue(selectIndex, value);
                break;
            case R.id.tvConfirm:
                isFinish = true;
                StringBuilder sb = new StringBuilder();
                sb.append("FFFFFFFFFF2F030500");
                List<PressBean> data = pressSetAdapter.getData();
                for (int i = 0; i < data.size(); i++) {
                    sb.append("01");
                    sb.append(BlueUtils.covert10TO16(data.get(i).getValue() * 10));
                    sb.append("00");
                }
                String cmd = sb.toString();
                cmd = cmd + BlueUtils.crc16Modbus(cmd);
                LogUtils.e("cmd", cmd);
                sendBlueCmd(cmd);//发送设置气囊压力值
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 启动蓝牙service
        Intent blueServiceIntent = new Intent(PressSetActivity.this, BluetoothLeService.class);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void askStatus() {
        try {
            Thread.sleep(500L);
            String cmd = "FFFFFFFFFF0B020400";
            cmd = cmd + BlueUtils.crc16Modbus(cmd);
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
            ToastUtils.showToast(PressSetActivity.this, getString(R.string.device_no_connected));
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
        if (cmd.indexOf("FFFFFFFFFF2F020401") > -1) {//询问状态回复
            if (cmd.length() == 94) {
                String result = cmd.substring(18, 90);

                List<PressBean> pressureList = pressSetAdapter.getData();

                List<String> resArray = BlueUtils.strToArray(result, 6);

                for (int i = 0; i < resArray.size(); i++) {
                    String item = resArray.get(i);
                    int value = BlueUtils.covert16TO10(item.substring(4, 6) + item.substring(2, 4));
                    pressureList.get(i).setValue(value / 10);
//                    LogUtils.e("pressValue", pressureList.get(i).getValue() + "");
                }
                pressSetAdapter.setData(pressureList);
            }
        } else if (cmd.indexOf("FFFFFFFFFF2F030501") > -1) {//设置成功
            ToastUtils.showToast(this, getResources().getString(R.string.success));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isFinish) {
                        finish();
                    }
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
                ToastUtils.showToast(PressSetActivity.this, R.string.device_disconnect);
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