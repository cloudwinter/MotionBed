package com.sn.blackdianqi.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.activity.AnmoSetActivity;
import com.sn.blackdianqi.activity.PressSetActivity;
import com.sn.blackdianqi.base.BaseFragment;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.view.AnjianRectangleView;
import com.sn.blackdianqi.view.AnjianWeitiaoVerticalView;
import com.sn.blackdianqi.view.JiyiView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 气囊
 */
public class QinangFragment extends BaseMcuFragment implements View.OnTouchListener {

    @BindView(R.id.view_quanshen)
    AnjianRectangleView quanshenView;
    @BindView(R.id.view_beibu)
    AnjianRectangleView beibuView;
    @BindView(R.id.view_yaobu)
    AnjianRectangleView yaobuView;
    @BindView(R.id.view_jingbu)
    AnjianRectangleView jingbuView;
    @BindView(R.id.view_yujia)
    AnjianRectangleView yujiaView;

    @BindView(R.id.cb_zishiying)
    CheckBox zishiyingCb;

    @BindView(R.id.view_anmo_stop)
    JiyiView anmoStopView;
    @BindView(R.id.view_fangqi)
    JiyiView fangqiView;

    @BindView(R.id.view_anmo_set)
    LinearLayout anmoSetView;
    @BindView(R.id.view_press_set)
    LinearLayout pressSetView;

    private List<View> views = new ArrayList<>();

    boolean isFirstTime = false; // 用于标记自适应是否是第一次设置状态

