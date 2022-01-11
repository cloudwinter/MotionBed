package com.sn.blackdianqi.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseBlueActivity;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.view.TranslucentActionBar;
import com.sn.blackdianqi.view.WeekItemView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 00 20:00
 * 01 21:00
 * 02 22:00
 * 03 23:00
 * 04 24:00
 * 智能睡眠定时选择界面
 * Created by xiayundong on 2021/9/21.
 */
public class SleepFallTimerSelectActivity extends BaseBlueActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    private String checkedTimer = "00";
    private String tempCheckedTimer = "00";

    public static int RESULT_CODE = 108;
    public static String EXTRA_KEY = "WEEK_EXTRA_KEY";


    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.v_2000)
    WeekItemView mItemView2000;
    @BindView(R.id.v_2100)
    WeekItemView mItemView2100;
    @BindView(R.id.v_2200)
    WeekItemView mItemView2200;
    @BindView(R.id.v_2300)
    WeekItemView mItemView2300;
    @BindView(R.id.v_2400)
    WeekItemView mItemView2400;

    @BindView(R.id.ll_save)
    LinearLayout saveLL;

    private Map<String, WeekItemView> itemViewMap = new HashMap<>();

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
        setContentView(R.layout.activity_sleep_fall_timer_select);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.sleep_timer_title), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
        initView();
        checkedTimer = getIntent().getStringExtra(EXTRA_KEY);
        tempCheckedTimer = checkedTimer;
        selectItem(checkedTimer);
    }

    private void initView() {
        itemViewMap.put("00",mItemView2000);
        itemViewMap.put("01",mItemView2100);
        itemViewMap.put("02",mItemView2200);
        itemViewMap.put("03",mItemView2300);
        itemViewMap.put("04",mItemView2400);
        mItemView2000.setOnClickListener(this);
        mItemView2100.setOnClickListener(this);
        mItemView2200.setOnClickListener(this);
        mItemView2300.setOnClickListener(this);
        mItemView2400.setOnClickListener(this);
        saveLL.setOnClickListener(this);
    }


    private void selectItem(String key) {
        Iterator<Map.Entry<String, WeekItemView>> it = itemViewMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, WeekItemView> entry = it.next();
            if (entry.getKey().equals(key)) {
                entry.getValue().setSelected(true);
            } else {
                entry.getValue().setSelected(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_2000:
                tempCheckedTimer = "00";
                selectItem("00");
                break;
            case R.id.v_2100:
                tempCheckedTimer = "01";
                selectItem("01");
                break;
            case R.id.v_2200:
                tempCheckedTimer = "02";
                selectItem("02");
                break;
            case R.id.v_2300:
                tempCheckedTimer = "03";
                selectItem("03");
                break;
            case R.id.v_2400:
                tempCheckedTimer = "04";
                selectItem("04");
                break;
            case R.id.ll_save:
                save();
                break;
        }
    }

    private void save() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FFFFFFFF0200150B");
        stringBuilder.append(checkedTimer);
        String checksum = BlueUtils.makeChecksum(stringBuilder.toString());
        stringBuilder.append(checksum);
        sendCmd(stringBuilder.toString());
        checkedTimer = tempCheckedTimer;
        Intent intent = getIntent();
        intent.putExtra(EXTRA_KEY, checkedTimer);
        setResult(RESULT_CODE, intent);
        finish();
    }

}
