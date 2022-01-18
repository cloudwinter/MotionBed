package com.sn.blackdianqi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseBlueActivity;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.TranslucentActionBar;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 睡姿角度调整页面
 * Created by xiayundong on 2022/1/4.
 */
public class SleepAdjustActivity extends BaseBlueActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener,View.OnTouchListener {

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;
    @BindView(R.id.tv_tips1)
    TextView tv_tips1;
    @BindView(R.id.tv_tips2)
    TextView tv_tips2;

    @BindView(R.id.layout_toubu)
    LinearLayout layout_toubu;
    @BindView(R.id.layout_beibu)
    LinearLayout layout_beibu;
    @BindView(R.id.layout_yaobu)
    LinearLayout layout_yaobu;
    @BindView(R.id.layout_tuibu)
    LinearLayout layout_tuibu;


    @BindView(R.id.tv_param_AZ)
    TextView tv_param_AZ;
    @BindView(R.id.tv_param_BZ)
    TextView tv_param_BZ;
    @BindView(R.id.tv_param_CZ)
    TextView tv_param_CZ;
    @BindView(R.id.tv_param_DZ)
    TextView tv_param_DZ;

    @BindView(R.id.tv_btn_toubu_top)
    LinearLayout tv_btn_toubu_top;
    @BindView(R.id.tv_btn_beibu_top)
    LinearLayout tv_btn_beibu_top;
    @BindView(R.id.tv_btn_yaobu_top)
    LinearLayout tv_btn_yaobu_top;
    @BindView(R.id.tv_btn_tuibu_top)
    LinearLayout tv_btn_tuibu_top;

    @BindView(R.id.tv_btn_toubu_bottom)
    LinearLayout tv_btn_toubu_bottom;
    @BindView(R.id.tv_btn_beibu_bottom)
    LinearLayout tv_btn_beibu_bottom;
    @BindView(R.id.tv_btn_yaobu_bottom)
    LinearLayout tv_btn_yaobu_bottom;
    @BindView(R.id.tv_btn_tuibu_bottom)
    LinearLayout tv_btn_tuibu_bottom;

    @BindView(R.id.tv_btn_pingtang)
    TextView tv_btn_pingtang;
    @BindView(R.id.tv_btn_cetang)
    TextView tv_btn_cetang;
    @BindView(R.id.tv_btn_save)
    TextView tv_btn_save;

    String pageType = "02";

    int AX, BX, CX, DX;
    int AY, BY, CY, DY;

    boolean timerTopStart;
    boolean timerBottomStart;



