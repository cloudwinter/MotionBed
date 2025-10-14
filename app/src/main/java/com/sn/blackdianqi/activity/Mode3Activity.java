package com.sn.blackdianqi.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.bean.LengNuanTimeBean;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.view.LoggerView;
import com.sn.blackdianqi.view.TranslucentActionBar;
import com.sn.blackdianqi.view.WeekItemView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 冷暖模式选择界面
 * Created by xiayundong on 2021/9/21.
 */
public class Mode3Activity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {


    public static int RESULT_CODE = 109;
    public static String EXTRA_KEY = "MODE_EXTRA_KEY";

    String checkMode = "00";

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.wiv_lingyali_left)
    WeekItemView lingyaliLeft;
    @BindView(R.id.wiv_lingyali_right)
    WeekItemView lingyaliRight;

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
        setContentView(R.layout.activity_mode3);
        ButterKnife.bind(this);
        checkMode = getIntent().getStringExtra("modeCode");
        // 设置title
        actionBar.setData(getString(R.string.moshi), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
        initView();

        if (checkMode.equals("01")) {
            lingyaliLeft.setSelected(true);
            lingyaliRight.setSelected(false);
        } else if (checkMode.equals("02")) {
            lingyaliLeft.setSelected(false);
            lingyaliRight.setSelected(true);
        } else {
            lingyaliLeft.setSelected(false);
            lingyaliRight.setSelected(false);
        }
    }

    private void initView() {
        lingyaliLeft.setOnClickListener(this);
        lingyaliRight.setOnClickListener(this);
        saveLL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wiv_lingyali_left:
                lingyaliLeft.setSelected(true);
                lingyaliRight.setSelected(false);
                checkMode = "01";
                break;
            case R.id.wiv_lingyali_right:
                lingyaliLeft.setSelected(false);
                lingyaliRight.setSelected(true);
                checkMode = "02";
                break;
            case R.id.ll_save:
                save();
                break;
        }
    }


    private void save() {
        Intent intent = getIntent();
        intent.putExtra(EXTRA_KEY, checkMode);
        setResult(RESULT_CODE, intent);
        finish();
    }

}
