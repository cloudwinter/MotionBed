package com.sn.blackdianqi.fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseFragment;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.ToastUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DengguangFragment extends BaseFragment implements View.OnClickListener {
    
    public static final String TAG = "DengguangFragment";

    @BindView(R.id.img_anjian_top_icon)
    ImageView topIconImgView;
    @BindView(R.id.text_anjian_top_title)
    TextView topTitleTextView;

    @BindView(R.id.tv_10fenzhong)
    TextView tenMinsTextView;
    @BindView(R.id.tv_8xiaoshi)
    TextView eightHoursTextView;
    @BindView(R.id.tv_10xiaoshi)
    TextView tenHoursTextView;

    /**
     * 默认间隔
     */
    protected final static long DEFAULT_INTERVAL = 2000;

    // 特征值
    protected BluetoothGattCharacteristic characteristic;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_dengguang, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        tenMinsTextView.setOnClickListener(this);
        eightHoursTextView.setOnClickListener(this);
        tenHoursTextView.setOnClickListener(this);
    }




    /**
     * 发送蓝牙命令
     *
     * @param cmd
     */
    protected void sendBlueCmd(String cmd) {
        cmd = cmd.replace(" ", "");
        Log.i(TAG, "sendBlueCmd: " + cmd);
        // 判断蓝牙是否连接
        if (!BlueUtils.isConnected()) {
            ToastUtils.showToast(getContext(), getString(R.string.device_no_connected));
            LogUtils.i(TAG, "sendBlueCmd -> 蓝牙未连接");
            return;
        }
        if (characteristic == null) {
            characteristic = MyApplication.getInstance().gattCharacteristic;
        }
        if (characteristic == null) {
            LogUtils.i(TAG, "sendBlueCmd -> 特征值未获取到");
            return;
        }
        characteristic.setValue(BlueUtils.StringToBytes(cmd));
        MyApplication.getInstance().mBluetoothLeService.writeCharacteristic(characteristic);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_10fenzhong:
                sendBlueCmd("FF FF FF FF 05 00 00 00 19 16 CA");
                if (tenMinsTextView.isSelected()) {
                    tenMinsTextView.setSelected(false);
                } else {
                    tenMinsTextView.setSelected(true);
                    eightHoursTextView.setSelected(false);
                    tenHoursTextView.setSelected(false);
                }
                break;
            case R.id.tv_8xiaoshi:
                sendBlueCmd("FF FF FF FF 05 00 00 00 1A 56 CB");
                // 8小时
                if (eightHoursTextView.isSelected()) {
                    eightHoursTextView.setSelected(false);
                } else {
                    tenMinsTextView.setSelected(false);
                    eightHoursTextView.setSelected(true);
                    tenHoursTextView.setSelected(false);
                }
                break;
            case R.id.tv_10xiaoshi:
                sendBlueCmd("FF FF FF FF 05 00 00 00 1B 97 0B");
                // 10小时
                if (tenHoursTextView.isSelected()) {
                    tenHoursTextView.setSelected(false);
                } else {
                    tenMinsTextView.setSelected(false);
                    eightHoursTextView.setSelected(false);
                    tenHoursTextView.setSelected(true);
                }
                break;

        }
    }


}
