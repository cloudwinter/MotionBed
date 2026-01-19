package com.sn.blackdianqi.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.adapter.BlueDeviceListAdapter;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.dialog.WaitDialog;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.CountDownTimerUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.TranslucentActionBar;

import net.frakbot.jumpingbeans.JumpingBeans;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectMcuActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener {

    private final static String TAG = "ConnectMcuActivity";

    private static final long DURATION_MILL = 5000L;
    //
    private static final int MSG_STOP_SCAN = 102;

    // 是否是第一次扫描
    protected boolean isFirstScan = false;

    @BindView(R.id.actionbar)
    TranslucentActionBar titleBar;
    @BindView(R.id.tv_try)
    TextView textViewTry;
    @BindView(R.id.tv_connect_time)
    TextView textViewConnectTime;
    @BindView(R.id.lv)
    ListView listView;

    // 自定义Adapter
    private BlueDeviceListAdapter mBlueDeviceListAdapter;

    //蓝牙service,负责后台的蓝牙服务
    private BluetoothLeService mBluetoothLeService;
    // 蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    // 蓝牙扫描
    private BluetoothLeScanner mBluetoothLeScanner;
    // 蓝牙特征值
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics;

    // 加载中对话框
    private WaitDialog mWaitDialog;
    private ConnectHandler mConnectHandler;

    private DeviceBean mSelectedDeviceBean;

    // 当前搜索状态
    private boolean mScanning;
    private String deviceType;//搜索的设备类型

    // 特征值
    protected BluetoothGattCharacteristic characteristic;

    @Override
    public void onLeftClick() {
        scanBlue(false);
        finish();
    }

    @Override
    public void onRightClick() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_mcu);
        ButterKnife.bind(this);
        titleBar.setData(getString(R.string.blue_equipment), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            titleBar.setStatusBarHeight(getStatusBarHeight());
        }
        deviceType = getIntent().getStringExtra("deviceType");

        initView();

        mConnectHandler = new ConnectHandler(this);
        mWaitDialog = new WaitDialog(this);

        // 启动蓝牙service
        Intent blueServiceIntent = new Intent(ConnectMcuActivity.this, BluetoothLeService.class);
        startService(blueServiceIntent);
        bindService(blueServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // 启动扫描
        isFirstScan = true;
        scanBlue(true);
    }

    private void initView() {
        textViewTry.setOnClickListener(mSearchBlueClickListener);

        mBlueDeviceListAdapter = new BlueDeviceListAdapter(this);
        listView.setAdapter(mBlueDeviceListAdapter);
        listView.setOnItemClickListener(mItemClickListener);

        // 获取手机本地的蓝牙适配器
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

    }

    /**
     * 蓝牙搜索
     *
     * @param enable true开始，false停止
     */
    private void scanBlue(boolean enable) {
        if (enable) {
            LogUtils.e(TAG, "==开始扫描蓝牙设备==", "begin.....................");
            // 5S后停止
            mConnectHandler.sendEmptyMessageDelayed(MSG_STOP_SCAN, DURATION_MILL);
            // 倒计时
            CountDownTimerUtils countDownTimer = new CountDownTimerUtils(textViewConnectTime, DURATION_MILL, 1000L);
            countDownTimer.start();
            mScanning = true;
            textViewTry.setText(getString(R.string.searching));
            JumpingBeans.with(textViewTry).appendJumpingDots().build();

            if (RunningContext.checkLocationPermission(ConnectMcuActivity.this, true)) {
                List<ScanFilter> filters = new ArrayList<ScanFilter>();
                //这里使用的SEARCH_SERVICE_UUID可以向蓝牙芯片厂商获取
//                filters.add(0, new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID.fromString(MyApplication.HEART_RATE_MEASUREMENT))).build());
                ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (mScanning) {
                LogUtils.e(TAG, "==停止扫描蓝牙设备==", "stoping................");
                mScanning = false;
                if (RunningContext.checkLocationPermission(ConnectMcuActivity.this, true)) {
                    mBluetoothLeScanner.stopScan(mScanCallback);
                }
                textViewTry.setText(getString(R.string.search_blue_equipment));
            }
        }
    }


    /**
     *
     */
    private static class ConnectHandler extends Handler {

        private WeakReference<ConnectMcuActivity> reference;

        public ConnectHandler(ConnectMcuActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ConnectMcuActivity activity = reference.get();
            if (activity == null || activity.isFinishing()) {
                LogUtils.i(TAG, "ConnectActivity 已被销毁");
                return;
            }
            switch (msg.what) {
                case MSG_STOP_SCAN:
                    // 停止扫描
                    activity.scanBlue(false);
                    break;
            }
        }
    }


    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSelectedDeviceBean = (DeviceBean) mBlueDeviceListAdapter.getItem(position);
            // 先停止搜索蓝牙
            scanBlue(false);
            // 根据蓝牙地址，连接设备
            LogUtils.e(TAG, "==连接MCU设备==");
            //启动连接动画
            mWaitDialog.setCanceledOnTouchOutside(true);
            mWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    LogUtils.e(TAG, "mWaitDialog 执行 onCancel");
                    mBluetoothLeService.close();
                }
            });
            mWaitDialog.show();

            String deviceId = mSelectedDeviceBean.getAddress().replaceAll(":","") .toUpperCase();
            LogUtils.e("连接的MCU设备", deviceId);
            // APP/小程序下发下位设备的MAC地址
            String cmd = "FFFFFFFF01002814" + deviceType + deviceId + "000000";
            cmd = cmd.toUpperCase();
            cmd = cmd + BlueUtils.makeChecksum(cmd);
            sendBlueCmd(cmd);//发送询问状态
        }
    };

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
            ToastUtils.showToast(ConnectMcuActivity.this, getString(R.string.device_no_connected));
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
     * 蓝牙搜索按钮点击事件
     */
    private View.OnClickListener mSearchBlueClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mScanning) {
                isFirstScan = false;
                // 搜索之前需要清除之前的数据
                mBlueDeviceListAdapter.clear();
                scanBlue(true);
            }
        }
    };


    private ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            LogUtils.i(TAG, "扫描的设备信息：" + result);
            List<ScanResult> list = new ArrayList();
            list.add(result);
            handleScanResult(list);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            handleScanResult(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            LogUtils.e(TAG, "蓝牙扫描设备失败：" + errorCode);
        }
    };

    /**
     * 处理扫描到的设备
     *
     * @param scanResultList
     */
    private void handleScanResult(List<ScanResult> scanResultList) {
        if (RunningContext.checkLocationPermission(ConnectMcuActivity.this, true)) {
            for (ScanResult scanResult : scanResultList) {
                BluetoothDevice device = scanResult.getDevice();
                String deviceName = device.getName();
                if (TextUtils.isEmpty(deviceName)) {
                    return;
                }
                if (!isContain(deviceName)) {
                    return;
                }
                LogUtils.e(TAG, "过滤到蓝牙设备信息：" + deviceName);
                addDevice(device, deviceName);
            }
        }
    }


    private void addDevice(final BluetoothDevice device, final String deviceName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBlueDeviceListAdapter.addDevice(device, false);
            }
        });
    }


    /**
     * 处理回码逻辑
     *
     * @param cmd
     */
    private void handleReceiveData(String cmd) {
        cmd = cmd.toUpperCase().replaceAll(" ", "");
        if (cmd.contains("FFFFFFFF01002714")) {//询问状态回复
            mWaitDialog.dismiss();
            Log.e("回复cmd:", cmd);
            Intent intent = new Intent(ConnectMcuActivity.this, HomeMcuActivity.class);
            intent.putExtra("cmd", cmd);
            intent.putExtra("deviceType", deviceType);
            startActivity(intent);
            finish();
        } else if (cmd.contains("FFFFFFFF01002914")) {//连接mcu设备回码
            // APP/小程序 询问总控板当前连接状态
             cmd = "FFFFFFFF010026140F000000000000000000";
            cmd = cmd + BlueUtils.makeChecksum(cmd);
            sendBlueCmd(cmd);//发送询问状态
        }
    }

    /**
     * 允许扫描到的设备类型
     *
     * @param blueName
     * @return
     */
    private boolean isContain(String blueName) {
        if (TextUtils.isEmpty(blueName)) {
            return false;
        }
        if (TextUtils.isEmpty(deviceType)) {
            return false;
        }
        if (TextUtils.equals(deviceType, "0A")) {
            if (blueName.contains("TL-B")) {
                return true;
            }
        } else if (TextUtils.equals(deviceType, "0B")) {
            if (blueName.contains("TL-A")) {
                return true;
            }
        } else if (TextUtils.equals(deviceType, "0C")) {
            if (blueName.contains("TL-W")) {
                return true;
            }
        }
        return false;
    }

    /* BluetoothLeService绑定的回调函数 */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            LogUtils.d(TAG, "BluetoothLeService 已启动");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            MyApplication.getInstance().
                    mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                LogUtils.e("找不到蓝牙", "Unable to initialize Bluetooth");
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtils.i(TAG, "BluetoothLeService 已断开");
        }
    };

    /* 意图过滤器 */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
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
                ToastUtils.showToast(ConnectMcuActivity.this, R.string.device_disconnect);
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
        LogUtils.e(TAG, "执行ConnectActivity onDestroy方法");
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}