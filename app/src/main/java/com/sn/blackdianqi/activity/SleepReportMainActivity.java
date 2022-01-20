package com.sn.blackdianqi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
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
import com.sn.blackdianqi.util.Prefer;
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
 * TODO 添加顶部按钮点击隐藏和显示实时数据功能
 * Created by xiayundong on 2022/1/4.
 */
public class SleepReportMainActivity extends BaseBlueActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    public static final int TIMER_REQUEST_CODE = 109;

    @BindView(R.id.tv_title)
    TextView tv_title;

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
        actionBar.setData(null, R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
        initView();
        initCalendarView();
        sendInitCmd();


    }

    private void initView() {
        if (Prefer.getInstance().getShowShishiData()) {
            shsjView.setVisibility(View.VISIBLE);
        }
        monthView.setOnClickListener(this);
        timeView.setOnClickListener(this);
        shsjView.setOnClickListener(this);
        tv_title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (MotionEvent.ACTION_DOWN == action) {
                    timeHandler.sendEmptyMessageDelayed(TITLE_WHAT, 5000);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(TITLE_WHAT);
                }
                return true;
            }
        });
    }

    private static final int TITLE_WHAT = 1;
    /**
     * 长按title
     */
    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TITLE_WHAT:
                    titleLongClick();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * title长按事件
     */
    private void titleLongClick() {
        boolean showShishi = Prefer.getInstance().getShowShishiData();
        if (showShishi) {
            Prefer.getInstance().setShowShishiData(false);
            shsjView.setVisibility(View.GONE);
        } else {
            Prefer.getInstance().setShowShishiData(true);
            shsjView.setVisibility(View.VISIBLE);
        }
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
        preMonthCal.add(Calendar.DATE, -28);
        int preMonthYear = preMonthCal.get(Calendar.YEAR);
        int preMonthMonth = preMonthCal.get(Calendar.MONTH) + 1;
        int preMonthDay = preMonthCal.get(Calendar.DATE);

        calendarView.scrollToCurrent();
        calendarView.setRange(preMonthYear, preMonthMonth, preMonthDay - 1, currentYear, currentMonth, currentDay);
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                LoggerView.d(year + "-" + month);
                setMonthTitle(year, month);
            }
        });
        calendarView.setOnCalendarInterceptListener(new CalendarView.OnCalendarInterceptListener() {
            @Override
            public boolean onCalendarIntercept(com.haibin.calendarview.Calendar calendar) {
                return true;
            }

            @Override
            public void onCalendarInterceptClick(com.haibin.calendarview.Calendar calendar, boolean isClick) {
                String currentDay = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay();
                long differDays = DateUtils.betweenDay(DateUtils.calendar(calendar.getYear(), calendar.getMonth(), calendar.getDay()), DateUtils.calendar(new Date()));
                if (differDays > 30 || differDays < 1) {
                    return;
                }
                differDays = differDays - 1;
                Intent dayIntent = new Intent();
                dayIntent.setClass(SleepReportMainActivity.this, SleepDayReportActivity.class);
                dayIntent.putExtra(SleepDayReportActivity.TYPE_EXTRA_KEY, "1");
                dayIntent.putExtra(SleepDayReportActivity.DATE_EXTRA_KEY, currentDay);
                dayIntent.putExtra(SleepDayReportActivity.UV_EXTRA_KEY, differDays < 10 ? "0" + differDays : differDays + "");
                dayIntent.putExtra(SleepDayReportActivity.OZ_EXTRA_KEY, sleepTimer);

                startActivity(dayIntent);
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
                shsjIntent.putExtra(SleepDayReportActivity.TYPE_EXTRA_KEY, "0");
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
