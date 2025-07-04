package com.sn.blackdianqi.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.bean.LengNuanTimeBean;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.view.TranslucentActionBar;
import com.sn.blackdianqi.view.WeekItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 冷暖档位选择界面
 * Created by xiayundong on 2021/9/21.
 */
public class GearActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {


    public static int RESULT_CODE = 109;
    public static String EXTRA_KEY = "GEAR_EXTRA_KEY";

    int selectGear = 0;

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.gear1)
    WeekItemView gear1;
    @BindView(R.id.gear2)
    WeekItemView gear2;
    @BindView(R.id.gear3)
    WeekItemView gear3;
    @BindView(R.id.gear4)
    WeekItemView gear4;

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
        setContentView(R.layout.activity_gear);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.alarm_mode), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
        initView();

        LengNuanTimeBean lengNuanTimeBean = Prefer.getInstance().getLengNuanTime(Prefer.getInstance().getLatelyConnectedDevice());
        if (lengNuanTimeBean != null) {
            String gear = lengNuanTimeBean.getGear();
            if (!TextUtils.isEmpty(gear)) {
                selectGear = Integer.parseInt(gear);
                setGear(Integer.parseInt(gear));
            }
        }
    }

    public void setGear(int gear) {
        switch (gear) {
            case 1:
                selectGear = 1;
                gear1.setSelected(true);
                gear2.setSelected(false);
                gear3.setSelected(false);
                gear4.setSelected(false);
                break;
            case 2:
                selectGear = 2;
                gear1.setSelected(false);
                gear2.setSelected(true);
                gear3.setSelected(false);
                gear4.setSelected(false);
                break;
            case 3:
                selectGear = 3;
                gear1.setSelected(false);
                gear2.setSelected(false);
                gear3.setSelected(true);
                gear4.setSelected(false);
                break;
            case 4:
                selectGear = 4;
                gear1.setSelected(false);
                gear2.setSelected(false);
                gear3.setSelected(false);
                gear4.setSelected(true);
                break;
        }
    }

    private void initView() {
        gear1.setOnClickListener(this);
        gear2.setOnClickListener(this);
        gear3.setOnClickListener(this);
        gear4.setOnClickListener(this);
        saveLL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gear1:
                setGear(1);
                break;
            case R.id.gear2:
                setGear(2);
                break;
            case R.id.gear3:
                setGear(3);
                break;
            case R.id.gear4:
                setGear(4);
                break;
            case R.id.ll_save:
                save();
                break;
        }
    }


    private void save() {
        Intent intent = getIntent();
        intent.putExtra(EXTRA_KEY, selectGear);
        setResult(RESULT_CODE, intent);
        finish();
    }

}