    @Override
    void handleReceiveData(String cmd) {
        if (cmd.contains("FF FF FF FF FF 14 02 09 01")) {//气囊状态查询回复
            cmd = cmd.toUpperCase().replaceAll(" ", "");
            String anmoStatus = cmd.substring(18, 20);//全身按摩、背部按摩状态
            String zishiyingStatus = cmd.substring(20, 22);//自适应开关状态

            if (TextUtils.equals(anmoStatus, "01")) {
                quanshenView.setSelected(true);
            }

            if (TextUtils.equals(anmoStatus, "02")) {
                beibuView.setSelected(true);
            }

            if (TextUtils.equals(anmoStatus, "03")) {
                beibuView.setSelected(true);
                quanshenView.setSelected(true);
            }

            if (TextUtils.equals(zishiyingStatus, "01")) {
                isFirstTime = true;//首次设置状态无需执行指令
                zishiyingCb.setChecked(true);
            }
        } else if (cmd.contains("FF FF FF FF FF 0D 03 0C 01")) {//自适应开关回码
            cmd = cmd.toUpperCase().replaceAll(" ", "");
            String zishiyingStatus = cmd.substring(18, 20);//全身按摩状态
            if (TextUtils.equals(zishiyingStatus, "01")) {
                zishiyingCb.setChecked(true);
            } else {
                zishiyingCb.setChecked(false);
            }
        } else if (cmd.contains("FF FF FF FF FF 14 03 0D 01")) {//全身按摩、背部按摩设置联动(记忆)
            cmd = cmd.toUpperCase().replaceAll(" ", "");
            String status = cmd.substring(18, 20);//状态
            String type = cmd.substring(20, 22);//状态
            if (TextUtils.equals(status, "01")) {
                if (TextUtils.equals(type, "03")) {
                    quanshenView.setSelected(true);
                } else if (TextUtils.equals(type, "12")) {
                    beibuView.setSelected(true);
                }
            } else {
                if (TextUtils.equals(type, "03")) {
                    quanshenView.setSelected(false);
                } else if (TextUtils.equals(type, "12")) {
                    beibuView.setSelected(false);
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isVisible() || getUserVisibleHint()) {
            askStatus();
        }
    }

    @Override
    void askStatus() {
        try {
            Thread.sleep(200L);
            //气囊合并询问码
            sendAskBlueCRCCmd("FF FF FF FF FF 14 02 09 00 00 00 00 00 00 00 00 00 00");
        } catch (Exception e) {
            LogUtils.e(TAG, "askStatus 异常" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onTongbukzEvent(boolean show, boolean open) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qinang, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        views.add(quanshenView);
        views.add(beibuView);
        views.add(yaobuView);
        views.add(jingbuView);
        views.add(yujiaView);
        views.add(anmoSetView);
        views.add(pressSetView);

        quanshenView.setOnTouchListener(this);
        beibuView.setOnTouchListener(this);

        yaobuView.setOnTouchListener(this);
        jingbuView.setOnTouchListener(this);
        yujiaView.setOnTouchListener(this);

        anmoStopView.setOnTouchListener(this);
        fangqiView.setOnTouchListener(this);

        anmoSetView.setOnTouchListener(this);
        pressSetView.setOnTouchListener(this);

        zishiyingCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (isFirstTime) {//首次设置状态
                    isFirstTime = false; // 标记为已处理，避免重复进入此分支
                    return;
                } else {
                    if (b) {
                        sendBlueCRCCmd("FF FF FF FF FF 0D 03 0C 00 01 00");
                    } else {
                        sendBlueCRCCmd("FF FF FF FF FF 0D 03 0C 00 00 00");
                    }
                }
            }
        });
    }

    private static final int QUANSHEN_WHAT = 1;
    private static final int BEIBU_WHAT = 2;

    /**
     * 全身按摩 1
     * 背部按摩  2
     */
    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case QUANSHEN_WHAT:
                    quanshenLongClick();
                    break;
                case BEIBU_WHAT:
                    beibuLongClick();
                    break;
                default:
                    break;
            }
        }
    };

    private long eventDownTime = 0L;


    private void quanshenLongClick() {
        if (quanshenView.isSelected()) {
            // 有记忆
            sendBlueCRCCmd("FF FF FF FF FF 14 03 0D 00 00 03 00 00 00 00 00 00 00");
        } else {
            sendBlueCRCCmd("FF FF FF FF FF 14 03 0D 00 01 03 00 00 00 00 00 00 00");
        }
    }

    private void beibuLongClick() {
        if (beibuView.isSelected()) {
            // 有记忆
            sendBlueCRCCmd("FF FF FF FF FF 14 03 0D 00 00 12 00 00 00 00 00 00 00");
        } else {
            sendBlueCRCCmd("FF FF FF FF FF 14 03 0D 00 01 12 00 00 00 00 00 00 00");
        }
    }

    public boolean isShortClick() {
        long endTime = System.currentTimeMillis();
        if (getInterval(eventDownTime, endTime) < 2000) {
            return true;
        }
        return false;
    }

    /**
     * 单位是毫秒
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private long getInterval(long startTime, long endTime) {
        long interval = endTime - startTime;
        return interval;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (v.getId()) {
            case R.id.view_quanshen:
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(QUANSHEN_WHAT, DEFAULT_INTERVAL);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(QUANSHEN_WHAT);
                    if (isShortClick()) {
                        // 短按
                        sendBlueCRCCmd("FF FF FF FF FF 0B 01 03 00");
                    }
                }
                break;
            case R.id.view_beibu:
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(BEIBU_WHAT, DEFAULT_INTERVAL);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(BEIBU_WHAT);
                    if (isShortClick()) {
                        // 短按
                        sendBlueCRCCmd("FF FF FF FF FF 0B 01 12 00");
                    }
                }
                break;
            case R.id.view_yaobu:
                if (MotionEvent.ACTION_DOWN == action) {
                    sendBlueCRCCmd("FF FF FF FF FF 0B 01 05 00");
                }
                break;
            case R.id.view_jingbu:
                if (MotionEvent.ACTION_DOWN == action) {
                    sendBlueCRCCmd("FF FF FF FF FF 0B 01 04 00");
                }
                break;
            case R.id.view_yujia:
                if (MotionEvent.ACTION_DOWN == action) {
                    sendBlueCRCCmd("FF FF FF FF FF 0B 01 0C 00");
                }
                break;
            case R.id.view_anmo_stop:
                if (MotionEvent.ACTION_DOWN == action) {
                    sendBlueCRCCmd("FF FF FF FF FF 0B 01 00 00");
                }
                break;
            case R.id.view_fangqi:
                if (MotionEvent.ACTION_DOWN == action) {
                    sendBlueCRCCmd("FF FF FF FF FF 0B 01 06 00");
                }
                break;
            case R.id.view_anmo_set:
                if (MotionEvent.ACTION_DOWN == action) {
                    Intent intent = new Intent(getActivity(), AnmoSetActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.view_press_set:
                if (MotionEvent.ACTION_DOWN == action) {
                    Intent intent = new Intent(getActivity(), PressSetActivity.class);
                    startActivity(intent);
                }
                break;
        }
        return true;
    }
}