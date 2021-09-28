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
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.adapter.TabPagerAdapter;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.base.BaseFragment;
import com.sn.blackdianqi.bean.AlarmBean;
import com.sn.blackdianqi.bean.DateBean;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.fragment.AnmoFragment;
import com.sn.blackdianqi.fragment.DengguangFragment;
import com.sn.blackdianqi.fragment.KuaijieK1Fragment;
import com.sn.blackdianqi.fragment.KuaijieK2Fragment;
import com.sn.blackdianqi.fragment.KuaijieK3Fragment;
import com.sn.blackdianqi.fragment.KuaijieK4Fragment;
import com.sn.blackdianqi.fragment.KuaijieK5Fragment;
import com.sn.blackdianqi.fragment.KuaijieK8Fragment;
import com.sn.blackdianqi.fragment.KuaijieK9Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW10Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW11Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW1Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW2Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW3Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW4Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW6Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW7Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW8Fragment;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.NoScrollViewPager;
import com.sn.blackdianqi.view.TranslucentActionBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements View.OnClickListener, TranslucentActionBar.ActionBarClickListener {

    public static final String TAG = "HomeActivity";

    // 设置tab个数
    private final static int tabCount = 4;

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.ll_content)
    RelativeLayout relativeLayout;

    @BindView(R.id.vp_home)
    NoScrollViewPager viewPager;

    @BindView(R.id.tab1)
    LinearLayout tab1;
    @BindView(R.id.tab1_img)
    ImageView tab1Img;
    @BindView(R.id.tab1_text)
    TextView tab1TextView;

    @BindView(R.id.tab2)
    LinearLayout tab2;
    @BindView(R.id.tab2_img)
    ImageView tab2Img;
    @BindView(R.id.tab2_text)
    TextView tab2TextView;


    @BindView(R.id.tab3)
    LinearLayout tab3;
    @BindView(R.id.tab3_img)
    ImageView tab3Img;
    @BindView(R.id.tab3_text)
    TextView tab3TextView;


    @BindView(R.id.tab4)
    LinearLayout tab4;
    @BindView(R.id.tab4_img)
    ImageView tab4Img;
    @BindView(R.id.tab4_text)
    TextView tab4TextView;

    List<TextView> tabTextViews;
    List<ImageView> tabImageViews;

    List<BaseFragment> fragments;
    TabPagerAdapter tabPagerAdapter;

    // 特征值
    protected BluetoothGattCharacteristic characteristic;

    private String blueName;

    private String deviceAddress;

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {
        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        actionBar.setData(null, R.mipmap.ic_back, null, R.mipmap.ic_set, null, this);
        actionBar.setStatusBarHeight(getStatusBarHeight());
        DeviceBean deviceBean = Prefer.getInstance().getConnectedDevice();
        if (deviceBean != null) {
            blueName = deviceBean.getTitle();
            deviceAddress = deviceBean.getAddress();
        }
        LogUtils.e(TAG, "当前连接的蓝牙名称为：" + blueName);
        initView();
        setCurrentTab(1);

        // 启动蓝牙service
        Intent blueServiceIntent = new Intent(HomeActivity.this, BluetoothLeService.class);
        startService(blueServiceIntent);
        bindService(blueServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        RunningContext.threadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500L);
                    askStatus();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        tab4.setOnClickListener(this);
        tabTextViews = new ArrayList<>();
        tabTextViews.add(tab1TextView);
        tabTextViews.add(tab2TextView);
        tabTextViews.add(tab3TextView);
        tabTextViews.add(tab4TextView);

        tabImageViews = new ArrayList<>();
        tabImageViews.add(tab1Img);
        tabImageViews.add(tab2Img);
        tabImageViews.add(tab3Img);
        tabImageViews.add(tab4Img);

        fragments = new ArrayList<>();
        setFragments();
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.setScroll(true);
        viewPager.setOffscreenPageLimit(3);

        if (!TextUtils.isEmpty(blueName) && blueName.toUpperCase().contains("QMS2")) {
            tab3.setVisibility(View.GONE);
        }
    }


    private void setFragments() {
        if (TextUtils.isEmpty(blueName)) {
            fragments.add(new KuaijieK1Fragment());
            fragments.add(new WeitiaoW1Fragment());
        } else if (blueName.contains("QMS-IQ") || blueName.contains("QMS-I06")
                || blueName.contains("QMS-LQ") || blueName.contains("QMS-L04")) {
            fragments.add(new KuaijieK1Fragment());
            fragments.add(new WeitiaoW1Fragment());
        } else if (blueName.contains("QMS-JQ-D") || blueName.contains("QMS4")) {
            fragments.add(new KuaijieK2Fragment());
            fragments.add(new WeitiaoW2Fragment());
        } else if (blueName.contains("QMS-NQ") || blueName.contains("QMS3")) {
            fragments.add(new KuaijieK2Fragment());
            fragments.add(new WeitiaoW3Fragment());
        } else if (blueName.contains("QMS-MQ") || blueName.contains("QMS2")) {
            fragments.add(new KuaijieK2Fragment());
            fragments.add(new WeitiaoW4Fragment());
        } else if (blueName.contains("QMS-KQ-H") || blueName.contains("QMS-H02")) {
            fragments.add(new KuaijieK3Fragment());
            fragments.add(new WeitiaoW6Fragment());
        } else if (blueName.contains("QMS-DFQ") || blueName.contains("QMS-430") || blueName.contains("QMS-444")) {
            fragments.add(new KuaijieK4Fragment());
            fragments.add(new WeitiaoW7Fragment());
        } else if (blueName.contains("QMS-DQ") || blueName.contains("QMS-443")) {
            fragments.add(new KuaijieK5Fragment());
            fragments.add(new WeitiaoW8Fragment());
        } else if (blueName.contains("S3-2")) {
            fragments.add(new KuaijieK2Fragment());
            fragments.add(new WeitiaoW10Fragment());
        } else if (blueName.contains("S3-3")) {
            fragments.add(new KuaijieK8Fragment());
            fragments.add(new WeitiaoW11Fragment());
        } else if (blueName.contains("S3-4")) {
            fragments.add(new KuaijieK9Fragment());
            fragments.add(new WeitiaoW11Fragment());
        } else {
            fragments.add(new KuaijieK1Fragment());
            fragments.add(new WeitiaoW1Fragment());
        }

        fragments.add(new AnmoFragment());
        fragments.add(new DengguangFragment());
    }

    private void setCurrentTab(int tabIndex) {
        if (tabCount == 4 && tabIndex == 4) {
            tabIndex = 4;
        }
        int position = tabIndex - 1;
        for (int i = 0; i < tabCount; i++) {
            if (position == i) {
                tabTextViews.get(i).setSelected(true);
                tabImageViews.get(i).setSelected(true);
            } else {
                tabTextViews.get(i).setSelected(false);
                tabImageViews.get(i).setSelected(false);
            }
        }
        viewPager.setCurrentItem(position, false);
    }

    private void askStatus() {
        // 发送闹钟指令
        sendAlarmInitCmd();
    }

    /**
     * 发送闹钟初始化命令
     */
    private void sendAlarmInitCmd() {
        StringBuilder cmdSB = new StringBuilder();
        cmdSB.append("FFFFFFFF01000111");
        DateBean dateBean = new DateBean(new Date());
        cmdSB.append(dateBean.getHour());
        cmdSB.append(dateBean.getMinute());
        cmdSB.append(dateBean.getSecond());
        cmdSB.append(dateBean.getWeek());
        cmdSB.append(dateBean.getEndYear());
        cmdSB.append(dateBean.getMonth());
        cmdSB.append(dateBean.getDay());
        // 累加校验和
        cmdSB.append(BlueUtils.makeChecksum(cmdSB.toString()));
        LogUtils.i(TAG, "发送闹钟指令：" + cmdSB.toString());
        sendBlueCmd(cmdSB.toString());
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
            ToastUtils.showToast(HomeActivity.this, getString(R.string.device_no_connected));
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
        if (cmd.contains("FFFFFFFF0100030B00")) {
            LogUtils.i(TAG, "收到无闹钟指令：" + cmd);
            // 有闹钟,未设置
            AlarmBean alarmBean = new AlarmBean();
            alarmBean.setAlarmSwitch(false);
            Prefer.getInstance().setAlarm(deviceAddress, alarmBean);
        } else if (cmd.contains("FFFFFFFF01000413")) {
            LogUtils.i(TAG, "收到有闹钟指令：" + cmd);
            // 有闹钟，已设置
            AlarmBean alarmBean = new AlarmBean();
            String cmdStatus = cmd.substring(16, 18);
            // 开关
            if (cmdStatus.equals("0F")) {
                alarmBean.setAlarmSwitch(true);
            } else {
                alarmBean.setAlarmSwitch(false);
            }

            // 时间
            String timeHour = cmd.substring(18, 20);
            alarmBean.setHourStr(timeHour);
            String timeMin = cmd.substring(20, 22);
            alarmBean.setMinuteStr(timeMin);

            // 星期
            String cmdWeek = BlueUtils.hexString16To2hexString(cmd.substring(24, 26));
            for (int i = 0; i < 7; i++) {
                char charAt = cmdWeek.charAt(i);
                if (charAt == 1) {
                    alarmBean.getWeekCheckBeanMap().put(7 - i, true);
                }
            }

            // 模式
            String cmdMode = cmd.substring(28, 30);
            alarmBean.setModeCode(cmdMode);

            // 按摩
            String cmdAnmo = cmd.substring(30, 32);
            if (cmdAnmo.equals("01")) {
                alarmBean.setAnmo(true);
            } else {
                alarmBean.setAnmo(false);
            }

            // 响铃
            String cmdRing = cmd.substring(32, 34);
            if (cmdRing.equals("01")) {
                alarmBean.setXiangling(true);
            } else {
                alarmBean.setXiangling(false);
            }
            Prefer.getInstance().setAlarm(deviceAddress, alarmBean);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab1:
                setCurrentTab(1);
                break;
            case R.id.tab2:
                setCurrentTab(2);
                break;
            case R.id.tab3:
                setCurrentTab(3);
                break;
            case R.id.tab4:
                setCurrentTab(4);
                break;
            default:
                break;
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
                Prefer.getInstance().setBleStatus("未连接", null);
                ToastUtils.showToast(HomeActivity.this, R.string.device_disconnect);
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
