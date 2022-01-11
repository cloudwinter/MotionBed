package com.sn.blackdianqi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.haibin.calendarview.CalendarView;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseBlueActivity;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.DateUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.LoggerView;
import com.sn.blackdianqi.view.ProlateItemView;
import com.sn.blackdianqi.view.TranslucentActionBar;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 睡姿报告入口页面
 * Created by xiayundong on 2022/1/4.
 */
public class SleepReportMainActivity extends BaseBlueActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    public static final int TIMER_REQUEST_CODE = 109;

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;
    @BindView(R.id.tv_month)
    TextView tv_month;
    @BindView(R.id.calendarView)
    CalendarView calendarView;
    @BindView(R.id.view_month)
    ProlateItemView monthView;
    @BindView(R.id.view_time)
    ProlateItemView timeView;
    @BindView(R.id.view_shsj)
    ProlateItemView shsjView;

    /**
     * 睡眠定时标记
     */
    String sleepTimer = "00";


    @Override
    protected void onDestroy() {
        unregisterReceiver(mMonthReportReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(mMonthReportReceiver, makeGattUpdateIntentFilter());
        setContentView(R.layout.activity_sleep_report_main);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.srm_action_bar_title), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
        initCalendarView();
        monthView.setOnClickListener(this);
        timeView.setOnClickListener(this);
        shsjView.setOnClickListener(this);

        sendInitCmd();
    }

    /**
     * 初始化日历view
     */
    private void initCalendarView() {
        Calendar calendar = DateUtils.calendar(new Date());
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DATE);
        setMonthTitle(currentYear, currentMonth);

        Calendar preMonthCal = Calendar.getInstance();
        preMonthCal.add(Calendar.MONTH, -1);
        int preMonthYear = preMonthCal.get(Calendar.YEAR);
        int preMonthMonth = preMonthCal.get(Calendar.MONTH) + 1;
        int preMonthDay = preMonthCal.get(Calendar.DATE);

        calendarView.setRange(preMonthYear, preMonthMonth, preMonthDay - 1, currentYear, currentMonth, currentDay);
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                LoggerView.d(year + "-" + month);
                setMonthTitle(year, month);
            }
        });
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(com.haibin.calendarview.Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(com.haibin.calendarview.Calendar calendar, boolean isClick) {
                long differDays = DateUtils.betweenDay(DateUtils.calendar(calendar.getYear(), calendar.getMonth(), calendar.getDay()), DateUtils.calendar(new Date()));
                LoggerView.d(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay() + " 相差 " + differDays);
                if (differDays > 30 || differDays < 1) {
                    ToastUtils.showToast(SleepReportMainActivity.this,"您选择的日期暂无数据");
                    return;
                }
                // TODO

            }
        });
    }

    private void sendInitCmd() {
        // 入睡时间查询命令
        StringBuilder builder = new StringBuilder("FFFFFFFF0200160B00");
        builder.append(BlueUtils.makeChecksum(builder.toString()));
        sendCmd(builder.toString());
    }

    private void setMonthTitle(int year, int month) {
        StringBuilder builder = new StringBuilder();
        builder.append(year + "");
        builder.append("-");
        if (month < 10) {
            builder.append("0");
        }
        builder.append(month);
        tv_month.setText(builder.toString());
    }

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_month:
                Intent monthIntent = new Intent();
                monthIntent.setClass(this, SleepMonthReportActivity.class);
                startActivity(monthIntent);
                break;
            case R.id.view_time:
                Intent timeIntent = new Intent();
                timeIntent.setClass(this, SleepFallTimerSelectActivity.class);
                timeIntent.putExtra(SleepTimerSelectActivity.EXTRA_KEY, sleepTimer);
                startActivityForResult(timeIntent, TIMER_REQUEST_CODE);
                break;
            case R.id.view_shsj:
                Intent shsjIntent = new Intent();
                shsjIntent.setClass(this, SleepDayReportActivity.class);
                shsjIntent.putExtra(SleepDayReportActivity.EXTRA_KEY, "0");
                startActivity(shsjIntent);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TIMER_REQUEST_CODE && data != null) {
            String extraVal = data.getStringExtra(SleepTimerSelectActivity.EXTRA_KEY);
            if (!TextUtils.isEmpty(extraVal)) {
                sleepTimer = extraVal;
                setSleepTimerDesc(sleepTimer);
            }
        }
    }


    private void setSleepTimerDesc(String sleepTimer) {
        if (sleepTimer.equals("00")) {
            timeView.setDesc("20:00");
        } else if (sleepTimer.equals("01")) {
            timeView.setDesc("21:00");
        } else if (sleepTimer.equals("02")) {
            timeView.setDesc("22:00");
        } else if (sleepTimer.equals("03")) {
            timeView.setDesc("23:00");
        } else if (sleepTimer.equals("04")) {
            timeView.setDesc("24:00");
        }
    }

    private void handleReceiveData(String cmd) {
        if (cmd.contains("FFFFFFFF0200160B")) {
            // 入睡时间回码
            String OZ = cmd.substring(16, 18);
            sleepTimer = OZ;
            setSleepTimerDesc(OZ);
        }
    }


    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mMonthReportReceiver = new BroadcastReceiver() {
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
                        LogUtils.e("==睡眠报告  接收设备返回的数据==", data);
                        data = data.replace(" ", "");
                        handleReceiveData(data);
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


}
