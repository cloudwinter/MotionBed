package com.sn.blackdianqi;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sn.blackdianqi.activity.ConnectActivity;
import com.sn.blackdianqi.activity.HomeActivity;
import com.sn.blackdianqi.activity.MainMcuActivity;
import com.sn.blackdianqi.activity.SettingActivity;
import com.sn.blackdianqi.activity.SingleMcuActivity;
import com.sn.blackdianqi.activity.WebActivity;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.dialog.DoubleConfirmDialog;
import com.sn.blackdianqi.dialog.PrivacyPolicyDialog;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.PreferenceUtil;
import com.sn.blackdianqi.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final static int PERMISSION_REQUEST_COARSE_LOCATION = 3;

    @BindView(R.id.text_enter)
    TextView textView;
    @BindView(R.id.tv_privacy)
    TextView tvPrivacy;
    @BindView(R.id.img_logo)
    ImageView imageView;


    // 蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    private Boolean isAgreePrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        imageView.setImageResource(R.mipmap.app_logo_small);
        textView.setOnClickListener(this);
        tvPrivacy.setOnClickListener(this);

        isAgreePrivacy = PreferenceUtil.getBoolean("isAgreePrivacy", false);

        if (!isAgreePrivacy) {
            PrivacyPolicyDialog.builder(this)
                    .setContent()
                    .setListener(new PrivacyPolicyDialog.OnPermissionsDialogListener() {
                        @Override
                        public void cancleOnClick(PrivacyPolicyDialog dialog) {
                            dialog.dismiss();
                            System.exit(0);
                        }

                        @Override
                        public void determineOnClick(PrivacyPolicyDialog dialog) {
                            dialog.dismiss();
                            PreferenceUtil.commitBoolean("isAgreePrivacy", true);
                            isAgreePrivacy = true;
                        }
                    }).show();
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
//            }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isCheckAll = RunningContext.checkLocationPermission(this, false);
        if (!isCheckAll) {
            DoubleConfirmDialog.builder(this)
                    .setContent(getResources().getString(R.string.open_permission))
                    .setListener(new DoubleConfirmDialog.OnPermissionsDialogListener() {
                        @Override
                        public void cancleOnClick(DoubleConfirmDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void determineOnClick(DoubleConfirmDialog dialog, String content) {
                            dialog.dismiss();
                            // 获取手机本地的蓝牙适配器
                            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                            mBluetoothAdapter = bluetoothManager.getAdapter();
                            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                                // 未打开蓝牙
                                if (RunningContext.checkLocationPermission(MainActivity.this, true)) {
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, 10);
                                }
                            }
                        }
                    }).show();
        } else {
            // 获取手机本地的蓝牙适配器
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                // 未打开蓝牙
                if (RunningContext.checkLocationPermission(MainActivity.this, true)) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 10);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_privacy:
                Intent webIntent = new Intent(MainActivity.this, WebActivity.class);
                startActivity(webIntent);
                break;
            case R.id.text_enter:
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    ToastUtils.showToast(this, getResources().getString(R.string.open_bluetooth));
                    return;
                }
                // 判断当前蓝牙是否已连接，如果已连接直接调整到HomeActivity
                if (BlueUtils.isConnected()) {
                    // 跳转到首页页面
                    DeviceBean connectedDevice = Prefer.getInstance().getConnectedDevice();
                    Intent intent;
                    if (connectedDevice != null && connectedDevice.getTitle().contains("TL-Q")) {//MCU组合模式的电动床
                        intent = new Intent(MainActivity.this, MainMcuActivity.class);
                    } else if (connectedDevice != null && connectedDevice.getTitle().contains("TL-A")) {//MCU组合模式的单个气囊
                        intent = new Intent(MainActivity.this, SingleMcuActivity.class);
                        intent.putExtra("type", "0B");
                    } else if (connectedDevice != null && connectedDevice.getTitle().contains("TL-B")) {//MCU组合模式的单个电动床
                        intent = new Intent(MainActivity.this, SingleMcuActivity.class);
                        intent.putExtra("type", "0A");
                    } else if (connectedDevice != null && connectedDevice.getTitle().contains("TL-W")) {//MCU组合模式的单个冷暖
                        intent = new Intent(MainActivity.this, SingleMcuActivity.class);
                        intent.putExtra("type", "0C");
                    } else {
                        intent = new Intent(MainActivity.this, HomeActivity.class);
                    }
                    startActivity(intent);
                } else {
                    // 跳转到蓝牙搜索和连接界面
                    Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                    intent.putExtra("from", "main");
                    startActivity(intent);
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
            }
        }
        if (requestCode == 10) {
            if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
                finish();
            }
        }
    }


    private long exitTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.showToast(MainActivity.this, getString(R.string.exit));
                exitTime = System.currentTimeMillis();
            } else {
                // 退出时已连接断开连接
                if (BlueUtils.isConnected()) {
                    MyApplication.getInstance().mBluetoothLeService.disconnect();
                    Prefer.getInstance().setBleStatus("未连接", null);
                }
                Prefer.getInstance().clearData();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
