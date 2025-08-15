package com.sn.blackdianqi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.activity.AlarmActivity;
import com.sn.blackdianqi.activity.DianDongSetActivity;
import com.sn.blackdianqi.activity.WebCommonActivity;
import com.sn.blackdianqi.activity.XinLvDaiActivity;
import com.sn.blackdianqi.bean.AlarmBean;
import com.sn.blackdianqi.bean.AudioEvent;
import com.sn.blackdianqi.bean.DateBean;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.bean.MessageEvent;
import com.sn.blackdianqi.dialog.DoubleConfirmDialog;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.AnjianTextView;
import com.sn.blackdianqi.view.AnjianWeitiaoVerticalView;
import com.sn.blackdianqi.view.ChildTouchListener;
import com.sn.blackdianqi.view.JiyiSmall2View;
import com.sn.blackdianqi.view.JiyiSmallView;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 电动床
 */
public class DiandongFragment extends BaseMcuFragment implements View.OnTouchListener {

    @BindView(R.id.view_beibutiaozheng)
    AnjianWeitiaoVerticalView beibutiaozhengView;
    @BindView(R.id.view_tuibutiaozheng)
    AnjianWeitiaoVerticalView tuibutiaozhengView;

    @BindView(R.id.view_kandianshi)
    JiyiSmall2View kandianshiView;
    @BindView(R.id.view_lingyali)
    JiyiSmall2View lingyaliView;
    @BindView(R.id.view_zhihan)
    JiyiSmall2View zhihanView;

    @BindView(R.id.view_jiyi1)
    JiyiSmall2View jiyi1View;
    @BindView(R.id.view_jiyi2)
    JiyiSmall2View jiyi2View;

    @BindView(R.id.view_fuyuan)
    JiyiSmall2View fuyuanView;
    @BindView(R.id.view_yaolan)
    JiyiSmall2View yaolanView;

    @BindView(R.id.ll_dengguang)
    LinearLayout llDengguang;
    @BindView(R.id.ll_anmo)
    LinearLayout llAnmo;
    @BindView(R.id.ll_dingshi)
    LinearLayout llDingshi;
    @BindView(R.id.ll_ddset)
    LinearLayout llDdset;
    @BindView(R.id.ll_zhinengjiance)
    LinearLayout llZhinengjiance;

    @BindView(R.id.cb_dengguang)
    CheckBox cbDengguang;
    @BindView(R.id.cb_anmo)
    CheckBox cbAnmo;
    @BindView(R.id.cb_dingshi)
    CheckBox cbDingshi;

    private long eventDownTime = 0L;
    private String blueName;
    private String deviceAddress;

    private boolean isFirstAlarm;//是否首次设置闹钟


