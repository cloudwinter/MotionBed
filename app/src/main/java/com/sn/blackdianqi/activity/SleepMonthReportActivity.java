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
import com.sn.blackdianqi.view.TranslucentActionBar;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiayundong on 2022/1/9.
 */
public class SleepMonthReportActivity extends BaseBlueActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;
    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.tv_pjzc_time)
    TextView tv_pjzc_time;
    @BindView(R.id.tv_zczc_time)
    TextView tv_zczc_time;
    @BindView(R.id.tv_zdzc_time)
    TextView tv_zdzc_time;

    @BindView(R.id.tv_pjfs_count)
    TextView tv_pjfs_count;
    @BindView(R.id.tv_zdfs_count)
    TextView tv_zdfs_count;
    @BindView(R.id.tv_zsfs_count)
    TextView tv_zsfs_count;

    @BindView(R.id.tv_pjpt_time)
    TextView tv_pjpt_time;
    @BindView(R.id.tv_pjct_time)
    TextView tv_pjct_time;


    @Override
    protected void onDestroy() {
        unregisterReceiver(mMonthReportReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(mMonthReportReceiver, makeGattUpdateIntentFilter());
        setContentView(R.layout.activity_sleep_month_report);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.smr_action_bar_title), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }

        sendInitCmd();
    }


    private void sendInitCmd() {
        StringBuilder builder = new StringBuilder("FFFFFFFF0200030B1E");
        String sum = BlueUtils.makeChecksum(builder.toString());
        builder.append(sum);
        String cmd = builder.toString();
        sendCmd(cmd);
    }


    private void handleReceiveData(String cmd) {
        if (!cmd.contains("FFFFFFFF0200")) {
            return;
        }
        String days = cmd.substring(16, 18);
        title.setText(String.format(getString(R.string.smr_top_title_format), days));

        // 在床时间
        String pjzcTime = cmd.substring(18, 20);
        String zczcTime = cmd.substring(20, 22);
        String zdzcTime = cmd.substring(22, 24);
        tv_pjzc_time.setText(DateUtils.transferToH(pjzcTime, 6));
        tv_zczc_time.setText(DateUtils.transferToH(zczcTime, 6));
        tv_zdzc_time.setText(DateUtils.transferToH(zdzcTime, 6));

        // 翻身次数
        String pjfsNum = cmd.substring(24, 26);
        String maxfsNum = cmd.substring(26, 28);
        String minfsNum = cmd.substring(28, 30);
        tv_pjfs_count.setText(pjfsNum);
        tv_zdfs_count.setText(maxfsNum);
        tv_zsfs_count.setText(minfsNum);

        // 平躺时间/侧躺时间
        String ptTime = cmd.substring(30, 32);
        String ctTime = cmd.substring(32, 34);
        tv_pjpt_time.setText(DateUtils.transferToH(ptTime, 6));
        tv_pjct_time.setText(DateUtils.transferToH(ctTime, 6));

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
                        LogUtils.e("==月报告  接收设备返回的数据==", data);
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



    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() { }

    @Override
    public void onClick(View v) { }
}
