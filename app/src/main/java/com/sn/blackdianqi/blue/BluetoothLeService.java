package com.sn.blackdianqi.blue;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


import androidx.core.app.ActivityCompat;

import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;

import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {
    private final static String TAG = "BluetoothLeService";
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    //
    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

    /**
     * <>蓝牙回调处理</>
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (!checkBluePermission()) {
                return;
            }
            //连接成功
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                /* 通过广播更新连接状态 */
                broadcastUpdate(ACTION_GATT_CONNECTED);
                LogUtils.e("==广播更新连接状态==", "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                mBluetoothGatt.discoverServices();
                LogUtils.e("==尝试启动服务发现==", "Attempting to start service discovery:");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //连接失败
                //在每次连接服务的时候， 或则断开后及时把之前的服务关闭掉
                LogUtils.e("==广播更新连接状态==", "Connected to GATT server. STATE_DISCONNECTED ");
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt.close();
                }
                LogUtils.e("==连接gatt服务连接失败==", "Disconnected from GATT server.");
                //连接gatt服务连接失败
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // MTU 修改成功
                LogUtils.i("onMtuChanged", "New MTU size: " + mtu);
            } else {
                // 处理错误
                LogUtils.e("onMtuChanged", "Failed to change MTU");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                LogUtils.e(TAG + "==onServicesDiscovered，未发现蓝牙服务===1111", "onServicesDiscovered received: " + status);
                return;
            }
            if (!checkBluePermission()) {
                return;
            }
            mBluetoothGatt.requestMtu(512);
            //发现到服务
            LogUtils.e(TAG + "==onServicesDiscovered，发现蓝牙服务gatt===2222", "--onServicesDiscovered called--");
            broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
        }

        // 特征值的读
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                return;
            }
            //从特征值读取数据
            byte[] data = characteristic.getValue();
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
            LogUtils.e("BluetoothLeService onCharacteristicRead ==特征值的读回调==", stringBuilder.toString());
            //将数据通过广播到Ble_Activity
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        // 特征值的改变
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
            LogUtils.e("BluetoothLeService onCharacteristicChanged ==接收到硬件返回的数据==", stringBuilder.toString());
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        // 特征值的写
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            byte[] data = characteristic.getValue();
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
            LogUtils.e("BluetoothLeService onCharacteristicWrite ==发送到硬件的数据==", stringBuilder.toString());
            //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        // 读描述值
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        }

        // 写描述值
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        }

        // 读写蓝牙信号值
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.w(TAG, "--onReadRemoteRssi--: " + status);
            broadcastUpdate(ACTION_DATA_AVAILABLE, rssi);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            Log.w(TAG, "--onReliableWriteCompleted--: " + status);
        }

    };

    //广播意图
    private void broadcastUpdate(final String action, int rssi) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, String.valueOf(rssi));
        sendBroadcast(intent);
    }

    //广播意图
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /* 广播远程发送过来的数据 */
    public void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        //从特征值获取数据
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
            //LogUtils.e("==从特征值获取返回的数据==", "" + stringBuilder);
            intent.putExtra(EXTRA_DATA, stringBuilder.toString().toUpperCase());
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("BluetoothLeService", "BluetoothLeService 调用onCreate方法");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e("BluetoothLeService", "BluetoothLeService 调用onStartCommand方法");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e("BluetoothLeService", "BluetoothLeService 调用onBind方法");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.e("BluetoothLeService", "BluetoothLeService 调用unbind方法");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LogUtils.e("BluetoothLeService", "BluetoothLeService 调用onTaskRemoved方法");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        LogUtils.e("BluetoothLeService", "BluetoothLeService 调用onDestroy方法");
        // 断开连接
        disconnect();
        // 关闭
        close();
        Prefer.getInstance().setBleStatus("未连接", null);
        super.onDestroy();
    }

    private final IBinder mBinder = new LocalBinder();


    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    /* service 中蓝牙初始化 */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {   //获取系统的蓝牙管理器
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The
     * connection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    // 连接远程蓝牙
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            LogUtils.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        /* 获取远端的蓝牙设备 */
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            LogUtils.e(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        /* 调用device中的connectGatt连接到远程设备 */
        if (!checkBluePermission()) {
            return false;
        }
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        LogUtils.d(TAG, "Trying to create a new connection.");
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The
     * disconnection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */

    // 取消远程蓝牙
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e("取消蓝牙连接", "BluetoothAdapter not initialized");
            return;
        }
        if (!checkBluePermission()) {
            return;
        }
        mBluetoothGatt.disconnect();

    }

    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    /**
     * @param
     * @return void
     * @throws
     * @Title: close
     * @Description: TODO(关闭所有蓝牙连接)
     */
    public void close() {
        LogUtils.e("BluetoothLeService 执行close方法");
        if (mBluetoothGatt == null) {
            return;
        }
        if (!checkBluePermission()) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic
     *            The characteristic to read from.
     */
    /**
     * @param @param characteristic（要读的特征值）
     * @return void    返回类型
     * @throws
     * @Title: readCharacteristic
     * @Description: TODO()
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        if (!checkBluePermission()) {
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
        LogUtils.e("==读取特征值==", "" + characteristic);
    }

    // 写入特征值
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        if (!checkBluePermission()) {
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
        //LogUtils.e("==写入特征值==", "" + characteristic);
    }

    // 读取RSSi
    public void readRssi() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtils.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        if (!checkBluePermission()) {
            return;
        }
        mBluetoothGatt.readRemoteRssi();
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic
     *            Characteristic to act on.
     * @param enabled
     *            If true, enable notification. False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtils.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        if (!checkBluePermission()) {
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if (enabled) {
            clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        mBluetoothGatt.writeDescriptor(clientConfig);
    }

    /**
     * <>
     *     得到特征值下的描述值
     * </>
     */
    public void getCharacteristicDescriptor(BluetoothGattDescriptor descriptor) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtils.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        if (!checkBluePermission()) {
            return;
        }
        mBluetoothGatt.readDescriptor(descriptor);
    }

    /**
     * <>
     *     得到蓝牙的所有服务
     * </>
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt != null) {
            return mBluetoothGatt.getServices();
        }
        return null;

    }

    /**
     * <>
     *     检查是否获取蓝牙连接权限
     * </>
     */
    private Boolean checkBluePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){//android 12以上才需要判断
            if (ActivityCompat.checkSelfPermission(RunningContext.sAppContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                LogUtils.w(TAG, "未获取到蓝牙权限");
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
