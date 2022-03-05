package com.sn.blackdianqi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.gzuliyujiang.wheelpicker.OptionPicker;
import com.github.gzuliyujiang.wheelpicker.TimePicker;
import com.github.gzuliyujiang.wheelpicker.annotation.TimeMode;
import com.github.gzuliyujiang.wheelpicker.contract.OnOptionPickedListener;
import com.github.gzuliyujiang.wheelpicker.contract.OnTimePickedListener;
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity;
import com.github.gzuliyujiang.wheelpicker.impl.SimpleTimeFormatter;
import com.github.gzuliyujiang.wheelpicker.widget.TimeWheelLayout;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseBlueActivity;
import com.sn.blackdianqi.bean.AlarmBean;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.TranslucentActionBar;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sn.blackdianqi.activity.WeekActivity.RESULT_CODE;

/**
 * 闹钟界面
 * Created by xiayundong on 2021/9/20.
 */
public class AlarmActivity extends BaseBlueActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    public static final String TAG = "AlarmActivity";

    public static int WEEK_REQUEST_CODE = 107;
    public static int MODE_REQUEST_CODE = 106;

    private HashMap<Integer, Boolean> weekCheckBeanMap = new HashMap<>();

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.cb_switch)
    CheckBox switchCB;

    @BindView(R.id.ll_content)
    LinearLayout contentLL;

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

    @BindView(R.id.cb_xinagling)
    CheckBox xinaglingCB;

    @BindView(R.id.ll_save)
    LinearLayout saveLL;

    // 时间
    private String hourStr, minuteStr;
    // 01：零压力，02：记忆1，03：无动作
    private String modeCode = "03";

    String blueTitle = "";


    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mAlarmReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(mAlarmReceiver, makeGattUpdateIntentFilter());
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.alarm), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
        initView();
        initData();
    }


    private void initView() {
        // QMS2 不显示按摩开关
        switchCB.setChecked(true);
        switchCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    contentLL.setVisibility(View.VISIBLE);
                } else {
                    contentLL.setVisibility(View.GONE);
                }
            }
        });
        contentLL.setVisibility(View.GONE);

        timeLL.setOnClickListener(this);
        weekLL.setOnClickListener(this);
        modeLL.setOnClickListener(this);
        saveLL.setOnClickListener(this);

        DeviceBean deviceBean = Prefer.getInstance().getConnectedDevice();
        if (deviceBean != null && !TextUtils.isEmpty(deviceBean.getTitle())) {
            blueTitle = deviceBean.getTitle().toUpperCase();
            if (deviceBean.getTitle().toUpperCase().contains("QMS2")) {
                anmoLL.setVisibility(View.GONE);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //initData();
    }

    private void initData() {
        String deviceAddress = Prefer.getInstance().getLatelyConnectedDevice();
        if (TextUtils.isEmpty(deviceAddress)) {
            return;
        }
        AlarmBean alarmBean = Prefer.getInstance().getAlarm(deviceAddress);
        if (alarmBean == null) {
            contentLL.setVisibility(View.GONE);
            return;
        }
        contentLL.setVisibility(alarmBean.isAlarmSwitch() ? View.VISIBLE : View.GONE);
        hourStr = alarmBean.getHourStr();
        minuteStr = alarmBean.getMinuteStr();
        modeCode = alarmBean.getModeCode();
        weekCheckBeanMap = alarmBean.getWeekCheckBeanMap();

        switchCB.setChecked(alarmBean.isAlarmSwitch());
        if (!TextUtils.isEmpty(hourStr) && !TextUtils.isEmpty(minuteStr)) {
            timeTV.setText(hourStr + ":" + minuteStr);
        }
        modeTV.setText(getModeStrByCode(modeCode));
        anmoCB.setChecked(alarmBean.isAnmo());
        xinaglingCB.setChecked(alarmBean.isXiangling());
        setWeek();
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
                weekStr.append(entry.getKey());
                count++;
            }
        }
        weekTV.setText(weekStr.toString());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_time:
                TimePicker picker = new TimePicker(this);
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
                Intent intentWeek = new Intent(AlarmActivity.this, WeekActivity.class);
                intentWeek.putExtra(WeekActivity.EXTRA_KEY, weekCheckBeanMap);
                startActivityForResult(intentWeek, WEEK_REQUEST_CODE);
                break;
            case R.id.ll_mode:
                Intent intentMode = null;
                if (blueTitle.contains("QMS-DFQ") || blueTitle.contains("QMS-430") || blueTitle.contains("QMS-444")) {
                    intentMode = new Intent(AlarmActivity.this, Mode2Activity.class);
                } else {
                    intentMode = new Intent(AlarmActivity.this, ModeActivity.class);
                }
                intentMode.putExtra(ModeActivity.EXTRA_KEY, modeCode);
                startActivityForResult(intentMode, MODE_REQUEST_CODE);
                break;
            case R.id.ll_save:
                checkAndSend();
                break;
            default:
                break;
        }
    }


    /**
     * 发送蓝牙命令
     */
    private void checkAndSend() {
        if (!checkConnected()) {
            return;
        }
        if (switchCB.isChecked()) {
            if (TextUtils.isEmpty(hourStr)) {
                ToastUtils.showToast(AlarmActivity.this, "请选择时间");
                return;
            }
        }
        AlarmBean alarmBean = new AlarmBean();

        StringBuilder sb = new StringBuilder();
        sb.append("FFFFFFFF01000213");
        // 状态
        alarmBean.setAlarmSwitch(switchCB.isChecked());
        if (switchCB.isChecked()) {
            sb.append("01");
        } else {
            sb.append("A1");
        }

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
        alarmBean.setXiangling(xinaglingCB.isChecked());
        sb.append(xinaglingCB.isChecked() ? "01" : "00");

        // 校验和
        sb.append(BlueUtils.makeChecksum(sb.toString()));

        // 发送蓝牙命令
        sendCmd(sb.toString());
        Prefer.getInstance().setAlarm(Prefer.getInstance().getLatelyConnectedDevice(), alarmBean);
        finish();
    }


    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) { //发现GATT服务器
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //处理发送过来的数据  (//有效数据)
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String data = bundle.getString(BluetoothLeService.EXTRA_DATA);
                    if (data != null) {
                        LogUtils.e("==快捷  接收设备返回的数据==", data);
                        //handleReceiveData(data);
                    }
                }
            }
        }
    };


    /* 意图过滤器 */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

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
            return getString(R.string.alarm_mode_lingyali);
        }
        if (mode.equals("05")) {
            return getString(R.string.alarm_mode_lingyali);
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
