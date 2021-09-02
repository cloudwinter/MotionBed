package com.sn.blackdianqi;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Base64;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.core.AppUncaughtExceptionHandler;
import com.sn.blackdianqi.file.FileUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.ScreenUtils;
import com.sn.blackdianqi.view.LoggerView;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
        initImageLoader();
        initFilePath();
        // Initializes Bluetooth adapter.
        // 获取手机本地的蓝牙适配器
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 初始化Bugly
        initBugly();

        // 初始化LoggerView
        LoggerView.init(this);
        //默认英文
       // LocaleUtils.updateLocale(this, LocaleUtils.LOCALE_ENGLISH);
    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this)) // default = device screen dimensions
                .diskCacheExtraOptions(ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this), null)
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .imageDecoder(new BaseImageDecoder(false)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void initFilePath() {
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


    //把list集合转为String
    public static String SceneList2String(List SceneList) throws IOException {
        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(SceneList);
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        String SceneListString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        // 关闭objectOutputStream
        objectOutputStream.close();
        return SceneListString;
    }

    //把String转为list集合
    @SuppressWarnings("unchecked")
    public static List String2SceneList(String SceneListString) throws IOException, ClassNotFoundException {
        byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        List SceneList = (List) objectInputStream.readObject();
        objectInputStream.close();
        return SceneList;
    }
    private void initBugly() {
        /* Bugly SDK初始化
        * 参数1：上下文对象
        * 参数2：APPID，平台注册时得到,注意替换成你的appId
        * 参数3：是否开启调试模式，调试模式下会输出'CrashReport'tag的日志
        * 注意：如果您之前使用过Bugly SDK，请将以下这句注释掉。
        */
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppVersion("1");
        strategy.setAppPackageName("com.sn.blackdianqi");
        strategy.setAppReportDelay(20000);                          //Bugly会在启动20s后联网同步数据

        /*  第三个参数为SDK调试模式开关，调试模式的行为特性如下：
            输出详细的Bugly SDK的Log；
            每一条Crash都会被立即上报；
            自定义日志将会在Logcat中输出。
            建议在测试阶段建议设置成true，发布时设置为false。*/

        CrashReport.initCrashReport(getApplicationContext(), "50b1036fc4", true ,strategy);

        Bugly.init(getApplicationContext(), "50b1036fc4", false);
    }

}
