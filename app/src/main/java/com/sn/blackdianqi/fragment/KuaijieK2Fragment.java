package com.sn.blackdianqi.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.view.AnjianYuanView;
import com.sn.blackdianqi.view.JiyiView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 快捷K2
 */
public class KuaijieK2Fragment extends KuaijieBaseFragment implements View.OnTouchListener {


    @BindView(R.id.img_anjian_top_icon)
    ImageView topIconImgView;
    @BindView(R.id.text_anjian_top_title)
    TextView topTitleTextView;

    @BindView(R.id.view_jiyi1)
    JiyiView jiyi1View;

    @BindView(R.id.view_jiyi2)
    JiyiView jiyi2View;

    @BindView(R.id.view_kandianshi)
    AnjianYuanView kandianshiView;
    @BindView(R.id.view_lingyali)
    AnjianYuanView lingyaliView;
    @BindView(R.id.view_zhihan)
    AnjianYuanView zhihanView;

    @BindView(R.id.view_fuyuan)
    AnjianYuanView fuyuanView;


    private long eventDownTime = 0L;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_kuaijie_k2, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 按键以外发送停止码
                sendBlueCmd("FF FF FF FF 05 00 00 00 00 D7 00");
            }
        });
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        jiyi1View.setOnTouchListener(this);
        jiyi2View.setOnTouchListener(this);

        kandianshiView.setOnTouchListener(this);
        lingyaliView.setOnTouchListener(this);
        zhihanView.setOnTouchListener(this);
        fuyuanView.setOnTouchListener(this);

    }

    /**
     * 设置顶部icon和title
     *
     * @param iconResId
     * @param titleResId
     */
    private void setTopIconAndTitle(int iconResId, int titleResId) {
        topIconImgView.setBackground(ContextCompat.getDrawable(getContext(), iconResId));
        topTitleTextView.setText(getString(titleResId));
    }


    /**
     * 设置顶部的title
     *
     * @param titleResId
     */
    private void setTitle(int titleResId) {
        topTitleTextView.setText(getString(titleResId));
    }


    private void jiyi2LongClick() {
        if (jiyi2View.isSelected()) {
            // 有记忆
            sendBlueCmd("FF FF FF FF 05 00 00 BF 0B E6 F7");
        } else {
            sendBlueCmd("FF FF FF FF 05 00 00 B0 0B E3 07");
        }
    }

    private void jiyi1LongClick() {
        if (jiyi1View.isSelected()) {
            // 有记忆
            sendBlueCmd("FF FF FF FF 05 00 00 AF 0A 2A F7");
        } else {
            sendBlueCmd("FF FF FF FF 05 00 00 A0 0A 2F 07");
        }
    }


    @Override
    protected void handleReceiveData(String data) {
        if (isJIYIxunwenma2()) {
            if (data.contains("FF FF FF FF 03 06 00 0A")) {
                // 记忆1有记忆返回码
                jiyi1View.setSelected(true);
            }
            if (data.contains("FF FF FF FF 03 06 00 0B")) {
                // 记忆2有记忆返回码
                jiyi2View.setSelected(true);
            }
            if (data.contains("FF FF FF FF 03 06 00 05")) {
                // 看电视
                kandianshiView.setSelected(true);
            }
            if (data.contains("FF FF FF FF 03 06 00 09")) {
                // 零压力
                lingyaliView.setSelected(true);
            }
            if (data.contains("FF FF FF FF 03 06 00 0F")) {
                // 止鼾
                zhihanView.setSelected(true);
            }

        } else {
            if (data.contains("FF FF FF FF 03 12 00 AA")) {
                // 记忆1有记忆返回码
                jiyi1View.setSelected(true);
            }
            if (data.contains("FF FF FF FF 03 12 00 AB")) {
                // 记忆2有记忆返回码
                jiyi2View.setSelected(true);
            }
            if (data.contains("FF FF FF FF 03 12 00 AB")) {
                // 看电视
                kandianshiView.setSelected(true);
            }
            if (data.contains("FF FF FF FF 03 12 00 A9")) {
                // 零压力
                lingyaliView.setSelected(true);
            }
            if (data.contains("FF FF FF FF 03 12 00 AF")) {
                // 止鼾
                zhihanView.setSelected(true);
            }
        }


        // 记忆1 按键回码
        if (data.contains("FF FF FF FF 05 00 00 A0 0A 2F 07")) {
            jiyi1View.setSelected(true);
        }
        if (data.contains("FF FF FF FF 05 00 00 AF 0A 2A F7")) {
            jiyi1View.setSelected(false);
        }

        // 记忆2 按键回码
        if (data.contains("FF FF FF FF 05 00 00 B0 0B E3 07")) {
            jiyi2View.setSelected(true);
        }
        if (data.contains("FF FF FF FF 05 00 00 BF 0B E6 F7")) {
            jiyi2View.setSelected(false);
        }

        // 看电视 按键回码
        if (data.contains("FF FF FF FF 05 00 00 50 05 2B 03")) {
            kandianshiView.setSelected(true);
        }
        if (data.contains("FF FF FF FF 05 00 00 5F 05 2E F3")) {
            kandianshiView.setSelected(false);
        }

        // 零压力 按键回码
        if (data.contains("FF FF FF FF 05 00 00 90 09 7B 06")) {
            lingyaliView.setSelected(true);
        }
        if (data.contains("FF FF FF FF 05 00 00 9F 09 7E F6")) {
            lingyaliView.setSelected(false);
        }

        // 止鼾 按键回码
        if (data.contains("FF FF FF FF 05 00 00 F0 0F D3 04")) {
            zhihanView.setSelected(true);
        }
        if (data.contains("FF FF FF FF 05 00 00 FF 0F D6 F4")) {
            zhihanView.setSelected(false);
        }
    }

    @Override
    void askStatus() {
        try {
            if (isJIYIxunwenma2()) {
                // 记忆1
                sendAskBlueCmd("FF FF FF FF 03 00 28 00 03 9F 09");
                Thread.sleep(500L);
                // 记忆2
                sendAskBlueCmd("FF FF FF FF 03 00 30 00 03 1F 0E");
                Thread.sleep(500L);
                // 看电视
                sendAskBlueCmd("FF FF FF FF 03 00 18 00 03 9F 06");
                Thread.sleep(500L);
                // 零压力
                sendAskBlueCmd("FF FF FF FF 03 00 20 00 03 1E CB");
                Thread.sleep(500L);
                // 止鼾
                sendAskBlueCmd("FF FF FF FF 03 00 38 00 03 9E CC");
            } else {
                // 记忆1
                sendAskBlueCmd("FF FF FF FF 03 00 28 00 09 1F 0E");
                Thread.sleep(500L);
                // 记忆2
                sendAskBlueCmd("FF FF FF FF 03 00 31 00 09 CE C9");
                Thread.sleep(500L);
                // 看电视
                sendAskBlueCmd("FF FF FF FF 03 00 16 00 09 7E C2");
                Thread.sleep(500L);
                // 零压力
                sendAskBlueCmd("FF FF FF FF 03 00 1F 00 09 AE C0");
                Thread.sleep(500L);
                // 止鼾
                sendAskBlueCmd("FF FF FF FF 03 00 3A 00 09 BF 0B");
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "askStatus 异常" + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (v.getId()) {
            case R.id.view_jiyi1:
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(JIYI1_WHAT, DEFAULT_INTERVAL);
                    setTitle(R.string.jiyi1);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(JIYI1_WHAT);
                    if (isShortClick()) {
                        // 短按
                        if (jiyi1View.isSelected()) {
                            sendBlueCmd("FF FF FF FF 05 00 00 A1 0A 2E 97");
                        }
                    }
                }
                break;
            case R.id.view_jiyi2:
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(JIYI2_WHAT, DEFAULT_INTERVAL);
                    setTitle(R.string.jiyi2);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(JIYI2_WHAT);
                    if (isShortClick()) {
                        // 短按
                        if (jiyi2View.isSelected()) {
                            sendBlueCmd("FF FF FF FF 05 00 00 B1 0B E2 97");
                        }
                    }
                }
                break;
            case R.id.view_kandianshi:
                setTopIconAndTitle(R.mipmap.xr_kandianshi_da, R.string.kandianshi);
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(KANDIANSHI_WHAT, DEFAULT_INTERVAL);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(KANDIANSHI_WHAT);
                    if (isShortClick()) {
                        // 短按
                        if (kandianshiView.isSelected()) {
                            sendBlueCmd("FF FF FF FF 05 00 00 51 05 2A 93");
                        } else {
                            sendBlueCmd("FF FF FF FF 05 00 00 00 05 17 03");
                        }
                    }
                }
                break;
            case R.id.view_lingyali:
                setTopIconAndTitle(R.mipmap.xr_lingyali_da, R.string.lingyali);
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(LINGYALI_WHAT, DEFAULT_INTERVAL);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(LINGYALI_WHAT);
                    if (isShortClick()) {
                        // 短按
                        setTopIconAndTitle(R.mipmap.xr_lingyali_da, R.string.lingyali);
                        if (lingyaliView.isSelected()) {
                            sendBlueCmd("FF FF FF FF 05 00 00 91 09 7A 96");
                        } else {
                            sendBlueCmd("FF FF FF FF 05 00 00 00 09 17 06");
                        }
                    }
                }
                break;
            case R.id.view_zhihan:
                setTopIconAndTitle(R.mipmap.xr_zhihan_da, R.string.zhihan);
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(ZHIHAN_WHAT, DEFAULT_INTERVAL);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(ZHIHAN_WHAT);
                    if (isShortClick()) {
                        // 短按
                        setTopIconAndTitle(R.mipmap.xr_zhihan_da, R.string.zhihan);
                        if (zhihanView.isSelected()) {
                            sendBlueCmd("FF FF FF FF 05 00 00 F1 0F D2 94");
                        } else {
                            sendBlueCmd("FF FF FF FF 05 00 00 00 0F 97 04");
                        }
                    }
                }
                break;
            case R.id.view_fuyuan:
                setTopIconAndTitle(R.mipmap.xr_fuyuan_da, R.string.fuyuan);
                if (MotionEvent.ACTION_DOWN == action) {
                    sendBlueCmd("FF FF FF FF 05 00 00 00 08 D6 C6");
                }
                break;
        }
        return true;
    }


    private static final int JIYI1_WHAT = 1;
    private static final int JIYI2_WHAT = 2;
    private static final int KANDIANSHI_WHAT = 3;
    private static final int LINGYALI_WHAT = 4;
    private static final int ZHIHAN_WHAT = 5;

    /**
     * 记忆1 1
     * 记忆2  2
     * 看电视 3
     * 零压力 4
     * 止鼾 5
     */
    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JIYI1_WHAT:
                    jiyi1LongClick();
                    break;
                case JIYI2_WHAT:
                    jiyi2LongClick();
                    break;
                case KANDIANSHI_WHAT:
                    kandianshiLongClick();
                    break;
                case LINGYALI_WHAT:
                    lingyaliLongClick();
                    break;
                case ZHIHAN_WHAT:
                    zhihanLongClick();
                    break;
                default:
                    break;
            }
        }
    };


    private void lingyaliLongClick() {
        if (lingyaliView.isSelected()) {
            // 有记忆
            sendBlueCmd("FF FF FF FF 05 00 00 9F 09 7E F6");
        } else {
            sendBlueCmd("FF FF FF FF 05 00 00 90 09 7B 06");
        }
    }


    private void zhihanLongClick() {
        if (zhihanView.isSelected()) {
            // 有记忆
            sendBlueCmd("FF FF FF FF 05 00 00 FF 0F D6 F4");
        } else {
            sendBlueCmd("FF FF FF FF 05 00 00 F0 0F D3 04");
        }
    }

    private void kandianshiLongClick() {
        if (kandianshiView.isSelected()) {
            // 有记忆
            sendBlueCmd("FF FF FF FF 05 00 00 5F 05 2E F3");
        } else {
            sendBlueCmd("FF FF FF FF 05 00 00 50 05 2B 03");
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


    /**
     * 是否是记忆询问码2
     *
     * @return
     */
    private boolean isJIYIxunwenma2() {
        if (TextUtils.isEmpty(blueDeviceName)) {
            return false;
        }
        if (blueDeviceName.contains("QMS-MQ") || blueDeviceName.contains("QMS2")
                || blueDeviceName.contains("S3-2") || blueDeviceName.contains("QMS3")) {
            return true;
        }
        return false;
    }
}
