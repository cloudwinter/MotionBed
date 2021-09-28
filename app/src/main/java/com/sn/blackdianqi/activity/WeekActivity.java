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
public class WeekActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {


    private HashMap<Integer, Boolean> weekCheckBeanMap = new HashMap<>();
    private HashMap<Integer, Boolean> tempWeekCheckBeanMap = new HashMap<>();

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
        setContentView(R.layout.activity_week);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.week), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
        initView();
        weekCheckBeanMap = (HashMap<Integer, Boolean>) getIntent().getSerializableExtra(EXTRA_KEY);
        tempWeekCheckBeanMap = weekCheckBeanMap;
        Iterator<Map.Entry<Integer, Boolean>> it = weekCheckBeanMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Boolean> entry = it.next();
            if (entry.getKey() == 1) {
                weekMonday.setSelected(true);
            } else if (entry.getKey() == 2) {
                weekTuesday.setSelected(true);
            } else if (entry.getKey() == 3) {
                weekWednesday.setSelected(true);
            } else if (entry.getKey() == 4) {
                weekThursday.setSelected(true);
            } else if (entry.getKey() == 5) {
                weekFriday.setSelected(true);
            } else if (entry.getKey() == 6) {
                weekSaturday.setSelected(true);
            } else if (entry.getKey() == 7) {
                weekSunday.setSelected(true);
            }
        }
    }

    private void initView() {
        weekMonday.setOnClickListener(this);
        weekTuesday.setOnClickListener(this);
        weekWednesday.setOnClickListener(this);
        weekThursday.setOnClickListener(this);
        weekFriday.setOnClickListener(this);
        weekSaturday.setOnClickListener(this);
        weekSunday.setOnClickListener(this);

        saveLL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        v.setSelected(!v.isSelected());
        boolean selected = v.isSelected();
        switch (v.getId()) {
            case R.id.wiv_monday:
                if (selected) {
                    tempWeekCheckBeanMap.put(1, true);
                } else {
                    tempWeekCheckBeanMap.remove(1);
                }
                break;
            case R.id.wiv_tuesday:
                if (selected) {
                    tempWeekCheckBeanMap.put(2, true);
                } else {
                    tempWeekCheckBeanMap.remove(2);
                }
                break;
            case R.id.wiv_wednesday:
                if (selected) {
                    tempWeekCheckBeanMap.put(3, true);
                } else {
                    tempWeekCheckBeanMap.remove(3);
                }
                break;
            case R.id.wiv_thursday:
                if (selected) {
                    tempWeekCheckBeanMap.put(4, true);
                } else {
                    tempWeekCheckBeanMap.remove(4);
                }
                break;
            case R.id.wiv_friday:
                if (selected) {
                    tempWeekCheckBeanMap.put(5, true);
                } else {
                    tempWeekCheckBeanMap.remove(5);
                }
                break;
            case R.id.wiv_saturday:
                if (selected) {
                    tempWeekCheckBeanMap.put(6, true);
                } else {
                    tempWeekCheckBeanMap.remove(6);
                }
                break;
            case R.id.wiv_sunday:
                if (selected) {
                    tempWeekCheckBeanMap.put(7, true);
                } else {
                    tempWeekCheckBeanMap.remove(7);
                }
                break;
            case R.id.ll_save:
                save();
                break;
        }
    }

    private void save() {
        weekCheckBeanMap = tempWeekCheckBeanMap;
        Intent intent = getIntent();
        intent.putExtra(EXTRA_KEY, weekCheckBeanMap);
        setResult(RESULT_CODE, intent);
        finish();
    }

}