    @Override
    protected void onDestroy() {
        unregisterReceiver(mAdjustReceiver);
        sendCmd("FFFFFFFF02000A0A1204");
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(mAdjustReceiver, makeGattUpdateIntentFilter());
        setContentView(R.layout.activity_sleep_adjust);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.sleep_timer_title), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
        initView();
        sendInitCmd();
    }

    private void initView() {
        tv_btn_toubu_top.setOnTouchListener(this);
        tv_btn_beibu_top.setOnTouchListener(this);
        tv_btn_yaobu_top.setOnTouchListener(this);
        tv_btn_tuibu_top.setOnTouchListener(this);

        tv_btn_toubu_bottom.setOnTouchListener(this);
        tv_btn_beibu_bottom.setOnTouchListener(this);
        tv_btn_yaobu_bottom.setOnTouchListener(this);
        tv_btn_tuibu_bottom.setOnTouchListener(this);

        tv_btn_pingtang.setOnClickListener(this);
        tv_btn_cetang.setOnClickListener(this);
        tv_btn_save.setOnClickListener(this);

        DeviceBean deviceBean = Prefer.getInstance().getConnectedDevice();
        if (deviceBean != null && !TextUtils.isEmpty(deviceBean.getTitle())) {
            String deviceName = deviceBean.getTitle();
            if (deviceName.contains("QMS-I06")
                    || deviceName.contains("QMS-I16") || deviceName.contains("QMS-I26") || deviceName.contains("QMS-I36")
                    || deviceName.contains("QMS-I46") || deviceName.contains("QMS-I56") || deviceName.contains("QMS-I66")
                    || deviceName.contains("QMS-I76") || deviceName.contains("QMS-I86") || deviceName.contains("QMS-I96")
                    || deviceName.contains("QMS-I04") || deviceName.contains("QMS-I14") || deviceName.contains("QMS-I24")
                    || deviceName.contains("QMS-I34") || deviceName.contains("QMS-I44") || deviceName.contains("QMS-I54")
                    || deviceName.contains("QMS-I64") || deviceName.contains("QMS-I74") || deviceName.contains("QMS-I84")
                    || deviceName.contains("QMS-I94")
                    || deviceName.contains("QMS4") || deviceName.contains("QMS3")) {
                pageType = "03";
            } else if (deviceName.contains("S4-N")) {
                pageType = "04";
            }
        }
        if (pageType.equals("02")) {
            tv_tips1.setText(getString(R.string.sad_top_tips_02_1));
            tv_tips2.setText(getString(R.string.sad_top_tips_02_2));
        } else if (pageType.equals("03")) {
            tv_tips1.setText(getString(R.string.sad_top_tips_03_1));
            tv_tips2.setText(getString(R.string.sad_top_tips_03_2));
            layout_yaobu.setVisibility(View.VISIBLE);
        } else if (pageType.equals("04")) {
            tv_tips1.setText(getString(R.string.sad_top_tips_04_1));
            tv_tips2.setText(getString(R.string.sad_top_tips_04_2));
            layout_toubu.setVisibility(View.VISIBLE);
            layout_yaobu.setVisibility(View.VISIBLE);
        }
    }

    private void sendInitCmd() {
        sendCmd("FFFFFFFF0200100B001904");
    }

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (v.getId()) {
            case R.id.tv_btn_toubu_top:
                if (action == MotionEvent.ACTION_DOWN) {
                    topTouchActionDown("FFFFFFFF050000020117A0");
                } else if (action == MotionEvent.ACTION_UP) {
                    topTouchActionUp();
                }
                break;
            case R.id.tv_btn_beibu_top:
                if (action == MotionEvent.ACTION_DOWN) {
                    topTouchActionDown("FFFFFFFF05000002039661");
                } else if (action == MotionEvent.ACTION_UP) {
                    topTouchActionUp();
                }
                break;
            case R.id.tv_btn_yaobu_top:
                if (action == MotionEvent.ACTION_DOWN) {
                    topTouchActionDown("FFFFFFFF050000020D17A5");
                } else if (action == MotionEvent.ACTION_UP) {
                    topTouchActionUp();
                }
                break;
            case R.id.tv_btn_tuibu_top:
                if (action == MotionEvent.ACTION_DOWN) {
                    topTouchActionDown("FFFFFFFF05000002065662");
                } else if (action == MotionEvent.ACTION_UP) {
                    topTouchActionUp();
                }
                break;

            case R.id.tv_btn_toubu_bottom:
                if (action == MotionEvent.ACTION_DOWN) {
                    bottomTouchActionDown("FFFFFFFF050000020257A1");
                } else if (action == MotionEvent.ACTION_UP) {
                    bottomTouchActionUp();
                }
                break;
            case R.id.tv_btn_beibu_bottom:
                if (action == MotionEvent.ACTION_DOWN) {
                    bottomTouchActionDown("FFFFFFFF0500000204D7A3");
                } else if (action == MotionEvent.ACTION_UP) {
                    bottomTouchActionUp();
                }
                break;
            case R.id.tv_btn_yaobu_bottom:
                if (action == MotionEvent.ACTION_DOWN) {
                    bottomTouchActionDown("FFFFFFFF050000020E57A4");
                } else if (action == MotionEvent.ACTION_UP) {
                    bottomTouchActionUp();
                }
                break;
            case R.id.tv_btn_tuibu_bottom:
                if (action == MotionEvent.ACTION_DOWN) {
                    bottomTouchActionDown("FFFFFFFF050000020797A2");
                } else if (action == MotionEvent.ACTION_UP) {
                    bottomTouchActionUp();
                }
                break;
        }
        return true;
    }


    private void topTouchActionDown(String cmd) {
        sendCmd(cmd);
        timerTopStart = true;
        timeHandler.sendEmptyMessage(MSG_TIMER_TOP_WHAT);
    }

    private void topTouchActionUp() {
        timeHandler.removeMessages(MSG_TIMER_TOP_WHAT);
        timerTopStart = false;
        sendCmd("FFFFFFFF0500000000D700");
        actionBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCmd("FFFFFFFF02000F0B001804");
            }
        },100);
    }

    private void bottomTouchActionDown(String cmd) {
        sendCmd(cmd);
        timerBottomStart = true;
        timeHandler.sendEmptyMessage(MSG_TIMER_BOTTOM_WHAT);
    }

    private void bottomTouchActionUp() {
        timeHandler.removeMessages(MSG_TIMER_BOTTOM_WHAT);
        timerBottomStart = false;
        sendCmd("FFFFFFFF0500000000D700");
        actionBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCmd("FFFFFFFF02000F0B001804");
            }
        },100);
    }




    private void timerSendTop() {
        if (timerTopStart) {
            sendCmd("FFFFFFFF02000F0B001804");
            timeHandler.sendEmptyMessageDelayed(MSG_TIMER_TOP_WHAT,500);
        }
    }

    private void timerSendBottom() {
        if (timerBottomStart) {
            sendCmd("FFFFFFFF02000F0B001804");
            timeHandler.sendEmptyMessageDelayed(MSG_TIMER_BOTTOM_WHAT,500);
        }
    }


    public static final int MSG_TIMER_TOP_WHAT = 101;
    public static final int MSG_TIMER_BOTTOM_WHAT = 102;

    /**
     * 长按title
     */
    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TIMER_TOP_WHAT:
                    timerSendTop();
                    break;
                case MSG_TIMER_BOTTOM_WHAT:
                    timerSendBottom();
                    break;
                default:
                    break;
            }
        }
    };





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_btn_pingtang:
                pingTangClick();
                break;
            case R.id.tv_btn_cetang:
                ceTangClick();
                break;
            case R.id.tv_btn_save:
                saveClick();
                break;
        }
    }

    private boolean hasWeizhiGuogao() {
        if (Integer.parseInt(tv_param_AZ.getText().toString()) > 20
                || Integer.parseInt(tv_param_BZ.getText().toString()) > 20
                || Integer.parseInt(tv_param_CZ.getText().toString()) > 20
                || Integer.parseInt(tv_param_DZ.getText().toString()) > 20) {
            return false;
        }
        return true;
    }

    private void pingTangClick() {
        if (!hasWeizhiGuogao()) {
            ToastUtils.showToast(SleepAdjustActivity.this,R.string.sad_weizhi_guogao_tips);
        } else {
            tv_btn_pingtang.setSelected(true);
        }
        AX = Integer.parseInt(tv_param_AZ.getText().toString());
        BX = Integer.parseInt(tv_param_BZ.getText().toString());
        CX = Integer.parseInt(tv_param_CZ.getText().toString());
        DX = Integer.parseInt(tv_param_DZ.getText().toString());
    }

    private void ceTangClick() {
        if (!hasWeizhiGuogao()) {
            ToastUtils.showToast(SleepAdjustActivity.this,R.string.sad_weizhi_guogao_tips);
        } else {
            tv_btn_cetang.setSelected(true);
        }
        AY = Integer.parseInt(tv_param_AZ.getText().toString());
        BY = Integer.parseInt(tv_param_BZ.getText().toString());
        CY = Integer.parseInt(tv_param_CZ.getText().toString());
        DY = Integer.parseInt(tv_param_DZ.getText().toString());
    }

    private void saveClick() {
        String AXcmd = BlueUtils.covert10TO16(AX);
        String BXcmd = BlueUtils.covert10TO16(BX);
        String CXcmd = BlueUtils.covert10TO16(CX);
        String DXcmd = BlueUtils.covert10TO16(DX);

        String AYcmd = BlueUtils.covert10TO16(AY);
        String BYcmd = BlueUtils.covert10TO16(BY);
        String CYcmd = BlueUtils.covert10TO16(CY);
        String DYcmd = BlueUtils.covert10TO16(DY);

        if (pageType.equals("02")) {
            AXcmd = "00";
            AYcmd = "00";
            DXcmd = "00";
            DYcmd = "00";
        } else if (pageType.equals("03")) {
            AXcmd = "00";
            AYcmd = "00";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("FFFFFFFF02001012");
        sb.append(AXcmd).append(BXcmd).append(CXcmd).append(DXcmd);
        sb.append(AYcmd).append(BYcmd).append(CYcmd).append(DYcmd);
        sb.append(BlueUtils.makeChecksum(sb.toString()));
        sendCmd(sb.toString());
        finish();
    }


    private void handleReceiveData(String cmd) {
        if (cmd.contains("FFFFFFFF02000F0E") && cmd.length() > 20) {
            String AZ = BlueUtils.covert16TO10(cmd.substring(16, 18)) + "";
            if (AZ.equals("175")) {
                tv_param_AZ.setText(getString(R.string.sad_weizhi_guogao));
            } else if (AZ.equals("191")) {
                tv_param_AZ.setText(getString(R.string.sad_weizhi_guodi));
            }

            String BZ = BlueUtils.covert16TO10(cmd.substring(18, 20)) + "";
            if (BZ.equals("175")) {
                tv_param_BZ.setText(getString(R.string.sad_weizhi_guogao));
            } else if (BZ.equals("191")) {
                tv_param_BZ.setText(getString(R.string.sad_weizhi_guodi));
            }

            String CZ = BlueUtils.covert16TO10(cmd.substring(20, 22)) + "";
            if (CZ.equals("175")) {
                tv_param_CZ.setText(getString(R.string.sad_weizhi_guogao));
            } else if (CZ.equals("191")) {
                tv_param_CZ.setText(getString(R.string.sad_weizhi_guodi));
            }

            String DZ = BlueUtils.covert16TO10(cmd.substring(22, 24)) + "";
            if (DZ.equals("175")) {
                tv_param_DZ.setText(getString(R.string.sad_weizhi_guogao));
            } else if (DZ.equals("191")) {
                tv_param_DZ.setText(getString(R.string.sad_weizhi_guodi));
            }

            tv_param_AZ.setText(AZ);
            tv_param_BZ.setText(BZ);
            tv_param_CZ.setText(CZ);
            tv_param_DZ.setText(DZ);
            return;
        }
        if (cmd.contains("FFFFFFFF02001012") && cmd.length() > 30) {
            String AX = BlueUtils.covert16TO10(cmd.substring(16, 18)) + "";
            String BX = BlueUtils.covert16TO10(cmd.substring(18, 20)) + "";
            String CX = BlueUtils.covert16TO10(cmd.substring(20, 22)) + "";
            String DX = BlueUtils.covert16TO10(cmd.substring(22, 24)) + "";
            String AY = BlueUtils.covert16TO10(cmd.substring(24, 26)) + "";
            String BY = BlueUtils.covert16TO10(cmd.substring(26, 28)) + "";
            String CY = BlueUtils.covert16TO10(cmd.substring(28, 30)) + "";
            String DY = BlueUtils.covert16TO10(cmd.substring(30, 32)) + "";
            actionBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendCmd("FFFFFFFF02000F0B001804");
                }
            }, 100);
        }
    }

    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mAdjustReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) { //发现GATT服务器
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //处理发送过来的数据  (//有效数据)
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String data = bundle.getString(BluetoothLeService.EXTRA_DATA);
                    if (data != null) {
                        LogUtils.e("==睡姿角度调整  接收设备返回的数据==", data);
                        data = data.replace(" ", "");
                        handleReceiveData(data);
                    }
                }
            }
        }
    };


    /* 意图过滤器 */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}
