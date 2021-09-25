package com.sn.blackdianqi.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.view.TranslucentActionBar;
import com.sn.blackdianqi.view.WeekItemView;

import java.util.HashMap;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 星期选择界面
 * Created by xiayundong on 2021/9/21.
 */
public class WeekActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {


    private HashMap<Integer,Boolean> weekCheckBeanMap = new HashMap<>();

    public static int RESULT_CODE = 108;
    public static String EXTRA_KEY = "WEEK_EXTRA_KEY";


    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.wiv_monday)
    WeekItemView weekMonday;
    @BindView(R.id.wiv_tuesday)
    WeekItemView weekTuesday;
    @BindView(R.id.wiv_wednesday)
    WeekItemView weekWednesday;
    @BindView(R.id.wiv_thursday)
    WeekItemView weekThursday;
    @BindView(R.id.wiv_friday)
    WeekItemView weekFriday;
    @BindView(R.id.wiv_saturday)
    WeekItemView weekSaturday;
    @BindView(R.id.wiv_sunday)
    WeekItemView weekSunday;

    @Override
    public void onLeftClick() {
        Intent intent = getIntent();
        intent.putExtra(EXTRA_KEY,weekCheckBeanMap);
        setResult(RESULT_CODE,intent);
        finish();
    }

    @Override
    public void onRightClick() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.week), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
        initView();
    }

    private void initView() {
        weekMonday.setOnClickListener(this);
        weekTuesday.setOnClickListener(this);
        weekWednesday.setOnClickListener(this);
        weekThursday.setOnClickListener(this);
        weekFriday.setOnClickListener(this);
        weekSaturday.setOnClickListener(this);
        weekSunday.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wiv_monday:
                weekCheckBeanMap.put(1, true);
            case R.id.wiv_tuesday:
                weekCheckBeanMap.put(2, true);
            case R.id.wiv_wednesday:
                weekCheckBeanMap.put(3, true);
            case R.id.wiv_thursday:
                weekCheckBeanMap.put(4, true);
            case R.id.wiv_friday:
                weekCheckBeanMap.put(5, true);
            case R.id.wiv_saturday:
                weekCheckBeanMap.put(6, true);
            case R.id.wiv_sunday:
                weekCheckBeanMap.put(7, true);
                v.setSelected(!v.isSelected());
                break;
        }
    }
}
