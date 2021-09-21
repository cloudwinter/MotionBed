package com.sn.blackdianqi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gzuliyujiang.wheelpicker.DatePicker;
import com.github.gzuliyujiang.wheelpicker.OptionPicker;
import com.github.gzuliyujiang.wheelpicker.TimePicker;
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode;
import com.github.gzuliyujiang.wheelpicker.annotation.TimeMode;
import com.github.gzuliyujiang.wheelpicker.contract.OnDatePickedListener;
import com.github.gzuliyujiang.wheelpicker.contract.OnTimePickedListener;
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity;
import com.github.gzuliyujiang.wheelpicker.impl.SimpleTimeFormatter;
import com.github.gzuliyujiang.wheelpicker.impl.UnitDateFormatter;
import com.github.gzuliyujiang.wheelpicker.impl.UnitTimeFormatter;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.TranslucentActionBar;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 闹钟界面
 * Created by xiayundong on 2021/9/20.
 */
public class AlarmActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    public static final String TAG = "AlarmActivity";

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.cb_switch)
    CheckBox switchCB;

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

    @BindView(R.id.cb_anmo)
    CheckBox anmoCB;

    @BindView(R.id.cb_xinagling)
    CheckBox xinaglingCB;

    @BindView(R.id.ll_save)
    LinearLayout saveLL;


    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {}


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
    }


    private void initView() {
        timeLL.setOnClickListener(this);
        weekLL.setOnClickListener(this);
        modeLL.setOnClickListener(this);
        saveLL.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_time:
                TimePicker picker = new TimePicker(this);
                picker.getWheelLayout().setTimeMode(TimeMode.HOUR_24_NO_SECOND);
                picker.getWheelLayout().setTimeFormatter(new SimpleTimeFormatter());
                picker.getWheelLayout().setRange(TimeEntity.target(0,0,0),TimeEntity.target(23, 59, 59));
                picker.getWheelLayout().setDefaultValue(TimeEntity.now());
                picker.setOnTimePickedListener(new OnTimePickedListener() {
                    @Override
                    public void onTimePicked(int hour, int minute, int second) {
                        ToastUtils.showToast(AlarmActivity.this, hour + ":" + minute + ":" + second);
                    }
                });
                picker.show();
                break;
            case R.id.ll_week:

                // TODO
                break;
            case R.id.ll_mode:
                OptionPicker weekPicker = new OptionPicker(this);
                weekPicker.setData("星期一", "样式1-屏幕底部弹窗", "样式2-屏幕底部弹窗", "样式3-屏幕中间弹窗");
                // TODO
                break;
            case R.id.ll_save:
                // TODO
                break;
            default:
                break;
        }
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
}
