package com.sn.blackdianqi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

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

import com.sn.blackdianqi.activity.ConnectActivity;
import com.sn.blackdianqi.activity.HomeActivity;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private final static int PERMISSION_REQUEST_COARSE_LOCATION = 3;

    @BindView(R.id.text_enter)
    TextView textView;
    @BindView(R.id.img_logo)
    ImageView imageView;



    // 蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取手机本地的蓝牙适配器
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            // 未打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 10);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_enter:
                // 判断当前蓝牙是否已连接，如果已连接直接调整到HomeActivity
                if (BlueUtils.isConnected()) {
                    // 跳转到首页页面
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    // 跳转到蓝牙搜索和连接界面
                    Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                    intent.putExtra("from","main");
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
                    Prefer.getInstance().setBleStatus("未连接",null);
                }
                Prefer.getInstance().clearData();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