    @Override
    void handleReceiveData(String cmd) {
        if (cmd.contains("FF FF FF FF 01 00 2A 14")) {//电动床状态查询回复
            cmd = cmd.toUpperCase().replaceAll(" ", "");
            String status = cmd.substring(16, 18);//快捷状态
            String result = BlueUtils.hexString16To2hexString(status);
            result = new StringBuilder(result).reverse().toString();//反转从低到高排列
            String kandianshi = result.substring(0, 1);
            String lingyali = result.substring(1, 2);
            String jiyi1 = result.substring(2, 3);
            String jiyi2 = result.substring(3, 4);
            String zhihan = result.substring(4, 5);
            if (TextUtils.equals(kandianshi, "1")) {
                kandianshiView.setSelected(true);
            } else {
                kandianshiView.setSelected(false);
            }
            if (TextUtils.equals(lingyali, "1")) {
                lingyaliView.setSelected(true);
            } else {
                lingyaliView.setSelected(false);
            }
            if (TextUtils.equals(jiyi1, "1")) {
                jiyi1View.setSelected(true);
            } else {
                jiyi1View.setSelected(false);
            }
            if (TextUtils.equals(jiyi2, "1")) {
                jiyi2View.setSelected(true);
            } else {
                jiyi2View.setSelected(false);
            }
            if (TextUtils.equals(zhihan, "1")) {
                zhihanView.setSelected(true);
            } else {
                zhihanView.setSelected(false);
            }

            String xinlvdai = cmd.substring(18, 20);//心率带状态
            if (TextUtils.equals(xinlvdai, "01")) {
                //获取心率带的MAC
                try {
                    Thread.sleep(200L);
                    sendBlueCmd("FF FF FF FF 01 00 0C 0B 0F");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                llZhinengjiance.setVisibility(View.GONE);
                Prefer.getInstance().setXinlvdai(Prefer.getInstance().getLatelyConnectedDevice(), false);
            }

            String anMostatus = cmd.substring(20, 22);//按摩状态
            if (TextUtils.equals(anMostatus, "01")) {
                llAnmo.setVisibility(View.VISIBLE);
            } else {
                llAnmo.setVisibility(View.GONE);
            }

            String dengGuangCheckstatus = cmd.substring(22, 24);//灯光开关状态
            if (TextUtils.equals(dengGuangCheckstatus, "01")) {
                cbDengguang.setChecked(true);
            } else {
                cbDengguang.setChecked(false);
            }

            String anMoCheckstatus = cmd.substring(24, 26);//按摩开关状态
            if (TextUtils.equals(anMoCheckstatus, "01")) {
                cbAnmo.setChecked(true);
            } else {
                cbAnmo.setChecked(false);
            }

        } else if (cmd.contains("FF FF FF FF 01 00 03 0B 00")) {//时间校验回码 ==>有闹钟、未设置
            isFirstAlarm = true;
            cmd = cmd.toUpperCase().replaceAll(" ", "");
            String isAudio = cmd.substring(16, 18);//是否有音响
            if (TextUtils.equals(isAudio, "00")) {
                Prefer.getInstance().setIsAudio(deviceAddress, false);
            } else {
                Prefer.getInstance().setIsAudio(deviceAddress, true);
            }
            LogUtils.i(TAG, "收到无闹钟未设置指令：" + cmd);
            // 有闹钟,未设置
            AlarmBean alarmBean = new AlarmBean();
            alarmBean.setAlarmSwitch(false);
            Prefer.getInstance().setAlarm(deviceAddress, alarmBean);
            EventBus.getDefault().post(new AudioEvent(true));
        } else if (cmd.contains("FF FF FF FF 01 00 04 13")) {//时间校验回码 ==>有闹钟   0F 或 1F：且闹钟已开启状态  ， AF 或 A1：且闹钟已关闭状态
            cmd = cmd.toUpperCase().replaceAll(" ", "");
            String isAudio = cmd.substring(16, 18);//是否有音响
            if (TextUtils.equals(isAudio, "0F") || TextUtils.equals(isAudio, "AF")) {
                Prefer.getInstance().setIsAudio(deviceAddress, false);
            } else {
                Prefer.getInstance().setIsAudio(deviceAddress, true);
            }
            LogUtils.i(TAG, "收到有闹钟已设置指令：" + cmd);
            setHasAlarm(cmd);
        } else if (cmd.contains("FF FF FF FF 01 00 0C 11")) {//查询心率带MAC
            cmd = cmd.toUpperCase().replaceAll(" ", "");
            String state = cmd.substring(16, 18);//心率带标志位
            if (TextUtils.equals(state, "03")) {//显示心率带入口
                llZhinengjiance.setVisibility(View.VISIBLE);
                String mac = cmd.substring(18, 30);//心率带mac
                Prefer.getInstance().setXinlvdai(Prefer.getInstance().getLatelyConnectedDevice(), true);
                Prefer.getInstance().setXinlvdaiMac(Prefer.getInstance().getLatelyConnectedDevice(), mac);
            } else {
                Prefer.getInstance().setXinlvdai(Prefer.getInstance().getLatelyConnectedDevice(), false);
            }
        }

        // 记忆1 按键回码
        if (cmd.contains("FF FF FF FF 05 00 00 A0 0A 2F 07")) {
            jiyi1View.setSelected(true);
        }
        if (cmd.contains("FF FF FF FF 05 00 00 AF 0A 2A F7")) {
            jiyi1View.setSelected(false);
        }

        // 记忆2 按键回码
        if (cmd.contains("FF FF FF FF 05 00 00 B0 0B E3 07")) {
            jiyi2View.setSelected(true);
        }
        if (cmd.contains("FF FF FF FF 05 00 00 BF 0B E6 F7")) {
            jiyi2View.setSelected(false);
        }

        // 看电视 按键回码
        if (cmd.contains("FF FF FF FF 05 00 00 50 05 2B 03")) {
            kandianshiView.setSelected(true);
        }
        if (cmd.contains("FF FF FF FF 05 00 00 5F 05 2E F3")) {
            kandianshiView.setSelected(false);
        }

        // 零压力 按键回码
        if (cmd.contains("FF FF FF FF 05 00 00 90 09 7B 06")) {
            lingyaliView.setSelected(true);
        }
        if (cmd.contains("FF FF FF FF 05 00 00 9F 09 7E F6")) {
            lingyaliView.setSelected(false);
        }

        // 止鼾 按键回码
        if (cmd.contains("FF FF FF FF 05 00 00 F0 0F D3 04")) {
            zhihanView.setSelected(true);
        }
        if (cmd.contains("FF FF FF FF 05 00 00 FF 0F D6 F4")) {
            zhihanView.setSelected(false);
        }
    }

    private void setHasAlarm(String cmd) {
        if (cmd.length() < 38) {
            return;
        }
        // 有闹钟，已设置
        AlarmBean alarmBean = new AlarmBean();
        String cmdStatus = cmd.substring(16, 18);
        // 开关
        if (cmdStatus.equals("0F") || cmdStatus.equals("1F")) {
            alarmBean.setAlarmSwitch(true);
            cbDingshi.setChecked(true);
        } else {
            alarmBean.setAlarmSwitch(false);
            cbDingshi.setChecked(false);
        }

        // 时间
        String timeHour = cmd.substring(18, 20);
        alarmBean.setHourStr(timeHour);
        String timeMin = cmd.substring(20, 22);
        alarmBean.setMinuteStr(timeMin);

        // 星期
        String cmdWeek = BlueUtils.hexString16To2hexString(cmd.substring(24, 26));
        for (int i = 0; i < 7; i++) {
            char charAt = cmdWeek.charAt(i);
            if (charAt == '1') {
                alarmBean.getWeekCheckBeanMap().put(7 - i, true);
            }
        }

        // 模式
        String cmdMode = cmd.substring(28, 30);
        alarmBean.setModeCode(cmdMode);

        // 按摩
        String cmdAnmo = cmd.substring(30, 32);
        if (cmdAnmo.equals("01")) {
            alarmBean.setAnmo(true);
        } else {
            alarmBean.setAnmo(false);
        }

        // 响铃
        String cmdRing = cmd.substring(32, 34);
        if (cmdRing.equals("01")) {
            alarmBean.setXiangling(true);
        } else {
            alarmBean.setXiangling(false);
        }
        alarmBean.setMusicVal(cmdRing);
        Prefer.getInstance().setAlarm(deviceAddress, alarmBean);

        EventBus.getDefault().post(new AudioEvent(true));
    }

    /**
     * 发送蓝牙命令
     */
    private void checkAndSend() {
        AlarmBean alarmBean = Prefer.getInstance().getAlarm(deviceAddress);

        if (alarmBean == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("FFFFFFFF01000213");
        // 状态
        alarmBean.setAlarmSwitch(cbDingshi.isChecked());
        if (cbDingshi.isChecked()) {
            sb.append("01");
        } else {
            sb.append("A1");
        }

        // 时间
        sb.append(defaultIfEmpty(alarmBean.getHourStr(), "00"));
        sb.append(defaultIfEmpty(alarmBean.getMinuteStr(), "00"));
        sb.append("00");


        // 星期
        HashMap<Integer, Boolean> weekCheckBeanMap = alarmBean.getWeekCheckBeanMap();
        StringBuilder weekStr2 = new StringBuilder();
        for (int i = 7; i >= 1; i--) {
            if (weekCheckBeanMap.containsKey(i) && weekCheckBeanMap.get(i)) {
                weekStr2.append("1");
            } else {
                weekStr2.append("0");
            }
        }
        weekStr2.append("0");
        sb.append(BlueUtils.str2To16(weekStr2.toString()));

        // 重复
        if (weekCheckBeanMap.size() == 0) {
            sb.append("00");
        } else {
            sb.append("01");
        }

        // 模式
        sb.append(alarmBean.getModeCode());

        // 按摩
        sb.append(alarmBean.isAnmo() ? "01" : "00");

        // 响铃
        boolean isAudio = Prefer.getInstance().getIsAudio(deviceAddress);
        if (isAudio) {
            // 响铃音乐
            sb.append(alarmBean.getMusicVal());//设置响铃音乐
        } else {
            //是否响铃
            sb.append(alarmBean.isXiangling() ? "01" : "00");
        }
        // 发送蓝牙命令
        sendBlueCmd(sb.toString());
        Prefer.getInstance().setAlarm(Prefer.getInstance().getLatelyConnectedDevice(), alarmBean);
    }


    @Override
    public void onResume() {
        super.onResume();
        boolean visible = isVisible();
        boolean userVisibleHint = getUserVisibleHint();
        Log.e("visible", visible + "  " + userVisibleHint + "");
        if (getUserVisibleHint()) {
            askStatus();
        }
    }

    @Override
    void askStatus() {
        try {
            Thread.sleep(200L);
            // 电动床合并询问码
            sendAskBlueCmd("FF FF FF FF 01 00 2A 14 00 00 00 00 00 00 00 00 00 00");
            Thread.sleep(200L);
            // 发送闹钟指令
            sendAlarmInitCmd();
        } catch (Exception e) {
            LogUtils.e(TAG, "askStatus 异常" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onTongbukzEvent(boolean show, boolean open) {
    }

    /**
     * 发送闹钟初始化命令
     */
    private void sendAlarmInitCmd() {
        StringBuilder cmdSB = new StringBuilder();
        cmdSB.append("FFFFFFFF01000111");
        DateBean dateBean = new DateBean(new Date());
        cmdSB.append(dateBean.getHour());
        cmdSB.append(dateBean.getMinute());
        cmdSB.append(dateBean.getSecond());
        cmdSB.append(dateBean.getWeek());
        cmdSB.append(dateBean.getEndYear());
        cmdSB.append(dateBean.getMonth());
        cmdSB.append(dateBean.getDay());
        LogUtils.i(TAG, "发送闹钟指令：" + cmdSB.toString());
        sendBlueCmd(cmdSB.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diandong, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 按键以外发送停止码
                sendBlueFullCmd("FF FF FF FF 05 00 00 00 00 D7 00");
            }
        });
        ButterKnife.bind(this, view);
        DeviceBean deviceBean = Prefer.getInstance().getConnectedDevice();
        if (deviceBean != null) {
            blueName = deviceBean.getTitle();
            deviceAddress = deviceBean.getAddress();
        }
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
        yaolanView.setOnTouchListener(this);

        beibutiaozhengView.setChildTouchListener(new ChildTouchListener() {
            @Override
            public void onTopTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 03 97 01");
                } else if (isUPorCancel(event.getAction())) {
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                }
            }

            @Override
            public void onBottomTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 04 D6 C3");
                } else if (isUPorCancel(event.getAction())) {
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                }
            }
        });


