package com.sn.blackdianqi.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.view.LoggerView;
import com.sn.blackdianqi.view.TranslucentActionBar;
import com.sn.blackdianqi.view.WeekItemView;

import java.util.logging.Logger;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 星期选择界面
 * Created by xiayundong on 2021/9/21.
 */
public class Mode2Activity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {


    private String checkMode;
    private String tempCheckMode;

    public static int RESULT_CODE = 109;
    public static String EXTRA_KEY = "MODE_EXTRA_KEY";


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
        setContentView(R.layout.activity_mode2);
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
        if (checkMode.equals("04")) {
            lingyaliLeft.setSelected(true);
        } else if (checkMode.equals("05")) {
            lingyaliRight.setSelected(true);
        } else if (checkMode.equals("06")) {
            lingyaliLeft.setSelected(true);
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
                lingyaliLeft.setSelected(!lingyaliLeft.getSelected());
                setCheckMode();
                break;
            case R.id.wiv_lingyali_right:
                lingyaliRight.setSelected(!lingyaliRight.getSelected());
                setCheckMode();
                break;
            case R.id.ll_save:
                save();
                break;
        }
    }

    private void setCheckMode() {
        LoggerView.e("setCheckMode","left:"+lingyaliLeft.getSelected()+" right:"+lingyaliRight.getSelected());
        if (lingyaliLeft.getSelected() && lingyaliRight.getSelected()) {
            tempCheckMode = "06";
        } else if (lingyaliLeft.getSelected() && !lingyaliRight.getSelected()){
            tempCheckMode = "04";
        } else if (!lingyaliLeft.getSelected() && lingyaliRight.getSelected()) {
            tempCheckMode = "05";
        } else {
            tempCheckMode = "03";
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
