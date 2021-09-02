package com.sn.blackdianqi.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
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
import com.sn.blackdianqi.view.TranslucentActionBar;

import net.frakbot.jumpingbeans.JumpingBeans;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sn.blackdianqi.MyApplication.HEART_RATE_MEASUREMENT;


/**
 * 蓝牙搜索连接界面
 */
public class ConnectActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener {

    private final static String TAG = "ConnectActivity";

    private static final long DURATION_MILL = 5000L;
    //
    private static final int MSG_STOP_SCAN = 102;
    //
    private static final int MSG_CONNECT_STATUS = 103;
    // 发现服务并设置特征值
    private static final int MSG_GATT_SERVICE_DISCOVERY = 104;

    // 从哪个页面进入 main 首页 /set 设置
    protected String mFrom = "";
    // 是否是第一次扫描
    protected boolean isFirstScan = false;

    protected List<String> blueNameList;


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
    // 蓝牙特征值
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics;

    // 加载中对话框
    private WaitDialog mWaitDialog;

    private ConnectHandler mConnectHandler;

    private DeviceBean mSelectedDeviceBean;

    // 当前搜索状态
    private boolean mScanning;
    // 是否是连接前断开中
    private boolean isPreConnectDisconnecting = false;


    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {
        // do nothing
        Intent intent = new Intent(ConnectActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onDestroy() {
        LogUtils.e(TAG, "执行ConnectActivity onDestroy方法");
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ButterKnife.bind(this);
        blueNameList = defindeBlueNameList();
        // 设置title
        mFrom = getIntent().getStringExtra("from");
        titleBar.setData(getString(R.string.blue_equipment), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            titleBar.setStatusBarHeight(getStatusBarHeight());
        }

        initView();

        mConnectHandler = new ConnectHandler(this);
        mWaitDialog = new WaitDialog(this);

        // 启动蓝牙service
        Intent blueServiceIntent = new Intent(ConnectActivity.this, BluetoothLeService.class);
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

            if (isConnected()) {
                // 如果当前是连接状态
                DeviceBean deviceBean = Prefer.getInstance().getConnectedDevice();
                if (deviceBean != null) {
                    mBlueDeviceListAdapter.addDevice(deviceBean);
                }
            }
            if (RunningContext.checkLocationPermission(ConnectActivity.this, true)) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            if (mScanning) {
                LogUtils.e(TAG, "==停止扫描蓝牙设备==", "stoping................");
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                textViewTry.setText(getString(R.string.search_blue_equipment));
            }
        }
    }

    /**
     * 去连接
     */
    private void connect() {
        // Automatically connects to the device upon successful start-up
        // initialization.
        // 先停止搜索蓝牙
        scanBlue(false);
        // 根据蓝牙地址，连接设备
        LogUtils.e(TAG, "==根据蓝牙地址，连接设备==", "开始连接目标设备");
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
        if (isConnected()) {
            mBluetoothLeService.disconnect();
            isPreConnectDisconnecting = true;
        } else {
            mBluetoothLeService.close();
            mBluetoothLeService.connect(mSelectedDeviceBean.getAddress());
        }

    }


    /**
     * 断开连接
     */
    private void disConnect() {
        showDialog(getString(R.string.dialog_title_disconnect),
                getString(R.string.dialog_negetive), getString(R.string.dialog_positive),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 断开连接
                        mBluetoothLeService.disconnect();
                    }
                });
    }


    /**
     *
     */
    private static class ConnectHandler extends Handler {

        private WeakReference<ConnectActivity> reference;

        public ConnectHandler(ConnectActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ConnectActivity activity = reference.get();
            if (activity == null || activity.isFinishing()) {
                LogUtils.i(TAG, "ConnectActivity 已被销毁");
                return;
            }
            switch (msg.what) {
                case MSG_STOP_SCAN:
                    // 停止扫描
                    activity.scanBlue(false);
                    break;
                case MSG_CONNECT_STATUS:
                    activity.mBlueDeviceListAdapter.notifyDataSetChanged();
                    break;
                case MSG_GATT_SERVICE_DISCOVERY:
                    if (activity.mFrom.equals("main")) {
                        // 只有首页过来才跳转
                        if (activity.mSelectedDeviceBean.isConnected()) {
                            Intent intent = new Intent(activity, HomeActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    } else if (activity.mFrom.equals("set")) {
                        // 只有首页过来才跳转
                        if (activity.mSelectedDeviceBean.isConnected()) {
                            Intent intent = new Intent(activity, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }
                    break;
            }
        }
    }


    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSelectedDeviceBean = (DeviceBean) mBlueDeviceListAdapter.getItem(position);
            if (mSelectedDeviceBean.isConnected()) {
                // 断开连接
                disConnect();
            } else {
                // 去连接
                connect();
            }
        }
    };


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


    /**
     * 蓝牙扫描回调函数 实现扫描蓝牙设备，回调蓝牙BluetoothDevice，可以获取name MAC等信息
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (device != null) {
                        LogUtils.i(TAG, "搜索到蓝牙设备信息：" + device.getName()+" "+device.getAddress());
                        // 发送给客户时需要加上
                        String deviceName = device.getName();
                        if (TextUtils.isEmpty(deviceName)) {
                            return;
                        }
                        if (!isContain(deviceName)) {
                            return;
                        }
                        LogUtils.e(TAG, "过滤到蓝牙设备信息：" + device.getName()+" "+device.getAddress());
                        String latelyConnectedDevice = Prefer.getInstance().getLatelyConnectedDevice();
                        if (device.getAddress().equals(latelyConnectedDevice)) {
                            if (("main").equals(mFrom) && isFirstScan) {
                                // 如果是从首页第一次进入，并且扫描到之前连接过的设备，则自动连接
                                mSelectedDeviceBean = mBlueDeviceListAdapter.addDevice(device, false);
                                if (mSelectedDeviceBean != null) {
                                    connect();
                                    return;
                                }
                            }
                        }
                        mBlueDeviceListAdapter.addDevice(device, false);
                    }
                }
            });
        }
    };


    private boolean isConnected() {
        return BlueUtils.isConnected();
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
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))//Gatt连接成功
            {
                LogUtils.e(TAG, "==更新连接状态 已连接==");
                //更新连接状态
                mSelectedDeviceBean.setConnected(true);
                Prefer.getInstance().setLatelyConnectedDevice(mSelectedDeviceBean.getAddress());
                Prefer.getInstance().setBleStatus("已连接", mSelectedDeviceBean);
//                mWaitDialog.dismiss();
                mConnectHandler.sendEmptyMessage(MSG_CONNECT_STATUS);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) { //Gatt连接失败
                boolean closeDialog = true;
                mBluetoothLeService.close();
                List<DeviceBean> list = mBlueDeviceListAdapter.getDeviceList();
                if (list != null && !list.isEmpty()) {
                    for (DeviceBean deviceBean : list) {
                        deviceBean.setConnected(false);
                        if (mSelectedDeviceBean != null && mSelectedDeviceBean.getAddress().equals(deviceBean.getAddress())) {
                            mSelectedDeviceBean = deviceBean;
                        }
                    }
                    mBlueDeviceListAdapter.notifyDataSetChanged();
                }
                String address  = Prefer.getInstance().getLatelyConnectedDevice();
                LogUtils.e(TAG, "==更新连接状态 断开连接==");
                Prefer.getInstance().setLatelyConnectedDevice("");
                Prefer.getInstance().setBleStatus("未连接", null);
                if (isPreConnectDisconnecting) {
                    // 成功断开后连接
                    isPreConnectDisconnecting = false;
                    if (mSelectedDeviceBean != null &&  !mSelectedDeviceBean.getAddress().equals(address)) {
                        // 如果当前选中的和当前连接的不是同一个,重新发起连接
                        mBluetoothLeService.connect(mSelectedDeviceBean.getAddress());
                        closeDialog = false;
                    }
                }
                if (closeDialog) {
                    mWaitDialog.dismiss();
                }
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) { //发现GATT服务器
                // Show all the supported services and characteristics on the
                // user interface.
                mWaitDialog.dismiss();
                LogUtils.i("==获取设备的所有蓝牙服务==", "" + mBluetoothLeService.getSupportedGattServices());
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                mConnectHandler.sendEmptyMessage(MSG_GATT_SERVICE_DISCOVERY);
            }
        }
    };


    /**
     * @param
     * @return void
     * @throws
     * @Title: displayGattServices
     * @Description: TODO(处理蓝牙服务)
     */
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        String unknownServiceString = "unknown_service";
        String unknownCharaString = "unknown_characteristic";
        // 服务数据,可扩展下拉列表的第一级数据
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        // 特征数据（隶属于某一级服务下面的特征值集合）
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
        // 部分层次，所有特征值集合
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            // 获取服务列表
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            // 查表，根据该uuid获取对应的服务名称。SampleGattAttributes这个表需要自定义。
            gattServiceData.add(currentServiceData);
            LogUtils.e("=获取服务列表中 Service Uuid==", "" + uuid + " instanceid=" + gattService.getInstanceId() + " type=" + gattService.getType());
            // 从当前循环所指向的服务中读取特征值列表
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            // Loops through available Characteristics.
            // 对于当前循环所指向的服务中的每一个特征值
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                LogUtils.e("=获取特征值列表中 chara Uuid==", "" + uuid + " Properties=" + gattCharacteristic.getProperties());
                uuid = gattCharacteristic.getUuid().toString();
                if (gattCharacteristic.getUuid().toString().equals(HEART_RATE_MEASUREMENT)) {
                    MyApplication.getInstance().mBluetoothLeService = mBluetoothLeService;
                    MyApplication.getInstance().gattCharacteristic = gattCharacteristic;

                    LogUtils.e("==蓝牙特征值1==", "" + MyApplication.getInstance().gattCharacteristic.toString());
                    // 接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);

                    // 设置数据内容
                    // 往蓝牙模块写入数据
                    mBluetoothLeService.writeCharacteristic(gattCharacteristic);
                }
                List<BluetoothGattDescriptor> descriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor descriptor : descriptors) {
                    System.out.println("---descriptor UUID:" + descriptor.getUuid());
                    // 获取特征值的描述
                    mBluetoothLeService.getCharacteristicDescriptor(descriptor);
                }
            }
        }
    }



    private List<String> defindeBlueNameList() {
        List<String> blueNameList = new ArrayList<>();
        blueNameList.add("QMS-IQ");
        blueNameList.add("QMS-I06");
        blueNameList.add("QMS-L04");
        blueNameList.add("QMS-LQ");

        blueNameList.add("QMS-JQ-D");
        blueNameList.add("QMS4");

        blueNameList.add("QMS-NQ");
        blueNameList.add("QMS3");

        blueNameList.add("QMS-MQ");
        blueNameList.add("QMS2");

        blueNameList.add("QMS-KQ-H");
        blueNameList.add("QMS-H02");

        blueNameList.add("QMS-DFQ");
        blueNameList.add("QMS-430");
        blueNameList.add("QMS-444");

        blueNameList.add("QMS-DQ");
        blueNameList.add("QMS-443");

        blueNameList.add("S3-2");
        blueNameList.add("S3-3");
        blueNameList.add("S3-4");

        return blueNameList;
    }


    private boolean isContain(String blueName) {
        if (TextUtils.isEmpty(blueName)) {
            return false;
        }
        if (blueNameList == null || blueNameList.size() <= 0) {
            blueNameList = defindeBlueNameList();
        }
        for (String item : blueNameList) {
            if (blueName.contains(item)) {
                return true;
            }
        }
        return false;
    }


}
