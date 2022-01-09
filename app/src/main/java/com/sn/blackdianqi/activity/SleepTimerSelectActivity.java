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
 * 00 无定时
 * 01 20:00
 * 02 20:30
 * 03 21:00
 * 04 21:30
 * 05 22:00
 * 06 22:30
 * 07 23:00
 * 08 23:30
 * 智能睡眠定时选择界面
 * Created by xiayundong on 2021/9/21.
 */
public class SleepTimerSelectActivity extends BaseBlueActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    private String checkedTimer = "00";
    private String tempCheckedTimer = "00";

    public static int RESULT_CODE = 108;
    public static String EXTRA_KEY = "WEEK_EXTRA_KEY";


    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.v_0000)
    WeekItemView mItemView0000;
    @BindView(R.id.v_2000)
    WeekItemView mItemView2000;
    @BindView(R.id.v_2030)
    WeekItemView mItemView2030;
    @BindView(R.id.v_2100)
    WeekItemView mItemView2100;
    @BindView(R.id.v_2130)
    WeekItemView mItemView2130;
    @BindView(R.id.v_2200)
    WeekItemView mItemView2200;
    @BindView(R.id.v_2230)
    WeekItemView mItemView2230;
    @BindView(R.id.v_2300)
    WeekItemView mItemView2300;
    @BindView(R.id.v_2330)
    WeekItemView mItemView2330;

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
        setContentView(R.layout.activity_sleep_timer_select);
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
        itemViewMap.put("00",mItemView0000);
        itemViewMap.put("01",mItemView2000);
        itemViewMap.put("02",mItemView2030);
        itemViewMap.put("03",mItemView2100);
        itemViewMap.put("04",mItemView2130);
        itemViewMap.put("05",mItemView2200);
        itemViewMap.put("06",mItemView2230);
        itemViewMap.put("07",mItemView2300);
        itemViewMap.put("08",mItemView2330);
        mItemView0000.setOnClickListener(this);
        mItemView2000.setOnClickListener(this);
        mItemView2030.setOnClickListener(this);
        mItemView2100.setOnClickListener(this);
        mItemView2130.setOnClickListener(this);
        mItemView2200.setOnClickListener(this);
        mItemView2230.setOnClickListener(this);
        mItemView2300.setOnClickListener(this);
        mItemView2330.setOnClickListener(this);
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
            case R.id.v_0000:
                tempCheckedTimer = "00";
                selectItem("00");
                break;
            case R.id.v_2000:
                tempCheckedTimer = "01";
                selectItem("01");
                break;
            case R.id.v_2030:
                tempCheckedTimer = "02";
                selectItem("02");
                break;
            case R.id.v_2100:
                tempCheckedTimer = "03";
                selectItem("03");
                break;
            case R.id.v_2130:
                tempCheckedTimer = "04";
                selectItem("04");
                break;
            case R.id.v_2200:
                tempCheckedTimer = "05";
                selectItem("05");
                break;
            case R.id.v_2230:
                tempCheckedTimer = "06";
                selectItem("06");
                break;
            case R.id.v_2300:
                tempCheckedTimer = "07";
                selectItem("07");
                break;
            case R.id.v_2330:
                tempCheckedTimer = "08";
                selectItem("08");
                break;
            case R.id.ll_save:
                save();
                break;
        }
    }

    private void save() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FFFFFFFF02000D0B");
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
