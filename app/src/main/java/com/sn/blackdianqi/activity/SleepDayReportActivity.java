package com.sn.blackdianqi.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseBlueActivity;
import com.sn.blackdianqi.view.TranslucentActionBar;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 折线图的使用：https://blog.csdn.net/weixin_43670802/article/details/100996792
 * 日报告页面
 * Created by xiayundong on 2022/1/9.
 */
public class SleepDayReportActivity extends BaseBlueActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    public static String EXTRA_KEY = "TYPE_EXTRA_KEY";

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

    // 0 实时数据 、1 日报告
    private String type = "0";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_month_report);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.smr_action_bar_title), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
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
