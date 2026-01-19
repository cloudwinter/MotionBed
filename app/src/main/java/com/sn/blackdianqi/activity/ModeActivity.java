package com.sn.blackdianqi.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.view.TranslucentActionBar;
import com.sn.blackdianqi.view.WeekItemView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 星期选择界面
 * Created by xiayundong on 2021/9/21.
 */
public class ModeActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {


    private String checkMode;
    private String tempCheckMode;

    public static int RESULT_CODE = 109;
    public static String EXTRA_KEY = "MODE_EXTRA_KEY";


    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.wiv_lingyali)
    WeekItemView lingyali;
    @BindView(R.id.wiv_jiyi1)
    WeekItemView yiji1;
    @BindView(R.id.wiv_budongzuo)
    WeekItemView budongzuo;

    @BindView(R.id.ll_save)
    LinearLayout saveLL;

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.alarm_mode), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
        initView();
        checkMode = getIntent().getStringExtra(EXTRA_KEY);
        tempCheckMode = checkMode;
        if (checkMode.equals("01")) {
            lingyali.setSelected(true);
        } else if (checkMode.equals("02")) {
            yiji1.setSelected(true);
        } else if (checkMode.equals("03")) {
            budongzuo.setSelected(true);
        }

    }

    private void initView() {
        lingyali.setOnClickListener(this);
        yiji1.setOnClickListener(this);
        budongzuo.setOnClickListener(this);

        saveLL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        v.setSelected(true);
        switch (v.getId()) {
            case R.id.wiv_lingyali:
                tempCheckMode = "01";
                yiji1.setSelected(false);
                budongzuo.setSelected(false);
                break;
            case R.id.wiv_jiyi1:
                tempCheckMode = "02";
                lingyali.setSelected(false);
                budongzuo.setSelected(false);
                break;
            case R.id.wiv_budongzuo:
                tempCheckMode = "03";
                lingyali.setSelected(false);
                yiji1.setSelected(false);
                break;
            case R.id.ll_save:
                save();
                break;
        }
    }

    private void save() {
        checkMode = tempCheckMode;
        Intent intent = getIntent();
        intent.putExtra(EXTRA_KEY, checkMode);
        setResult(RESULT_CODE, intent);
        finish();
    }

}
