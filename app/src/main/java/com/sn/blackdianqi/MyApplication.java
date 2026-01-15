package com.sn.blackdianqi;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Base64;

import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.core.AppUncaughtExceptionHandler;
import com.sn.blackdianqi.file.FileUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.view.LoggerView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    public static final boolean isDebug = true;//是否为调试模式

    private static MyApplication instance;
    private static Stack<Activity> activityStack = new Stack<>();
    //蓝牙4.0的UUID,其中0000ffe1-0000-1000-8000-00805f9b34fb是广州汇承信息科技有限公司08蓝牙模块的UUID
    public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";

    public List<BluetoothGattService> supportedGattServices;
    //蓝牙service,负责后台的蓝牙服务
    public BluetoothLeService mBluetoothLeService;
    public BluetoothGattCharacteristic gattCharacteristic;

    // 蓝牙适配器
    public BluetoothAdapter mBluetoothAdapter;

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
            instance.onCreate();
        }
        return instance;
    }


    @Override
    public void onCreate() {
        LogUtils.e("---", "[MyApplication] onCreate");
        super.onCreate();
        instance = this;
        RunningContext.init(this);
        AppUncaughtExceptionHandler.getInstance().init(this);
        // 获取手机本地的蓝牙适配器
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 初始化Bugly
        //initBugly();

        // 初始化LoggerView
        LoggerView.init(this);
        //默认英文
       // LocaleUtils.updateLocale(this, LocaleUtils.LOCALE_ENGLISH);
    }

    public void initFilePath() {
        FileUtils.getInstance().createFiles(FileUtils.getInstance().getRootPath(), FileUtils.getInstance().getAudioPath(), FileUtils.getInstance().getImagePath(),
                FileUtils.getInstance().getImageTempPath(), FileUtils.getInstance().getPPTUploadPath());
    }

    //往栈中添加activity
    public void addActivity(Activity activity) {
        if (activity != null) {
            activityStack.add(activity);
        }
    }

    //从栈中移出activity
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    //依次销毁activity
    private void finishActivity() {
        for (int i = 0; i < activityStack.size(); i++) {
            if (activityStack.get(i) != null && !activityStack.get(i).isFinishing()) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }
}
