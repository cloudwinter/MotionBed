package com.sn.blackdianqi.activity;

import static com.sn.blackdianqi.activity.WeekActivity.RESULT_CODE;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.gzuliyujiang.wheelpicker.TimePicker;
import com.github.gzuliyujiang.wheelpicker.annotation.TimeMode;
import com.github.gzuliyujiang.wheelpicker.contract.OnTimePickedListener;
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity;
import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.bean.AlarmBean;
import com.sn.blackdianqi.bean.AskStatusgeEvent;
import com.sn.blackdianqi.bean.AudioEvent;
import com.sn.blackdianqi.bean.DateBean;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.bean.MusicBean;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.dialog.MusicSelectDialog;
import com.sn.blackdianqi.dialog.WaitDialog;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LocaleUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.AnjianAnmoView;
import com.sn.blackdianqi.view.AnjianAnmoYuanView;
import com.sn.blackdianqi.view.TranslucentActionBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DianDongSetActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    public static final String TAG = "DianDongSetActivity";


    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.llAnmoTitle)
    LinearLayout llAnmoTitle;
    @BindView(R.id.llAnmo)
    LinearLayout llAnmo;
    @BindView(R.id.ivAnmo)
    ImageView ivAnmo;
    @BindView(R.id.tvSaveAnmo)
    TextView tvSaveAnmo;

    @BindView(R.id.llDengguangTitle)
    LinearLayout llDengguangTitle;
    @BindView(R.id.llDengguang)
    LinearLayout llDengguang;
    @BindView(R.id.ivDengguang)
    ImageView ivDengguang;
    @BindView(R.id.tvSaveDengguang)
    TextView tvSaveDengguang;

    @BindView(R.id.llClockTitle)
    LinearLayout llClockTitle;
    @BindView(R.id.llClock)
    LinearLayout llClock;
    @BindView(R.id.ivClock)
    ImageView ivClock;
    @BindView(R.id.tvSaveClock)
    TextView tvSaveClock;

    @BindView(R.id.view_10time)
    AnjianAnmoYuanView min10View;
    @BindView(R.id.view_20time)
    AnjianAnmoYuanView min20View;
    @BindView(R.id.view_30time)
    AnjianAnmoYuanView min30View;

    @BindView(R.id.view_anmo_pinglv)
    AnjianAnmoView anmoPinglvView;
    @BindView(R.id.view_anmo_toubu)
    AnjianAnmoView anmoToubuView;
    @BindView(R.id.view_anmo_zubu)
    AnjianAnmoView anmoZubuView;

    @BindView(R.id.tv_10fenzhong)
    TextView tenMinsTextView;
    @BindView(R.id.tv_8xiaoshi)
    TextView eightHoursTextView;
    @BindView(R.id.tv_10xiaoshi)
    TextView tenHoursTextView;

    @BindView(R.id.view_dengguang_level)
    AnjianAnmoView dengguangLevel;

    @BindView(R.id.ll_time)
    LinearLayout timeLL;
    @BindView(R.id.tv_time)
    TextView timeTV;

    @BindView(R.id.ll_week)
    LinearLayout weekLL;
    @BindView(R.id.tv_week)
    TextView weekTV;

    @BindView(R.id.ll_mode)
    LinearLayout modeLL;
    @BindView(R.id.tv_mode)
    TextView modeTV;

    @BindView(R.id.ll_anmo)
    LinearLayout anmoLL;
    @BindView(R.id.cb_anmo)
    CheckBox anmoCB;

    @BindView(R.id.ll_xiangling1)
    LinearLayout xianglingLL1;
    @BindView(R.id.tv_music)
    TextView tvMusic;

    @BindView(R.id.ll_xiangling2)
    LinearLayout xianglingLL2;
    @BindView(R.id.cb_xinagling)
    CheckBox xinaglingCB;

    /**
     * 默认间隔
     */
    protected final static long DEFAULT_INTERVAL = 2000;
    // 特征值
    protected BluetoothGattCharacteristic characteristic;


    public static int WEEK_REQUEST_CODE = 107;
    public static int MODE_REQUEST_CODE = 106;

    private HashMap<Integer, Boolean> weekCheckBeanMap = new HashMap<>();
    private List<MusicBean> musicList = new ArrayList<>();

    // 加载中对话框
    private WaitDialog mWaitDialog;

    // 时间
    private String hourStr, minuteStr;
    // 01：零压力，02：记忆1，03：无动作
    private String modeCode = "03";
    // 展示异常的Toast
    private Boolean showFailToast = Boolean.TRUE;
    private boolean isAudio;//是否音响
    String blueTitle = "";
    private MusicSelectDialog musicSelectDialog;

    private boolean isFirstAlarm;//是否首次设置闹钟
    private boolean isQueryAlarm;//是否是查询闹钟

    //音乐
    private String musicVal = "00";


    private String blueName;

    private String deviceAddress;

    private String switchCheck = "00";

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
        setContentView(R.layout.activity_dian_dong_set);
        ButterKnife.bind(this);
        actionBar.setData(null, R.mipmap.ic_back, null, 0, "", this);
        actionBar.setStatusBarHeight(getStatusBarHeight());
        DeviceBean deviceBean = Prefer.getInstance().getConnectedDevice();
        if (deviceBean != null) {
            blueName = deviceBean.getTitle();
            deviceAddress = deviceBean.getAddress();
        }

        isFirstAlarm = getIntent().getBooleanExtra("isFirstAlarm", false);
        if (isFirstAlarm) {
            setVisibleModel(2);
        }
        LogUtils.e(TAG, "当前连接的蓝牙名称为：" + blueName);
        initView();
        initData();
    }

    private void initData() {
        String deviceAddress = Prefer.getInstance().getLatelyConnectedDevice();
        if (TextUtils.isEmpty(deviceAddress)) {
            return;
        }

        //是否有音响模式
        isAudio = Prefer.getInstance().getIsAudio(deviceAddress);
        if (isAudio) {
            xianglingLL1.setVisibility(View.VISIBLE);
            xianglingLL2.setVisibility(View.GONE);
        } else {
            xianglingLL2.setVisibility(View.VISIBLE);
            xianglingLL1.setVisibility(View.GONE);
        }

        AlarmBean alarmBean = Prefer.getInstance().getAlarm(deviceAddress);
        if (alarmBean != null) {
            if (isFirstAlarm) {
                switchCheck = "01";
            } else {
                switchCheck = alarmBean.isAlarmSwitch() ? "01" : "A1";
            }
            hourStr = alarmBean.getHourStr();
            minuteStr = alarmBean.getMinuteStr();
            modeCode = alarmBean.getModeCode();
            weekCheckBeanMap = alarmBean.getWeekCheckBeanMap();

            if (!TextUtils.isEmpty(hourStr) && !TextUtils.isEmpty(minuteStr)) {
                timeTV.setText(hourStr + ":" + minuteStr);
            }
            modeTV.setText(getModeStrByCode(modeCode));
            anmoCB.setChecked(alarmBean.isAnmo());
            xinaglingCB.setChecked(alarmBean.isXiangling());
            setMusic(alarmBean);
        } else {
            llClockTitle.setVisibility(View.GONE);
            llClock.setVisibility(View.GONE);
        }
        setWeek();
    }

    //初始化音乐
    private void setMusic(AlarmBean alarmBean) {
        musicList.add(new MusicBean("00", getResources().getString(R.string.music_2_0)));
        musicList.add(new MusicBean("11", getResources().getString(R.string.music_2_1)));
        musicList.add(new MusicBean("12", getResources().getString(R.string.music_2_2)));
        musicList.add(new MusicBean("13", getResources().getString(R.string.music_2_3)));
        musicList.add(new MusicBean("14", getResources().getString(R.string.music_2_4)));
        musicList.add(new MusicBean("15", getResources().getString(R.string.music_2_5)));

        for (MusicBean bean : musicList) {
            if (TextUtils.equals(bean.getValue(), alarmBean.getMusicVal())) {
                musicVal = alarmBean.getMusicVal();
                tvMusic.setText(bean.getName());
            }
        }
    }


    private void setWeek() {
        if (weekCheckBeanMap == null || weekCheckBeanMap.size() == 0) {
            weekTV.setText(R.string.alarm_week_no_repeat);
            return;
        }
        TreeMap<Integer, Boolean> treeMap = sortMapByKey(weekCheckBeanMap);
        Iterator<Map.Entry<Integer, Boolean>> it = treeMap.entrySet().iterator();
        StringBuilder weekStr = new StringBuilder();
        int count = 0;
        while (it.hasNext()) {
            Map.Entry<Integer, Boolean> entry = it.next();
            if (entry.getValue()) {
                if (count > 0) {
                    weekStr.append(",");
                }
                if (Prefer.getInstance().getSelectedLanguage().equals("ja")) {
                    weekStr.append(LocaleUtils.getJaWeek(entry.getKey()));
                } else {
                    weekStr.append(entry.getKey());
                }
                count++;
            }
        }
        weekTV.setText(weekStr.toString());
    }

    private void initView() {
        llAnmoTitle.setOnClickListener(this);
        llDengguangTitle.setOnClickListener(this);
        llClockTitle.setOnClickListener(this);
        tvSaveAnmo.setOnClickListener(this);
        tvSaveDengguang.setOnClickListener(this);
        tvSaveClock.setOnClickListener(this);
        min10View.setOnClickListener(this);
        min20View.setOnClickListener(this);
        min30View.setOnClickListener(this);
        anmoPinglvView.setChildClickListener(new AnjianAnmoView.ChildClickListener() {
            @Override
            public void minusClick() {
                sendBlueCmd("FF FF FF FF 05 00 00 00 15 16 CF");
            }

            @Override
            public void plusClick() {
                sendBlueCmd("FF FF FF FF 05 00 00 00 14 D7 0F");
            }
        });

        anmoToubuView.setChildClickListener(new AnjianAnmoView.ChildClickListener() {
            @Override
            public void minusClick() {
                sendBlueCmd("FF FF FF FF 05 00 00 00 11 17 0C");
            }

            @Override
            public void plusClick() {
                sendBlueCmd("FF FF FF FF 05 00 00 00 10 D6 CC");
            }
        });

        anmoZubuView.setChildClickListener(new AnjianAnmoView.ChildClickListener() {
            @Override
            public void minusClick() {
                sendBlueCmd("FF FF FF FF 05 00 00 00 13 96 CD");
            }

            @Override
            public void plusClick() {
                sendBlueCmd("FF FF FF FF 05 00 00 00 12 57 0D");
            }
        });

        tenMinsTextView.setOnClickListener(this);
        eightHoursTextView.setOnClickListener(this);
        tenHoursTextView.setOnClickListener(this);

        dengguangLevel.setChildClickListener(new AnjianAnmoView.ChildClickListener() {
            @Override
            public void minusClick() {
                if (dengguangLevel.getLevel() == 0) {
                    ToastUtils.showToast(RunningContext.sAppContext, R.string.dengguangliangdu_min_tips);
                    return;
                }
                sendDengguangLevelCmd(dengguangLevel.getLevel() - 1);
            }

            @Override
            public void plusClick() {
                if (dengguangLevel.getLevel() == 10) {
                    ToastUtils.showToast(RunningContext.sAppContext, R.string.dengguangliangdu_max_tips);
                    return;
                }
                sendDengguangLevelCmd(dengguangLevel.getLevel() + 1);
            }
        });

        mWaitDialog = new WaitDialog(this, getString(R.string.sending));
        mWaitDialog.setCanceledOnTouchOutside(true);

        timeLL.setOnClickListener(this);
        weekLL.setOnClickListener(this);
        modeLL.setOnClickListener(this);
        xianglingLL1.setOnClickListener(this);

        DeviceBean deviceBean = Prefer.getInstance().getConnectedDevice();
        if (deviceBean != null && !TextUtils.isEmpty(deviceBean.getTitle())) {
            blueTitle = deviceBean.getTitle().toUpperCase();
            if (deviceBean.getTitle().toUpperCase().contains("QMS2")) {
                anmoLL.setVisibility(View.GONE);
            }
        }
    }

    private void sendDengguangLevelCmd(int level) {
        String cmd = "";
        switch (level) {
            case 0:
                cmd = "FF FF FF FF 05 00 00 00 23 96 D9";
                break;
            case 1:
                cmd = "FF FF FF FF 05 00 00 01 23 97 49";
                break;
            case 2:
                cmd = "FF FF FF FF 05 00 00 02 23 97 B9";
                break;
            case 3:
                cmd = "FF FF FF FF 05 00 00 03 23 96 29";
                break;
            case 4:
                cmd = "FF FF FF FF 05 00 00 04 23 94 19";
                break;
            case 5:
                cmd = "FF FF FF FF 05 00 00 05 23 95 89";
                break;
            case 6:
                cmd = "FF FF FF FF 05 00 00 06 23 95 79";
                break;
            case 7:
                cmd = "FF FF FF FF 05 00 00 07 23 94 E9";
                break;
            case 8:
                cmd = "FF FF FF FF 05 00 00 08 23 91 19";
                break;
            case 9:
                cmd = "FF FF FF FF 05 00 00 09 23 90 89";
                break;
            case 10:
                cmd = "FF FF FF FF 05 00 00 0A 23 90 79";
                break;
        }
        if (!TextUtils.isEmpty(cmd)) {
            sendBlueCmd(cmd);
        }
    }

    public void setVisibleModel(int index) {
        int visibility = 0;
        switch (index) {
            case 0:
                 visibility = llAnmo.getVisibility();
                if (visibility == 0) {
                    ivAnmo.setImageDrawable(getResources().getDrawable(R.mipmap.arror_up));
                    llAnmo.setVisibility(View.GONE);
                } else {
                    ivAnmo.setImageDrawable(getResources().getDrawable(R.mipmap.arror_down));
                    llAnmo.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                 visibility = llDengguang.getVisibility();
                if (visibility == 0) {
                    ivDengguang.setImageDrawable(getResources().getDrawable(R.mipmap.arror_up));
                    llDengguang.setVisibility(View.GONE);
                } else {
                    ivDengguang.setImageDrawable(getResources().getDrawable(R.mipmap.arror_down));
                    llDengguang.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                visibility = llClock.getVisibility();
                if (visibility == 0) {
                    ivClock.setImageDrawable(getResources().getDrawable(R.mipmap.arror_up));
                    llClock.setVisibility(View.GONE);
                } else {
                    ivClock.setImageDrawable(getResources().getDrawable(R.mipmap.arror_down));
                    llClock.setVisibility(View.VISIBLE);
                }
                break;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llAnmoTitle:
                setVisibleModel(0);
                break;
            case R.id.llDengguangTitle:
                setVisibleModel(1);
                break;
            case R.id.llClockTitle:
                setVisibleModel(2);
                break;
            case R.id.view_10time:
                if (min10View.isSelected()) {
                    clear();
                    sendBlueCmd("FF FF FF FF 05 00 00 00 1C D6 C9");
                } else {
                    sendBlueCmd("FF FF FF FF 05 00 00 00 16 56 CE");
                }
                min10View.setSelected(!min10View.isSelected());
                min20View.setSelected(false);
                min30View.setSelected(false);
                break;
            case R.id.view_20time:
                if (min20View.isSelected()) {
                    clear();
                    sendBlueCmd("FF FF FF FF 05 00 00 00 1C D6 C9");
                } else {
                    sendBlueCmd("FF FF FF FF 05 00 00 00 17 97 0E");
                }
                min10View.setSelected(false);
                min20View.setSelected(!min20View.isSelected());
                min30View.setSelected(false);
                break;
            case R.id.view_30time:
                if (min30View.isSelected()) {
                    clear();
                    sendBlueCmd("FF FF FF FF 05 00 00 00 1C D6 C9");
                } else {
                    sendBlueCmd("FF FF FF FF 05 00 00 00 18 D7 0A");
                }
                min10View.setSelected(false);
                min20View.setSelected(false);
                min30View.setSelected(!min30View.isSelected());
                break;
            case R.id.tv_10fenzhong:
                sendBlueCmd("FF FF FF FF 05 00 00 00 19 16 CA");
                if (tenMinsTextView.isSelected()) {
                    tenMinsTextView.setSelected(false);
                } else {
                    tenMinsTextView.setSelected(true);
                    eightHoursTextView.setSelected(false);
                    tenHoursTextView.setSelected(false);
                }
                break;
            case R.id.tv_8xiaoshi:
                sendBlueCmd("FF FF FF FF 05 00 00 00 1A 56 CB");
                // 8小时
                if (eightHoursTextView.isSelected()) {
                    eightHoursTextView.setSelected(false);
                } else {
                    tenMinsTextView.setSelected(false);
                    eightHoursTextView.setSelected(true);
                    tenHoursTextView.setSelected(false);
                }
                break;
            case R.id.tv_10xiaoshi:
                sendBlueCmd("FF FF FF FF 05 00 00 00 1B 97 0B");
                // 10小时
                if (tenHoursTextView.isSelected()) {
                    tenHoursTextView.setSelected(false);
                } else {
                    tenMinsTextView.setSelected(false);
                    eightHoursTextView.setSelected(false);
                    tenHoursTextView.setSelected(true);
                }
                break;
            case R.id.ll_time:
                TimePicker picker = new TimePicker(this);
                picker.getCancelView().setText(R.string.dialog_cancel);
                picker.getOkView().setTextColor(getResources().getColor(R.color.text_green));
                picker.getOkView().setText(getText(R.string.dialog_confirm));
                picker.getWheelLayout().setTimeMode(TimeMode.HOUR_24_NO_SECOND);
                picker.getWheelLayout().setRange(TimeEntity.target(0, 0, 0), TimeEntity.target(23, 59, 59));
                TimeEntity timeEntity = TimeEntity.now();
                if (!TextUtils.isEmpty(hourStr)) {
                    timeEntity.setHour(Integer.parseInt(hourStr));
                }
                if (!TextUtils.isEmpty(minuteStr)) {
                    timeEntity.setMinute(Integer.parseInt(minuteStr));
                }
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
                        timeTV.setText(hourStr + ":" + minuteStr);
                    }
                });
                picker.show();
                break;
            case R.id.ll_week:
                Intent intentWeek = new Intent(DianDongSetActivity.this, WeekActivity.class);
                intentWeek.putExtra(WeekActivity.EXTRA_KEY, weekCheckBeanMap);
                startActivityForResult(intentWeek, WEEK_REQUEST_CODE);
                break;
            case R.id.ll_mode:
                Intent intentMode = null;
                if (blueTitle.contains("QMS-DFQ") || blueTitle.contains("QMS-430") || blueTitle.contains("QMS-444")) {
                    intentMode = new Intent(DianDongSetActivity.this, Mode2Activity.class);
                } else {
                    intentMode = new Intent(DianDongSetActivity.this, ModeActivity.class);
                }
                intentMode.putExtra(ModeActivity.EXTRA_KEY, modeCode);
                startActivityForResult(intentMode, MODE_REQUEST_CODE);
                break;
            case R.id.ll_xiangling1:
                musicSelectDialog = new MusicSelectDialog(this, musicList, new MusicSelectDialog.CallBackDialogListener() {
                    @Override
                    public void selectOnClick(String value, String name) {
                        musicVal = value;
                        tvMusic.setText(name);
                        if (TextUtils.equals("11", value)) {
                            String cmd = "FFFFFFFF0100130B81";
                            cmd = cmd + BlueUtils.makeChecksum(cmd);
                            sendBlueCmd(cmd);
                        } else if (TextUtils.equals("12", value)) {
                            String cmd = "FFFFFFFF0100130B82";
                            cmd = cmd + BlueUtils.makeChecksum(cmd);
                            sendBlueCmd(cmd);
                        } else if (TextUtils.equals("13", value)) {
                            String cmd = "FFFFFFFF0100130B83";
                            cmd = cmd + BlueUtils.makeChecksum(cmd);
                            sendBlueCmd(cmd);
                        } else if (TextUtils.equals("14", value)) {
                            String cmd = "FFFFFFFF0100130B84";
                            cmd = cmd + BlueUtils.makeChecksum(cmd);
                            sendBlueCmd(cmd);
                        } else if (TextUtils.equals("15", value)) {
                            String cmd = "FFFFFFFF0100130B85";
                            cmd = cmd + BlueUtils.makeChecksum(cmd);
                            sendBlueCmd(cmd);
                        }
                    }

                    @Override
                    public void saveOnClick() {
                        String cmd = "FF FF FF FF 01 00 13 0B 00";
                        sendBlueCmd(cmd + BlueUtils.makeChecksum(cmd));
                    }
                });
                musicSelectDialog.show();
                break;
            case R.id.tvSaveAnmo:
                String cmdAnmo = "FF FF FF FF 01 00 1C 14 01 00 00 00 00 00 00 00 00 00";
                sendBlueCmd(cmdAnmo + BlueUtils.makeChecksum(cmdAnmo));
                break;
            case R.id.tvSaveDengguang:
                String cmdDengguang = "FF FF FF FF 01 00 1C 14 03 00 00 00 00 00 00 00 00 00";
                sendBlueCmd(cmdDengguang + BlueUtils.makeChecksum(cmdDengguang));
                break;
            case R.id.tvSaveClock:
                isQueryAlarm = false;
                checkAndSend();
                break;
        }
    }

    private void clear() {
        // 头部按摩停止
        anmoToubuView.setLevel(0);
        // 足部按摩停止
        anmoZubuView.setLevel(0);
        // 按摩频率1档
        anmoPinglvView.setLevel(0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 启动蓝牙service
        Intent blueServiceIntent = new Intent(DianDongSetActivity.this, BluetoothLeService.class);
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
            Thread.sleep(300L);
            // 获取灯光状态指令 旧版本指令：sendBlueCmd("FF FF FF FF 05 00 05 FF 23 C7 28");
            String cmd = "FF FF FF FF 01 00 1C 14 02 00 00 00 00 00 00 00 00 00";
            cmd = cmd + BlueUtils.makeChecksum(cmd);
            sendBlueCmd(cmd);
            Thread.sleep(300L);
            // 获取按摩状态指令
            cmd = "FF FF FF FF 01 00 1C 14 04 00 00 00 00 00 00 00 00 00";
            cmd = cmd + BlueUtils.makeChecksum(cmd);
            sendBlueCmd(cmd);
            Thread.sleep(300L);
            //发送闹钟指令
            sendAlarmInitCmd();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送闹钟初始化命令
     */
    private void sendAlarmInitCmd() {
        isQueryAlarm = true;
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


    private void handleReceiveData(String data) {
        Log.e("回复cmd:", data);
        if (data.contains("FF FF FF FF 05 00 00 01")) {
            if (data.contains("FF FF FF FF 05 00 00 01 00 D6 90")) {
                // 头部按摩停止
                anmoToubuView.setLevel(0);
            }
            if (data.contains("FF FF FF FF 05 00 00 01 1E 56 98")) {
                // 头部按摩一档
                anmoToubuView.setLevel(1);
            }
            if (data.contains("FF FF FF FF 05 00 00 01 1F 97 58")) {
                // 头部按摩二档
                anmoToubuView.setLevel(2);
            }
            if (data.contains("FF FF FF FF 05 00 00 01 20 D7 48")) {
                // 头部按摩三档
                anmoToubuView.setLevel(3);
            }
        } else if (data.contains("FF FF FF FF 05 00 00 02")) {
            if (data.contains("FF FF FF FF 05 00 00 02 00 D6 60")) {
                // 足部按摩停止
                anmoZubuView.setLevel(0);
            }
            if (data.contains("FF FF FF FF 05 00 00 02 21 16 78")) {
                // 足部按摩一档
                anmoZubuView.setLevel(1);
            }
            if (data.contains("FF FF FF FF 05 00 00 02 22 56 79")) {
                // 足部按摩二档
                anmoZubuView.setLevel(2);
            }
            if (data.contains("FF FF FF FF 05 00 00 02 23 97 B9")) {
                // 足部按摩三档
                anmoZubuView.setLevel(3);
            }
        } else if (data.contains("FF FF FF FF 05 00 00 03")) {
            if (data.contains("FF FF FF FF 05 00 00 03 24 D7 EB")) {
                // 按摩频率1档
                anmoPinglvView.setLevel(1);
            }
            if (data.contains("FF FF FF FF 05 00 00 03 25 16 2B")) {
                // 按摩频率2档
                anmoPinglvView.setLevel(2);
            }
            if (data.contains("FF FF FF FF 05 00 00 03 26 56 2A")) {
                // 按摩频率3档
                anmoPinglvView.setLevel(3);
            }
            if (data.contains("FF FF FF FF 05 00 00 03 27 97 EA")) {
                // 按摩频率4档
                anmoPinglvView.setLevel(4);
            }
        } else if (data.contains("FF FF FF FF 05 00 01")) {//旧版本查询灯光状态回复
            dengguangLevel.setVisibility(View.VISIBLE);
            // 去除空格
            data = data.replaceAll(" ", "");
            String level = data.substring(14, 16);
            int levelNum = BlueUtils.covert16TO10(level);
            dengguangLevel.setLevel(levelNum);
            if (levelNum == 0) {
                tenMinsTextView.setSelected(false);
                eightHoursTextView.setSelected(false);
                tenHoursTextView.setSelected(false);
            }
        } else if (data.contains("FF FF FF FF 01 00 03 0B 00")) {
            data = data.toUpperCase().replaceAll(" ", "");
            LogUtils.i(TAG, "接收到有闹钟未设置指令：" + data);
            mWaitDialog.dismiss();
            showFailToast = false;
            String isAudio = data.substring(16, 18);//是否有音响
            if (TextUtils.equals(isAudio, "00") || blueName.toUpperCase().contains("QMS3-N93-327")) {
                Prefer.getInstance().setIsAudio(deviceAddress, false);
            } else {
                Prefer.getInstance().setIsAudio(deviceAddress, true);
            }
            LogUtils.i(TAG, "收到无闹钟未设置指令：" + data);
            // 有闹钟,未设置
            AlarmBean alarmBean = new AlarmBean();
            alarmBean.setAlarmSwitch(false);
            Prefer.getInstance().setAlarm(deviceAddress, alarmBean);
            if (!isQueryAlarm) {
                ToastUtils.showToast(DianDongSetActivity.this, getString(R.string.alarm_save_suc));
            }
        } else if (data.contains("FF FF FF FF 01 00 04 13")) {
            data = data.toUpperCase().replaceAll(" ", "");
            LogUtils.i(TAG, "接收到有闹钟已设置指令：" + data);
            mWaitDialog.dismiss();
            showFailToast = false;
            String isAudio = data.substring(16, 18);//是否有音响
            if (TextUtils.equals(isAudio, "0F") || TextUtils.equals(isAudio, "AF") || blueName.toUpperCase().contains("QMS3-N93-327")) {
                Prefer.getInstance().setIsAudio(deviceAddress, false);
            } else {
                Prefer.getInstance().setIsAudio(deviceAddress, true);
            }
            LogUtils.i(TAG, "收到有闹钟已设置指令：" + data);
            setHasAlarm(data);
            if (!isQueryAlarm) {
                ToastUtils.showToast(DianDongSetActivity.this, getString(R.string.alarm_save_suc));
            }
        } else if (data.contains("FF FF FF FF 01 00 13 0B")) {//设置音乐
            data = data.toUpperCase().replaceAll(" ", "");
            String musicVal = data.substring(17, 18);//音乐
            String deviceAddress = Prefer.getInstance().getLatelyConnectedDevice();
            AlarmBean alarmBean = Prefer.getInstance().getAlarm(deviceAddress);
            alarmBean.setMusicVal("0" + musicVal);
            Prefer.getInstance().setAlarm(deviceAddress, alarmBean);
        } else if (data.contains("FF FF FF FF 01 00 1C 14 02")) {//查询灯光状态回码
            data = data.toUpperCase().replaceAll(" ", "");
            String dgLevel = data.substring(20, 22);//灯光亮度
            dengguangLevel.setLevel(BlueUtils.covert16TO10(dgLevel));
            String dgTime = data.substring(22, 24);//灯光时间
            if (TextUtils.equals(dgTime, "00")) {//关闭状态
                tenMinsTextView.setSelected(false);
                eightHoursTextView.setSelected(false);
                tenHoursTextView.setSelected(false);
            } else if (TextUtils.equals(dgTime, "01")) {
                tenMinsTextView.setSelected(true);
                eightHoursTextView.setSelected(false);
                tenHoursTextView.setSelected(false);
            } else if (TextUtils.equals(dgTime, "02")) {
                tenMinsTextView.setSelected(false);
                eightHoursTextView.setSelected(true);
                tenHoursTextView.setSelected(false);
            } else if (TextUtils.equals(dgTime, "03")) {
                tenMinsTextView.setSelected(false);
                eightHoursTextView.setSelected(false);
                tenHoursTextView.setSelected(true);
            }
        } else if (data.contains("FF FF FF FF 01 00 1C 14 04")) {//查询按摩状态回码
            data = data.toUpperCase().replaceAll(" ", "");
            String toubuLevel = data.substring(18, 20);//头部按摩
            anmoToubuView.setLevel(BlueUtils.covert16TO10(toubuLevel));
            String tuibuLevel = data.substring(20, 22);//腿部按摩
            anmoZubuView.setLevel(BlueUtils.covert16TO10(tuibuLevel));
            String pinlvLevel = data.substring(22, 24);//按摩频率
            anmoPinglvView.setLevel(BlueUtils.covert16TO10(pinlvLevel));
            String anmoTime = data.substring(24, 26);//按摩时长
            if (TextUtils.equals(anmoTime, "00")) {
                min10View.setSelected(true);
                min20View.setSelected(false);
                min30View.setSelected(false);
            } else if (TextUtils.equals(anmoTime, "01")) {
                min10View.setSelected(false);
                min20View.setSelected(true);
                min30View.setSelected(false);
            } else if (TextUtils.equals(anmoTime, "02")) {
                min10View.setSelected(false);
                min20View.setSelected(false);
                min30View.setSelected(true);
            }
        } else if (data.contains("FF FF FF FF 01 00 1C 14 01")) {//设置按摩状态回码
            ToastUtils.showToast(this, getResources().getString(R.string.alarm_save_suc));
        } else if (data.contains("FF FF FF FF 01 00 1C 14 03")) {//设置灯光状态回码
            ToastUtils.showToast(this, getResources().getString(R.string.alarm_save_suc));
        }
    }

    protected boolean checkConnected() {
        // 判断蓝牙是否连接
        if (!BlueUtils.isConnected()) {
            ToastUtils.showToast(RunningContext.sAppContext, getString(R.string.device_no_connected));
            LogUtils.i(TAG, "sendBlueCmd -> 蓝牙未连接");
            return false;
        }
        return true;
    }

    /**
     * 发送蓝牙命令
     */
    private void checkAndSend() {
        if (!checkConnected()) {
            return;
        }
        if (TextUtils.isEmpty(hourStr)) {
            ToastUtils.showToast(DianDongSetActivity.this, "请选择时间");
            return;
        }
        AlarmBean alarmBean = new AlarmBean();

        StringBuilder sb = new StringBuilder();
        sb.append("FFFFFFFF01000213");
        // 状态
        sb.append(switchCheck);

        // 时间
        alarmBean.setHourStr(hourStr);
        alarmBean.setMinuteStr(minuteStr);
        sb.append(defaultIfEmpty(hourStr, "00"));
        sb.append(defaultIfEmpty(minuteStr, "00"));
        sb.append("00");


        // 星期
        alarmBean.setWeekCheckBeanMap(weekCheckBeanMap);
        StringBuilder weekStr2 = new StringBuilder();
        for (int i = 7; i >= 1; i--) {
            if (weekCheckBeanMap.containsKey(i) && weekCheckBeanMap.get(i)) {
                weekStr2.append("1");
            } else {
                weekStr2.append("0");
            }
        }
        weekStr2.append("0");
        sb.append(BlueUtils.str2To16(weekStr2.toString()));

        // 重复
        if (weekCheckBeanMap.size() == 0) {
            sb.append("00");
        } else {
            sb.append("01");
        }

        // 模式
        alarmBean.setModeCode(modeCode);
        sb.append(modeCode);

        // 按摩
        alarmBean.setAnmo(anmoCB.isChecked());
        sb.append(anmoCB.isChecked() ? "01" : "00");
        // 响铃
        if (isAudio) {
            // 响铃音乐
            sb.append(musicVal);//设置响铃音乐
        } else {
            //是否响铃
            alarmBean.setXiangling(xinaglingCB.isChecked());
            sb.append(xinaglingCB.isChecked() ? "01" : "00");
        }

        // 校验和
        sb.append(BlueUtils.makeChecksum(sb.toString()));

        // 发送蓝牙命令
        mWaitDialog.show();
        sendBlueCmd(sb.toString());
        Prefer.getInstance().setAlarm(Prefer.getInstance().getLatelyConnectedDevice(), alarmBean);
        new Handler().postDelayed(() -> {
            if (showFailToast) {
                mWaitDialog.dismiss();
                ToastUtils.showToast(DianDongSetActivity.this, getString(R.string.alarm_save_failed));
            }
        }, 1500);
    }


    private void setHasAlarm(String cmd) {
        // 有闹钟，已设置
        AlarmBean alarmBean = new AlarmBean();
        String cmdStatus = cmd.substring(16, 18);
        // 开关
        if (cmdStatus.equals("0F") || cmdStatus.equals("1F")) {
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
            if (charAt == '1') {
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
        alarmBean.setMusicVal(cmdRing);
        Prefer.getInstance().setAlarm(deviceAddress, alarmBean);

        EventBus.getDefault().post(new AudioEvent(true));
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
            ToastUtils.showToast(DianDongSetActivity.this, getString(R.string.device_no_connected));
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
                ToastUtils.showToast(DianDongSetActivity.this, R.string.device_disconnect);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (WEEK_REQUEST_CODE == requestCode && resultCode == RESULT_CODE) {
            weekCheckBeanMap = (HashMap<Integer, Boolean>) data.getSerializableExtra(WeekActivity.EXTRA_KEY);
            setWeek();
        } else if (MODE_REQUEST_CODE == requestCode && resultCode == ModeActivity.RESULT_CODE) {
            modeCode = data.getStringExtra(ModeActivity.EXTRA_KEY);
            modeTV.setText(getModeStrByCode(modeCode));
        }
    }

    public String defaultIfEmpty(String value, String defaultStr) {
        if (TextUtils.isEmpty(value)) {
            return defaultStr;
        }
        return value;
    }

    public String getModeStrByCode(String mode) {
        if (mode.equals("01")) {
            return getString(R.string.alarm_mode_lingyali);
        }
        if (mode.equals("02")) {
            return getString(R.string.alarm_mode_jiyi1);
        }
        if (mode.equals("04")) {
            return getString(R.string.alarm_mode_lingyali_left);
        }
        if (mode.equals("05")) {
            return getString(R.string.alarm_mode_lingyali_right);
        }
        if (mode.equals("06")) {
            return getString(R.string.alarm_mode_lingyali);
        }
        return getString(R.string.alarm_mode_budongzuo);
    }


    public static TreeMap<Integer, Boolean> sortMapByKey(HashMap<Integer, Boolean> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        TreeMap<Integer, Boolean> sortMap = new TreeMap<>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    private static class MapKeyComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }
}