        tuibutiaozhengView.setChildTouchListener(new ChildTouchListener() {
            @Override
            public void onTopTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 06 57 02");
                } else if (isUPorCancel(event.getAction())) {
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                }
            }

            @Override
            public void onBottomTouch(MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 07 96 C2");
                } else if (isUPorCancel(event.getAction())) {
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 00 D7 00");
                }
            }
        });

        //定时
        cbDingshi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!compoundButton.isPressed()) {
                    return;
                }
                Log.e("CheckBox", "=================");
                if (isFirstAlarm) {
                    String hint = getResources().getString(R.string.set_alarm);
                    DoubleConfirmDialog.builder(getActivity())
                            .setContent(hint)
                            .setListener(new DoubleConfirmDialog.OnPermissionsDialogListener() {
                                @Override
                                public void cancleOnClick(DoubleConfirmDialog dialog) {
                                    cbDingshi.setChecked(false);
                                    dialog.dismiss();
                                }

                                @Override
                                public void determineOnClick(DoubleConfirmDialog dialog, String content) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(getActivity(), DianDongSetActivity.class);
                                    intent.putExtra("isFirstAlarm", true);
                                    startActivity(intent);
                                }
                            }).show();
                } else {
                    checkAndSend();
                }
            }
        });

        //灯光
        cbDengguang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!compoundButton.isPressed()) {
                    return;
                }
                if (b) {//灯光开
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 4A 56 F7");
                } else {//灯光关
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 4B 97 37");
                }
            }
        });

        //按摩
        cbAnmo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!compoundButton.isPressed()) {
                    return;
                }
                if (b) {//按摩开
                    sendBlueFullCmd("FF FF FF FF 05 00 00 01 1C D7 59");
                } else {//按摩关
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 1C D6 C9");
                }
            }
        });

        //电动床设置
        llDdset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), DianDongSetActivity.class);
                startActivity(intent);
            }
        });

        //智能检测
        llZhinengjiance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), XinLvDaiActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 当前选择的按钮
     *
     * @param name
     */
    private void setSelectIndex(String name) {
        switch (name) {
            case "jiyi1":
                jiyi1View.setActivated(true);
                jiyi2View.setActivated(false);
                kandianshiView.setActivated(false);
                lingyaliView.setActivated(false);
                zhihanView.setActivated(false);
                fuyuanView.setActivated(false);
                yaolanView.setActivated(false);
                break;
            case "jiyi2":
                jiyi1View.setActivated(false);
                jiyi2View.setActivated(true);
                kandianshiView.setActivated(false);
                lingyaliView.setActivated(false);
                zhihanView.setActivated(false);
                fuyuanView.setActivated(false);
                yaolanView.setActivated(false);
                break;
            case "kandianshi":
                jiyi1View.setActivated(false);
                jiyi2View.setActivated(false);
                kandianshiView.setActivated(true);
                lingyaliView.setActivated(false);
                zhihanView.setActivated(false);
                fuyuanView.setActivated(false);
                yaolanView.setActivated(false);
                break;
            case "lingyali":
                jiyi1View.setActivated(false);
                jiyi2View.setActivated(false);
                kandianshiView.setActivated(false);
                lingyaliView.setActivated(true);
                zhihanView.setActivated(false);
                fuyuanView.setActivated(false);
                yaolanView.setActivated(false);
                break;
            case "zhihan":
                jiyi1View.setActivated(false);
                jiyi2View.setActivated(false);
                kandianshiView.setActivated(false);
                lingyaliView.setActivated(false);
                zhihanView.setActivated(true);
                fuyuanView.setActivated(false);
                yaolanView.setActivated(false);
                break;
            case "fuyuan":
                jiyi1View.setActivated(false);
                jiyi2View.setActivated(false);
                kandianshiView.setActivated(false);
                lingyaliView.setActivated(false);
                zhihanView.setActivated(false);
                fuyuanView.setActivated(true);
                yaolanView.setActivated(false);
                break;
            case "yaolan":
                jiyi1View.setActivated(false);
                jiyi2View.setActivated(false);
                kandianshiView.setActivated(false);
                lingyaliView.setActivated(false);
                zhihanView.setActivated(false);
                fuyuanView.setActivated(false);
                yaolanView.setActivated(true);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (v.getId()) {
            case R.id.view_jiyi1:
                setSelectIndex("jiyi1");
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(JIYI1_WHAT, DEFAULT_INTERVAL);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(JIYI1_WHAT);
                    if (isShortClick()) {
                        // 短按
                        if (jiyi1View.isSelected()) {
                            sendBlueFullCmd("FF FF FF FF 05 00 00 A1 0A 2E 97");
                        } else {
                            sendBlueFullCmd("FF FF FF FF 05 00 00 00 0A 57 07");
                        }
                    }
                }
                break;
            case R.id.view_jiyi2:
                setSelectIndex("jiyi2");
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(JIYI2_WHAT, DEFAULT_INTERVAL);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(JIYI2_WHAT);
                    if (isShortClick()) {
                        // 短按
                        if (jiyi2View.isSelected()) {
                            sendBlueFullCmd("FF FF FF FF 05 00 00 B1 0B E2 97");
                        } else {
                            sendBlueFullCmd("FF FF FF FF 05 00 00 00 0B 96 C7");
                        }
                    }
                }
                break;
            case R.id.view_kandianshi:
                setSelectIndex("kandianshi");
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(KANDIANSHI_WHAT, DEFAULT_INTERVAL);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(KANDIANSHI_WHAT);
                    if (isShortClick()) {
                        // 短按
                        if (kandianshiView.isSelected()) {
                            sendBlueFullCmd("FF FF FF FF 05 00 00 51 05 2A 93");
                        } else {
                            sendBlueFullCmd("FF FF FF FF 05 00 00 00 05 17 03");
                        }
                    }
                }
                break;
            case R.id.view_lingyali:
                setSelectIndex("lingyali");
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(LINGYALI_WHAT, DEFAULT_INTERVAL);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(LINGYALI_WHAT);
                    if (isShortClick()) {
                        // 短按
                        if (lingyaliView.isSelected()) {
                            sendBlueFullCmd("FF FF FF FF 05 00 00 91 09 7A 96");
                        } else {
                            sendBlueFullCmd("FF FF FF FF 05 00 00 00 09 17 06");
                        }
                    }
                }
                break;
            case R.id.view_zhihan:
                setSelectIndex("zhihan");
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(ZHIHAN_WHAT, DEFAULT_INTERVAL);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(ZHIHAN_WHAT);
                    if (isShortClick()) {
                        // 短按
                        if (zhihanView.isSelected()) {
                            sendBlueFullCmd("FF FF FF FF 05 00 00 F1 0F D2 94");
                        } else {
                            sendBlueFullCmd("FF FF FF FF 05 00 00 00 0F 97 04");
                        }
                    }
                }
                break;
            case R.id.view_fuyuan://放平
                setSelectIndex("fuyuan");
                if (MotionEvent.ACTION_DOWN == action) {
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 08 D6 C6");
                }
                break;
            case R.id.view_yaolan://摇篮
                setSelectIndex("yaolan");
                if (MotionEvent.ACTION_DOWN == action) {
                    sendBlueFullCmd("FF FF FF FF 05 00 00 00 6A 57 2F");
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
            sendBlueFullCmd("FF FF FF FF 05 00 00 9F 09 7E F6");
        } else {
            sendBlueFullCmd("FF FF FF FF 05 00 00 90 09 7B 06");
        }
    }


    private void zhihanLongClick() {
        if (zhihanView.isSelected()) {
            // 有记忆
            sendBlueFullCmd("FF FF FF FF 05 00 00 FF 0F D6 F4");
        } else {
            sendBlueFullCmd("FF FF FF FF 05 00 00 F0 0F D3 04");
        }
    }

    private void kandianshiLongClick() {
        if (kandianshiView.isSelected()) {
            // 有记忆
            sendBlueFullCmd("FF FF FF FF 05 00 00 5F 05 2E F3");
        } else {
            sendBlueFullCmd("FF FF FF FF 05 00 00 50 05 2B 03");
        }
    }

    private void jiyi2LongClick() {
        if (jiyi2View.isSelected()) {
            // 有记忆
            sendBlueFullCmd("FF FF FF FF 05 00 00 BF 0B E6 F7");
        } else {
            sendBlueFullCmd("FF FF FF FF 05 00 00 B0 0B E3 07");
        }
    }

    private void jiyi1LongClick() {
        if (jiyi1View.isSelected()) {
            // 有记忆
            sendBlueFullCmd("FF FF FF FF 05 00 00 AF 0A 2A F7");
        } else {
            sendBlueFullCmd("FF FF FF FF 05 00 00 A0 0A 2F 07");
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


    public String defaultIfEmpty(String value, String defaultStr) {
        if (TextUtils.isEmpty(value)) {
            return defaultStr;
        }
        return value;
    }
}