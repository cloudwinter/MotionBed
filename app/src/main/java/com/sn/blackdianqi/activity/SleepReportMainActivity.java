package com.sn.blackdianqi.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.haibin.calendarview.CalendarView;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseBlueActivity;
import com.sn.blackdianqi.view.TranslucentActionBar;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 睡姿报告入口页面
 * Created by xiayundong on 2022/1/4.
 */
public class SleepReportMainActivity extends BaseBlueActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;
    @BindView(R.id.calendarView)
    CalendarView calendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_report_main);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.sleep_timer_title), R.mipmap.ic_back, null, 0, null, this);
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
