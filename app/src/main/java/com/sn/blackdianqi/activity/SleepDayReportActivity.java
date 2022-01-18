package com.sn.blackdianqi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseBlueActivity;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.DateUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.MotionBedUtil;
import com.sn.blackdianqi.view.LoggerView;
import com.sn.blackdianqi.view.TranslucentActionBar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * 折线图的使用：https://blog.csdn.net/weixin_43670802/article/details/100996792
 * https://www.jianshu.com/p/90a544b739b1
 * 日报告页面
 * Created by xiayundong on 2022/1/9.
 */
public class SleepDayReportActivity extends BaseBlueActivity implements TranslucentActionBar.ActionBarClickListener {

    public static String TYPE_EXTRA_KEY = "TYPE_EXTRA_KEY";
    public static String DATE_EXTRA_KEY = "DATE_EXTRA_KEY";
    public static String UV_EXTRA_KEY = "UV_EXTRA_KEY";
    public static String OZ_EXTRA_KEY = "OZ_EXTRA_KEY";


    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;
    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.tv_zcsj_time)
    TextView tv_zcsj_time;
    @BindView(R.id.tv_zcsj_time_unit)
    TextView tv_zcsj_time_unit;
    @BindView(R.id.tv_fscs_count)
    TextView tv_fscs_count;
    @BindView(R.id.tv_fscs_count_unit)
    TextView tv_fscs_count_unit;
    @BindView(R.id.tv_ctsj_time)
    TextView tv_ctsj_time;
    @BindView(R.id.tv_ctsj_time_unit)
    TextView tv_ctsj_time_unit;
    @BindView(R.id.tv_ptsj_time)
    TextView tv_ptsj_time;
    @BindView(R.id.tv_ptsj_time_unit)
    TextView tv_ptsj_time_unit;

    @BindView(R.id.chart)
    LineChartView chartView;

    @BindView(R.id.tv_line_title)
    TextView tv_line_title;

    @BindView(R.id.tv_btn_pre)
    TextView tv_btn_pre;
    @BindView(R.id.tv_btn_next)
    TextView tv_btn_next;


    // 0 实时数据 、1 日报告
    private String type = "0";
    // 日期
    private String date = "";
    // 相差的天数
    private String UV = "00";
    // 选择的日期
    private String OZ = "00";

    private String[] preCategories = new String[]{"12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};
    private String[] midCategories = new String[]{"20:00", "21:00", "22:00", "23:00", "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00"};
    private String[] nextCategories = new String[]{"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00"};

    private int[] preDateVal = new int[12];
    private int[] midDateVal = new int[12];
    private int[] nextDateVal = new int[12];

    // 24小时统计数据
    private int[] dataVal = new int[24];
    // 当前显示的曲线是
    private String showCurrent = "middle";  // pre middle next

    private LineChartData lineData;


    @Override
    protected void onDestroy() {
        unregisterReceiver(mDataEntryReceiver);
        sendCmd("FFFFFFFF02000A0A1204");
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(mDataEntryReceiver, makeGattUpdateIntentFilter());
        setContentView(R.layout.activity_sleep_day_report);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.sdr_action_bar_title), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
        type = getIntent().getStringExtra(TYPE_EXTRA_KEY);
        if (type == null) {
            type = "0";
        }
        date = getIntent().getStringExtra(DATE_EXTRA_KEY);
        UV = getIntent().getStringExtra(UV_EXTRA_KEY);
        OZ = getIntent().getStringExtra(OZ_EXTRA_KEY);
        LoggerView.e("type: " + type + " date:" + date + " UV:" + UV + " OZ:" + OZ);
        initView();
        generateInitialLineData();
        splitDataByTime();
        initData();
        sendInitCmd();

//        actionBar.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                preDateVal = new int[]{1, 20, 20, 12, 15, 6, 8, 9, 4, 21, 24, 1};
//                midDateVal = new int[]{4, 20, 20, 14, 15, 6, 8, 8, 4, 21, 24, 5};
//                nextDateVal = new int[]{8, 20, 20, 8, 15, 6, 8, 9, 4, 21, 24, 17};
//                resetLineChartDate(midDateVal, midCategories);
//            }
//        }, 3000);
    }

    private void initData() {
        if (type.equals("1")) {
            // 日报告
            actionBar.setTitle(getString(R.string.sdr_action_bar_title));
            title.setText(getString(R.string.sdr_action_bar_title));
            tv_zcsj_time_unit.setText(R.string.sdr_time_h);
            tv_ctsj_time_unit.setText(R.string.sdr_time_h);
            tv_ptsj_time_unit.setText(R.string.sdr_time_h);
            tv_line_title.setText(date);
        } else {
            // 实时数据
            actionBar.setTitle(getString(R.string.sdr_action_bar_real_title));
            title.setText(getString(R.string.sdr_action_bar_real_title));
            tv_zcsj_time_unit.setText(R.string.sdr_time_m);
            tv_ctsj_time_unit.setText(R.string.sdr_time_m);
            tv_ptsj_time_unit.setText(R.string.sdr_time_m);
            String currentDay = DateUtils.getCurrentDay();
            tv_line_title.setText(String.format(getString(R.string.sdr_line_title), currentDay));
        }
    }

    private void sendInitCmd() {
        StringBuilder sb = new StringBuilder();
        if (type.equals("1")) {
            sb.append("FFFFFFFF0200130B");
            sb.append(UV);
        } else {
            sb.append("FFFFFFFF0200030B01");
        }
        sb.append(BlueUtils.makeChecksum(sb.toString()));
        sendCmd(sb.toString());
    }


    private void initView() {
        setLineChartView();
        if (type.equals("1")) {
            tv_btn_pre.setVisibility(View.GONE);
            tv_btn_next.setVisibility(View.GONE);
        }
        tv_btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showCurrent.equals("middle")) {
                    showCurrent = "pre";
                    resetLineChartDate(preDateVal, preCategories);
                    tv_btn_pre.setEnabled(false);
                } else if (showCurrent.equals("next")) {
                    showCurrent = "middle";
                    resetLineChartDate(midDateVal, midCategories);
                    tv_btn_next.setEnabled(true);
                }
            }
        });
        tv_btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showCurrent.equals("middle")) {
                    showCurrent = "next";
                    resetLineChartDate(nextDateVal, nextCategories);
                    tv_btn_next.setEnabled(false);
                } else if (showCurrent.equals("pre")) {
                    showCurrent = "middle";
                    resetLineChartDate(midDateVal, midCategories);
                    tv_btn_pre.setEnabled(true);
                }
            }
        });
    }

    private void setLineChartView() {
        // 不支持缩放
        chartView.setZoomEnabled(false);
        chartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {

            }

            @Override
            public void onValueDeselected() {

            }
        });
    }

    /**
     * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
     * will select value on column chart.
     */
    private void generateInitialLineData() {
        int numValues = 12;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            values.add(new PointValue(i, 0));
            AxisValue axisValue = new AxisValue(i);
            axisValue.setLabel(midCategories[i]);
            axisValues.add(axisValue);
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setTextSize(6));
        lineData.setAxisYLeft(new Axis().setHasLines(true));

        chartView.setLineChartData(lineData);

        // For build-up animation you have to disable viewport recalculation.
        chartView.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, 26, 12, 0);
        chartView.setMaximumViewport(v);
        chartView.setCurrentViewport(v);

        chartView.setZoomType(ZoomType.HORIZONTAL);
    }


    private void resetLineChartDate(int[] charDataVal, String[] axisXArray) {
        chartView.cancelDataAnimation();
        LineChartData lineChartData = chartView.getLineChartData();
        Line line = lineChartData.getLines().get(0);
        Axis axisX = lineChartData.getAxisXBottom();

        for (int i = 0; i < line.getValues().size(); i++) {
            line.getValues().get(i).setTarget(line.getValues().get(i).getX(), charDataVal[i]);
            axisX.getValues().get(i).setLabel(axisXArray[i]);
        }
        chartView.startDataAnimation(300);
    }

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {
    }


    private void handleReceiveData(String cmd) {
        if (!cmd.contains("FFFFFFFF0200")) {
            // 实时在床会回复三帧数据
            return;
        }
        String askType = cmd.substring(12, 14);
        if (!(askType.equals("04") || askType.equals("05") || askType.equals("14"))) {
            return;
        }
        String frameNo = cmd.substring(16, 18);
        if (frameNo.equals("01") || frameNo.equals("06") || frameNo.equals("3C")) {
            String ptTimeParam = "";
            String ctTimeParam = "";
            String smTimeParam = "";
            String fsCountParam = "";

            int pingtangTimeVal = BlueUtils.covert16TO10(cmd.substring(18, 20));
            int cetangTimeVal = BlueUtils.covert16TO10(cmd.substring(20, 22));
            BigDecimal shuimianTimeVal = new BigDecimal(pingtangTimeVal).add(new BigDecimal(cetangTimeVal));
            fsCountParam = BlueUtils.covert16TO10(cmd.substring(22, 24)) + "";
            if (type.equals("01")) {
                ptTimeParam = new BigDecimal(pingtangTimeVal).multiply(new BigDecimal(0.1)).toString();
                ctTimeParam = new BigDecimal(pingtangTimeVal).multiply(new BigDecimal(0.1)).toString();
                smTimeParam = shuimianTimeVal.toString();
            } else {
                if (frameNo.equals("01")) {
                    ptTimeParam = pingtangTimeVal + "";
                    ctTimeParam = cetangTimeVal + "";
                    smTimeParam = shuimianTimeVal.toString();
                } else if (frameNo.equals("06")) {
                    ptTimeParam = new BigDecimal(pingtangTimeVal).multiply(new BigDecimal(0.1)).toString();
                    ctTimeParam = new BigDecimal(pingtangTimeVal).multiply(new BigDecimal(0.1)).toString();
                    shuimianTimeVal = new BigDecimal(ptTimeParam).add(new BigDecimal(ctTimeParam));
                    smTimeParam = shuimianTimeVal.toString();
                    tv_zcsj_time_unit.setText(R.string.sdr_time_h);
                    tv_ctsj_time_unit.setText(R.string.sdr_time_h);
                    tv_ptsj_time_unit.setText(R.string.sdr_time_h);
                } else if (frameNo.equals("3C")) {
                    ptTimeParam = pingtangTimeVal + "";
                    ctTimeParam = cetangTimeVal + "";
                    smTimeParam = shuimianTimeVal.toString();
                    tv_zcsj_time_unit.setText(R.string.sdr_time_h);
                    tv_ctsj_time_unit.setText(R.string.sdr_time_h);
                    tv_ptsj_time_unit.setText(R.string.sdr_time_h);
                }
            }
            tv_zcsj_time.setText(smTimeParam);
            tv_fscs_count.setText(fsCountParam);
            tv_ctsj_time.setText(ctTimeParam);
            tv_ptsj_time.setText(ptTimeParam);

            dataVal[0] = BlueUtils.covert16TO10(cmd.substring(24, 26)); // 12:00
            dataVal[1] = BlueUtils.covert16TO10(cmd.substring(26, 28)); // 13:00
            dataVal[2] = BlueUtils.covert16TO10(cmd.substring(28, 30)); // 14:00
            dataVal[3] = BlueUtils.covert16TO10(cmd.substring(30, 32)); // 15:00
            dataVal[4] = BlueUtils.covert16TO10(cmd.substring(32, 34)); // 16:00
            dataVal[5] = BlueUtils.covert16TO10(cmd.substring(34, 36)); // 17:00

        } else if (frameNo.equals("02")) {
            dataVal[6] = BlueUtils.covert16TO10(cmd.substring(18, 20)); // 18:00
            dataVal[7] = BlueUtils.covert16TO10(cmd.substring(20, 22)); // 19:00
            dataVal[8] = BlueUtils.covert16TO10(cmd.substring(22, 24)); // 20:00
            dataVal[9] = BlueUtils.covert16TO10(cmd.substring(24, 26)); // 21:00
            dataVal[10] = BlueUtils.covert16TO10(cmd.substring(26, 28)); // 22:00
            dataVal[11] = BlueUtils.covert16TO10(cmd.substring(28, 30)); // 23:00
            dataVal[12] = BlueUtils.covert16TO10(cmd.substring(30, 32)); // 00:00
            dataVal[13] = BlueUtils.covert16TO10(cmd.substring(32, 34)); // 01:00
            dataVal[14] = BlueUtils.covert16TO10(cmd.substring(34, 36)); // 02:00

        } else if (frameNo.equals("03")) {
            dataVal[15] = BlueUtils.covert16TO10(cmd.substring(18, 20)); // 03:00
            dataVal[16] = BlueUtils.covert16TO10(cmd.substring(20, 22)); // 04:00
            dataVal[17] = BlueUtils.covert16TO10(cmd.substring(22, 24)); // 05:00
            dataVal[18] = BlueUtils.covert16TO10(cmd.substring(24, 26)); // 06:00
            dataVal[19] = BlueUtils.covert16TO10(cmd.substring(26, 28)); // 07:00
            dataVal[20] = BlueUtils.covert16TO10(cmd.substring(28, 30)); // 08:00
            dataVal[21] = BlueUtils.covert16TO10(cmd.substring(30, 32)); // 09:00
            dataVal[22] = BlueUtils.covert16TO10(cmd.substring(32, 34)); // 10:00
            dataVal[23] = BlueUtils.covert16TO10(cmd.substring(34, 36)); // 11:00


            splitDataByTime();
        }

    }


    private void splitDataByTime() {
        // 分割数据
        if (type.equals("1")) {
            // 日报告
            if (OZ.equals("00")) {
                midDateVal = MotionBedUtil.splitArray(dataVal, 8, 19);
                midCategories = new String[]{"20:00", "21:00", "22:00", "23:00", "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00"};
            } else if (OZ.equals("01")) {
                midDateVal = MotionBedUtil.splitArray(dataVal, 9, 20);
                midCategories = new String[]{"21:00", "22:00", "23:00", "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00"};
            } else if (OZ.equals("02")) {
                midDateVal = MotionBedUtil.splitArray(dataVal, 10, 21);
                midCategories = new String[]{"22:00", "23:00", "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00"};
            } else if (OZ.equals("03")) {
                midDateVal = MotionBedUtil.splitArray(dataVal, 11, 22);
                midCategories = new String[]{"23:00", "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00"};
            } else if (OZ.equals("04")) {
                midDateVal = MotionBedUtil.splitArray(dataVal, 12, 23);
                midCategories = new String[]{"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00"};
            }
            resetLineChartDate(midDateVal, midCategories);
        } else {
            // 实时数据
            preDateVal = MotionBedUtil.splitArray(dataVal, 0, 11);
            midDateVal = MotionBedUtil.splitArray(dataVal, 8, 19);
            preDateVal = MotionBedUtil.splitArray(dataVal, 12, 23);
            resetLineChartDate(midDateVal, midCategories);
        }

    }

    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mDataEntryReceiver = new BroadcastReceiver() {
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
                        LogUtils.e("==睡姿特征数据录入  接收设备返回的数据==", data);
                        data = data.replace(" ", "").toUpperCase();
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
