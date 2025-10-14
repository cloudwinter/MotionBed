package com.sn.blackdianqi.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.activity.TimeSettingActivity;
import com.sn.blackdianqi.adapter.TempAdapter;
import com.sn.blackdianqi.bean.DateBean;
import com.sn.blackdianqi.bean.LengNuanTimeBean;
import com.sn.blackdianqi.bean.TempModel;
import com.sn.blackdianqi.dialog.DoubleConfirmDialog;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.MotionBedUtil;
import com.sn.blackdianqi.util.Prefer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 冷暖
 */
public class LengnuanFragment extends BaseMcuFragment {

    @BindView(R.id.tvShuiWei)
    TextView tvShuiWei;
    @BindView(R.id.ivSlider)
    ImageView ivSlider;
    @BindView(R.id.rvList)
    RecyclerView rvList;
    @BindView(R.id.cbTimer)
    CheckBox cbTimer;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.llTime)
    LinearLayout llTime;
    @BindView(R.id.tvTemp)
    TextView tvTemp;


    private List<TempModel> temps = new ArrayList<>();
    private List<Float> tempGears = new ArrayList<>();
    private TempAdapter tempAdapter;
    private float tempLength;
    private float xStart;
    private float yStart;

    Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (getUserVisibleHint()) {
                String cmd = "FF FF FF FF FE 10 00 01 00 00 00 00 00 AA";
                sendAskBlueCRCCmd(cmd);
            }
            mHandler.postDelayed(runnable, 5000);
        }
    };

    @Override
    void handleReceiveData(String cmd) {
        if (cmd.contains("FF FF FF FF FE 14 00 00 01")) {//冷暖状态查询回复
            cmd = cmd.toUpperCase().replaceAll(" ", "");
            if (cmd.length() < 40) {
                return;
            }
            String timeLower = cmd.substring(18, 19);//工作状态
            String stateHigh = cmd.substring(19, 20);//定时状态

            //工作状态
            int workState = BlueUtils.covert16TO10(stateHigh);//工作状态  0：空闲 1：加热 2：制冷 3：水位低 4：故障
            String gear = cmd.substring(20, 22);//工作档位
            Log.e("工作档位:", gear);
            int selectScaleIndex = 1;
            if (workState == 0) {//空闲
                selectScaleIndex = 5;
            } else if (workState == 1) {//加热
                selectScaleIndex = 5 + Integer.parseInt(gear);
            } else if (workState == 2) {//制冷
                selectScaleIndex = 5 - Integer.parseInt(gear);
            } else if (workState == 3) {//水位低
                selectScaleIndex = 5;
            } else if (workState == 4) {//故障
                selectScaleIndex = 5;
            }
            tempAdapter.setSelectIndex(selectScaleIndex - 1);
            ivSlider.animate()
                    .x(xStart + MotionBedUtil.dpToPx((Context) getActivity(), (float) ((5f * (selectScaleIndex) - 2.5) * 7 - 10.5f)))
                    .y(yStart)
                    .setDuration(0)
                    .start();

            //定时状态
            int timeState = BlueUtils.covert16TO10(timeLower);//定时开启关闭：  0 定时关闭，1 定时开启
            var isTimer = false;
            if (timeState == 0) {
                isTimer = false;
            } else if (timeState == 1) {
                isTimer = true;
            }
            cbTimer.setChecked(isTimer);

            //定时时间
            String timeStr = "";
            if (isTimer) {
                String timerHour = cmd.substring(22, 24);//若已开启定时，则表示设定的小时位，0~23
                String timerMin = cmd.substring(24, 26);//若已开启定时，则表示设定的分钟位，0~59
                String workMode = cmd.substring(26, 28);//设置的定时工作模式00：无01：加热02：制冷
                String workGear = cmd.substring(28, 30);//设置的定时工作挡位0~4：挡位

                LengNuanTimeBean lengNuanTimeBean = new LengNuanTimeBean();
                lengNuanTimeBean.setHour(timerHour);
                lengNuanTimeBean.setMins(timerMin);
                lengNuanTimeBean.setMode(workMode);
                lengNuanTimeBean.setGear(workGear);

                Prefer.getInstance().setLengNuanTime(Prefer.getInstance().getLatelyConnectedDevice(), lengNuanTimeBean);
                timeStr = timerHour + ":" + timerMin;
            }
            tvTime.setText(timeStr);

            if (isNumeric(cmd.substring(30, 32)) && isNumeric(cmd.substring(32, 34))) {//拿到温度是正常温度，才解析显示
                //温度
                int temp1 = Integer.parseInt(cmd.substring(30, 32));//整数
                int temp2 = Integer.parseInt(cmd.substring(32, 34));//小数
                String temp = temp1 + "." + temp2;
                tvTemp.setText(temp + "°c");
            }

            //水位
            String waterLevelStatus = cmd.substring(34, 36);//整数
            int waterLevel = Integer.parseInt(waterLevelStatus);
            tvShuiWei.setText(String.valueOf(waterLevel));

            if (waterLevel < 20) {
                DoubleConfirmDialog.builder(getActivity())
                        .setContent(getResources().getString(R.string.shuiwei_hint))
                        .setLeftButVisibility(View.GONE)
                        .setLineVisibility(View.GONE)
                        .setListener(new DoubleConfirmDialog.OnPermissionsDialogListener() {
                            @Override
                            public void cancleOnClick(DoubleConfirmDialog dialog) {
                                dialog.dismiss();
                            }

                            @Override
                            public void determineOnClick(DoubleConfirmDialog dialog, String content) {
                                dialog.dismiss();
                            }
                        }).show();
            }

        } else if (cmd.contains("FF FF FF FF FE 14 00 07 01")) {//实时时间回码
            Log.e("=====实时时间", cmd);
            cmd = cmd.toUpperCase().replaceAll(" ", "");
        } else if (cmd.contains("FF FF FF FF FE 14 00 01 01")) {//定时查询温度、档位、状态
            cmd = cmd.toUpperCase().replaceAll(" ", "");
            Log.e("获取温度：", cmd);
            //温度
            String t1 = cmd.substring(30, 32);
            String t2 = cmd.substring(32, 34);
            if (isNumeric(t1) && isNumeric(t2)) {//拿到温度是正常温度，才解析显示
                int temp1 = Integer.parseInt(cmd.substring(30, 32));//整数
                int temp2 = Integer.parseInt(cmd.substring(32, 34));//小数
                String temp = temp1 + "." + temp2;
                Log.e("获取温度：", temp);
                tvTemp.setText(temp + "°c");
            } else {
                tvTemp.setText("");
            }

            String stateHigh = cmd.substring(19, 20);//定时状态

            //工作状态
            int workState = BlueUtils.covert16TO10(stateHigh);//工作状态  0：空闲 1：加热 2：制冷 3：水位低 4：故障
            String gear = cmd.substring(20, 22);//工作档位
            Log.e("工作状态:", workState + "");
            Log.e("工作档位:", gear);
            int selectScaleIndex = 1;
            if (workState == 0) {//空闲
                selectScaleIndex = 5;
            } else if (workState == 1) {//加热
                selectScaleIndex = 5 + Integer.parseInt(gear);
            } else if (workState == 2) {//制冷
                selectScaleIndex = 5 - Integer.parseInt(gear);
            } else if (workState == 3) {//水位低
                selectScaleIndex = 5;
            } else if (workState == 4) {//故障
                selectScaleIndex = 5;
            }

            Log.e("读取档位:" + selectScaleIndex, ",当前档位:" + tempAdapter.getSelectIndex());
            if (selectScaleIndex - 1 != tempAdapter.getSelectIndex()) {
                tempAdapter.setSelectIndex(selectScaleIndex - 1);
                ivSlider.animate()
                        .x(xStart + MotionBedUtil.dpToPx((Context) getActivity(), (float) ((5f * (selectScaleIndex) - 2.5) * 7 - 10.5f)))
                        .y(yStart)
                        .setDuration(0)
                        .start();
            }
        }
    }

    //判断字符串是不是纯数字
    public boolean isNumeric(String str) {
        return str.matches("[0-9]+");
    }

    @Override
    void askStatus() {
        try {
            //冷暖合并询问码
            Thread.sleep(200L);
            sendAskBlueCRCCmd("FF FF FF FF FE 10 00 00 00 00 00 00 00 AA");
            Thread.sleep(200L);
            // 发送实时时间指令
            sendTimeInitCmd();
            Log.e("==LengnuanFragment==", "冷暖初始化指令发送结束");
        } catch (Exception e) {
            LogUtils.e(TAG, "askStatus 异常" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendTimeInitCmd() {
        StringBuilder cmdSB = new StringBuilder();
        cmdSB.append("FFFFFFFFFE1400070000");
        DateBean dateBean = new DateBean(new Date());
        cmdSB.append(dateBean.getEndYear());
        cmdSB.append(dateBean.getMonth());
        cmdSB.append(dateBean.getDay());
        cmdSB.append(dateBean.getHour());
        cmdSB.append(dateBean.getMinute());
        cmdSB.append(dateBean.getSecond());
        cmdSB.append(dateBean.getWeek());
        cmdSB.append("00");
        LogUtils.i(TAG, "发送实时时间指令：" + cmdSB.toString());
        sendAskBlueCRCCmd(cmdSB.toString());
    }

    @Override
    public void onTongbukzEvent(boolean show, boolean open) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            askStatus();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lengnuan, container, false);
        ButterKnife.bind(this, view);
        initData();
        initView();
        String cachePath = getActivity().getExternalCacheDir().getAbsolutePath();
        Log.e("========cachePath", cachePath);
        mHandler.postDelayed(runnable, 2000);//监听时间时钟
        return view;
    }

    private void initData() {
        temps.clear();
        temps.add(new TempModel("5", "FFFFFFFFFE1000050000040000AA", "#17C9D5"));
        temps.add(new TempModel("10", "FFFFFFFFFE1000050000030000AA", "#2EBFE4"));
        temps.add(new TempModel("15", "FFFFFFFFFE1000050000020000AA", "#47B4F4"));
        temps.add(new TempModel("20", "FFFFFFFFFE1000050000010000AA", "#1A89FE"));
        temps.add(new TempModel(getResources().getString(R.string.close), "FFFFFFFFFE1000060000000000AA", "#41B67D"));
        temps.add(new TempModel("30", "FFFFFFFFFE1000040000010000AA", "#FF9704"));
        temps.add(new TempModel("35", "FFFFFFFFFE1000040000020000AA", "#FF6D04"));
        temps.add(new TempModel("40", "FFFFFFFFFE1000040000030000AA", "#EA3F03"));
        temps.add(new TempModel("45", "FFFFFFFFFE1000040000040000AA", "#FF1204"));

        tempGears.add(MotionBedUtil.dpToPx(getActivity(), 2.5f * 7));
        tempGears.add(MotionBedUtil.dpToPx(getActivity(), 7.5f * 7));
        tempGears.add(MotionBedUtil.dpToPx(getActivity(), 12.5f * 7));
        tempGears.add(MotionBedUtil.dpToPx(getActivity(), 17.5f * 7));
        tempGears.add(MotionBedUtil.dpToPx(getActivity(), 27.5f * 7));
        tempGears.add(MotionBedUtil.dpToPx(getActivity(), 32.5f * 7));
        tempGears.add(MotionBedUtil.dpToPx(getActivity(), 37.5f * 7));
        tempGears.add(MotionBedUtil.dpToPx(getActivity(), 42.5f * 7));
        tempGears.add(MotionBedUtil.dpToPx(getActivity(), 45f * 7));
    }

    private float xDelta, yDelta;
    private float xLast, yLast;

    private void initView() {
        tempLength = MotionBedUtil.dpToPx(getContext(), 315);//温度坐标滑动的长度范围
        rvList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        tempAdapter = new TempAdapter(getContext(), temps);
        rvList.setAdapter(tempAdapter);

        xStart = ivSlider.getX();
        yStart = ivSlider.getY();

        ivSlider.animate()
                .x(xStart + MotionBedUtil.dpToPx(getActivity(), 22.5f * 7 - 10.5f))
                .y(yStart)
                .setDuration(0)
                .start();

        ivSlider.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        xDelta = X - v.getLeft();
                        yDelta = Y - v.getTop();
                        xLast = v.getX();
                        yLast = v.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
//                                         .y(yLast + Y - yDelta) Y轴移动的位置
                        if (xStart < (xLast + X - xDelta) && (xLast + X - xDelta) < (xStart + tempLength - 31.5)) {
                            v.animate()
                                    .x(xLast + X - xDelta)
                                    .y(yStart)
                                    .setDuration(0)
                                    .start();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (xStart < (xLast + X - xDelta) && (xLast + X - xDelta) < (xStart + tempLength - 31.5)) {
                            v.animate()
                                    .x(xLast + X - xDelta)
                                    .y(yStart)
                                    .setDuration(0)
                                    .start();
                        }
                        float xLastValue = ivSlider.getX() - xStart + MotionBedUtil.dpToPx(getActivity(), 10.5f);
                        //根据当前的位置获取所在的档位
                        int index = getGearByAction(xLastValue);
                        Log.e("坐标:", xLastValue + "," + index);
                        tempAdapter.setSelectIndex(index);
                        String tempCmd = temps.get(index).getTempCmd();
                        sendAskBlueCRCCmd(tempCmd);
                        break;
                }
                return true; // 如果需要消费事件，返回true，否则返回false。
            }
        });

        cbTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!compoundButton.isPressed()) {
                    return;
                }
                if (b) {
                    sendAskBlueCRCCmd("FF FF FF FF FE 10 00 03 00 00 01 00 00 AA");
                } else {
                    sendAskBlueCRCCmd("FF FF FF FF FE 10 00 03 00 00 00 00 00 AA");
                }
            }
        });

        llTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TimeSettingActivity.class);
                startActivity(intent);
            }
        });
    }


    //根据移动的距离判断当前所在的档位
    public int getGearByAction(float x) {
        int gear = 0;
        for (int i = 0; i < tempGears.size(); i++) {
            if (x < tempGears.get(i)) {
                gear = i;
                break;
            }
        }
        return gear;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 销毁 Handler
        mHandler.removeCallbacksAndMessages(null);
    }

}