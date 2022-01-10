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
