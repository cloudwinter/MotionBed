package com.sn.blackdianqi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseBlueActivity;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.dialog.DataEntryInputDialog;
import com.sn.blackdianqi.dialog.FuweiDialog;
import com.sn.blackdianqi.dialog.FuweiNextDialog;
import com.sn.blackdianqi.dialog.WaitDialog;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.view.LoggerView;
import com.sn.blackdianqi.view.TranslucentActionBar;

import java.util.Random;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 睡姿录入页面
 * Created by xiayundong on 2022/1/4.
 */
public class SleepDataEntryActivity extends BaseBlueActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    private final static String TAG = "SleepDataEntryActivity";

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_AA)
    TextView tv_AA;
    @BindView(R.id.tv_KK)
    TextView tv_KK;

    @BindView(R.id.ll_param_pingtang)
    LinearLayout ll_param_pingtang;
    @BindView(R.id.tv_param_desc_pingtang)
    TextView tv_param_desc_pingtang;
    @BindView(R.id.tv_param_desc_cetang)
    TextView tv_param_desc_cetang;


    @BindView(R.id.ll_param_cetang)
    LinearLayout ll_param_cetang;
    @BindView(R.id.tv_param_pingtang)
    EditText tv_param_pingtang;
    @BindView(R.id.tv_param_cetang)
    EditText tv_param_cetang;

    @BindView(R.id.tv_btn_pingtang)
    TextView tv_btn_pingtang;
    @BindView(R.id.tv_btn_cetang)
    TextView tv_btn_cetang;

    @BindView(R.id.tv_btn_save)
    TextView tv_btn_save;
    @BindView(R.id.tv_btn_reset)
    TextView tv_btn_reset;

    WaitDialog waitDialog;
    FuweiDialog fuweiDialog;
    FuweiNextDialog fuweiNextDialog;
    DataEntryInputDialog dataEntryInputDialog;
    // 循环发码次数，最大不超过30次
    int loopSendCount = 0;
    boolean startLoopSend = false;
    private long eventDownTime = 0L;

    // 0表示平躺，1表示侧躺
    private int inputType = 0;

    @Override
    protected void onDestroy() {
        unregisterReceiver(mDataEntryReceiver);
        sendCmd("FFFFFFFF02000A0A1204");
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(mDataEntryReceiver, makeGattUpdateIntentFilter());
        setContentView(R.layout.activity_sleep_dataentry);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(null, R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }
        initView();
        initDialog();
        sendCmd("FFFFFFFF02000A0A1204");
        if (Prefer.getInstance().getStartDataEntrySwitch()) {
            startLoopSend = true;
            loopSendCmd();
        }
    }

    private void initView() {
        tv_title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (MotionEvent.ACTION_DOWN == action) {
                    timeHandler.sendEmptyMessageDelayed(TITLE_WHAT, 5000);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(TITLE_WHAT);
                }
                return true;
            }
        });

        ll_param_pingtang.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(PINGTANG_WHAT, 5000);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(PINGTANG_WHAT);
                }
                return true;
            }
        });

        ll_param_cetang.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (MotionEvent.ACTION_DOWN == action) {
                    eventDownTime = System.currentTimeMillis();
                    timeHandler.sendEmptyMessageDelayed(CETANG_WHAT, 5000);
                } else if (MotionEvent.ACTION_UP == action) {
                    timeHandler.removeMessages(CETANG_WHAT);
                }
                return true;
            }
        });
        tv_btn_pingtang.setOnClickListener(this);
        tv_btn_cetang.setOnClickListener(this);
        tv_btn_save.setOnClickListener(this);
        tv_btn_reset.setOnClickListener(this);

    }

    private void initDialog() {
        waitDialog = new WaitDialog(this, getString(R.string.sde_dialog_fuwei_loading));
        fuweiDialog = new FuweiDialog(this);
        fuweiNextDialog = new FuweiNextDialog(this);
        fuweiDialog.setOnFuweiClick(new FuweiDialog.OnFuweiClick() {
            @Override
            public void onCancel(View view) {
                // no things
            }

            @Override
            public void onFuwei(View view) {
                sendCmd("FF FF FF FF 05 00 00 02 08 D7 A6");
                waitDialog.show();
                actionBar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitDialog.dismiss();
                    }
                }, 3000);
            }
        });
        fuweiNextDialog.setOnFuweiClick(new FuweiNextDialog.OnNextClick() {
            @Override
            public void onNext(View view) {
                sendCmd("FF FF FF FF 02 00 0A 0A 12 04");
            }
        });
        //fuweiDialog.show();

        dataEntryInputDialog = new DataEntryInputDialog(this);
        dataEntryInputDialog.setOnButtonClick(new DataEntryInputDialog.OnButtonClick() {
            @Override
            public void onCancel(View view) {

            }

            @Override
            public void onSure(View view, int num) {
                if (inputType == 1) {
                    tv_param_cetang.setText(num+"");
                } else {
                    tv_param_pingtang.setText(num + "");
                }
            }
        });
    }


    @Override
    public void onLeftClick() {
        startLoopSend = false;
        finish();
    }

    @Override
    public void onRightClick() {
    }

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
            case R.id.tv_btn_reset:
                resetClick();
                break;
        }
    }


    private void pingTangClick() {
        sendCmd("FFFFFFFF0200090D0100001504");
        tv_btn_pingtang.setSelected(true);
    }

    private void ceTangClick() {
        sendCmd("FFFFFFFF0200090D0200001604");
        tv_btn_cetang.setSelected(true);
    }

    private void saveClick() {
        StringBuilder builder = new StringBuilder();
        builder.append("FFFFFFFF0200120C");
        String pingtangCmd = BlueUtils.covert10TO16(Integer.parseInt(tv_param_pingtang.getText().toString()));
        builder.append(pingtangCmd);
        String cetangCmd = BlueUtils.covert10TO16(Integer.parseInt(tv_param_cetang.getText().toString()) / 2);
        builder.append(cetangCmd);
        builder.append(BlueUtils.makeChecksum(builder.toString()));
        sendCmd(builder.toString());
        finish();
    }

    private void resetClick() {
        sendCmd("FFFFFFFF0200120C0A466C04");
        finish();
    }


    /**
     * title长按事件
     */
    private void titleLongClick() {
        if (Prefer.getInstance().getStartDataEntrySwitch()) {
            LoggerView.e(TAG, "实时数据人工停止循环");
            Prefer.getInstance().setStartDataEntrySwitch(false);
            startLoopSend = false;
            loopSendCount = 0;
            tv_AA.setText("");
            tv_KK.setText("");
        } else {
            LoggerView.e(TAG, "实时数据人工开启循环");
            Prefer.getInstance().setStartDataEntrySwitch(true);
            startLoopSend = true;
            timeHandler.sendEmptyMessage(LOOP_SEND_WHAT);
        }
    }

    /**
     * 循环发码
     */
    private void loopSendCmd() {
        if (startLoopSend && loopSendCount < 10) {
            LoggerView.e(TAG, "实时数据第" + loopSendCount + "循环");
            sendCmd("FFFFFFFF0200090F03000000001904");
//            tv_AA.setText(new Random().nextInt(100)+"");
//            tv_KK.setText(new Random().nextInt(100)+"");
            loopSendCount++;
            timeHandler.sendEmptyMessageDelayed(LOOP_SEND_WHAT,2000);
        } else {
            LoggerView.e(TAG, "实时数据超次数停止循环");
            startLoopSend = false;
            loopSendCount = 0;
        }
    }

    /**
     * 平躺长按
     */
    private void longPingtangClick() {
        //tv_param_pingtang.setEnabled(!tv_param_pingtang.isEnabled());
        inputType = 0;
        dataEntryInputDialog.show(tv_param_pingtang.getText().toString());
    }

    /**
     * 侧躺长按
     */
    private void longCetangClick() {
//        tv_param_cetang.setEnabled(!tv_param_cetang.isEnabled());
        inputType = 1;
        dataEntryInputDialog.show(tv_param_cetang.getText().toString());
    }


    private static final int TITLE_WHAT = 1;
    private static final int LOOP_SEND_WHAT = 2;
    private static final int PINGTANG_WHAT = 3;
    private static final int CETANG_WHAT = 4;
    /**
     * 长按title
     */
    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TITLE_WHAT:
                    titleLongClick();
                    break;
                case LOOP_SEND_WHAT:
                    loopSendCmd();
                    break;
                case PINGTANG_WHAT:
                    longPingtangClick();
                    break;
                case CETANG_WHAT:
                    longCetangClick();
                    break;
                default:
                    break;
            }
        }
    };


    private void handleReceiveData(String cmd) {
        if (cmd.contains("FFFFFFFF0500008208B666")) {
            // 复位返回结果
            waitDialog.dismiss();
            fuweiNextDialog.show();
            return;
        }
        if (cmd.contains("FFFFFFFF02000A14")) {
            // 复位下一步回码
            String pingtangParam = cmd.substring(20, 22);
            tv_param_pingtang.setText(BlueUtils.covert16TO10(pingtangParam) + "");
            String cetangParam = cmd.substring(22, 24);
            tv_param_cetang.setText(BlueUtils.covert16TO10(cetangParam) * 2 + "");
        }
        if (cmd.contains("FFFFFFFF0200090D01")) {
            // 按下平躺按键回码
            String ptCmd = cmd.substring(20, 22) + cmd.substring(18, 20);
            tv_param_pingtang.setText(BlueUtils.covert16TO10(ptCmd) * 2 + "");
            return;
        }
        if (cmd.contains("FFFFFFFF0200090D02")) {
            // 按下侧躺按键回码
            String ctCmd = cmd.substring(20, 22) + cmd.substring(18, 20);
            tv_param_cetang.setText(BlueUtils.covert16TO10(ctCmd) + "");
            return;
        }
        if (cmd.contains("FFFFFFFF0200090F03")) {
            String AAAA = cmd.substring(20, 22) + cmd.substring(18, 20);
            String KKKK = cmd.substring(24, 26) + cmd.substring(22, 24);
            if (startLoopSend) {
                tv_AA.setText(BlueUtils.covert16TO10(AAAA)+"");
                tv_KK.setText(BlueUtils.covert16TO10(KKKK)+"");
            }
            return;
        }

    }

    /**
     * 广播接收器，负责接收BluetoothLeService类发送的数据
     */
    private final BroadcastReceiver mDataEntryReceiver = new BroadcastReceiver() {
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
                        LogUtils.e("==睡姿特征数据录入  接收设备返回的数据==", data);
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

    public boolean isShortClick() {
        long endTime = System.currentTimeMillis();
        if (getInterval(eventDownTime, endTime) < 2000) {
            return true;
        }
        return false;
    }


    /**
     * 单位是毫秒
     * @param startTime
     * @param endTime
     * @return
     */
    private long getInterval(long startTime,long endTime) {
        long interval = endTime - startTime;
        return interval;
    }

}
