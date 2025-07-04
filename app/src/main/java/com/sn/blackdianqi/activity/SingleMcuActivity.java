package com.sn.blackdianqi.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.bean.AskStatusgeEvent;
import com.sn.blackdianqi.fragment.DiandongFragment;
import com.sn.blackdianqi.fragment.LengnuanFragment;
import com.sn.blackdianqi.fragment.QinangFragment;
import com.sn.blackdianqi.view.TranslucentActionBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleMcuActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener {
    public static final String TAG = "HomeMcuActivity";

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;
    @BindView(R.id.container)
    FrameLayout container;

    private String type;

    @Override
    public void onLeftClick() {
        Intent intent = new Intent(SingleMcuActivity.this, ConnectActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRightClick() {
        Intent intent = new Intent(SingleMcuActivity.this, Setting2Activity.class);
        intent.putExtra("type", type);
        startActivityForResult(intent, 10000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_single_mcu);
        ButterKnife.bind(this);
        actionBar.setData(null, R.mipmap.ic_back, null, R.mipmap.ic_set, getString(R.string.setting), this);
        actionBar.setStatusBarHeight(getStatusBarHeight());

        type = getIntent().getStringExtra("type");

        initView();

        Log.e("===SingleMcuActivity===", "主界面初始化指令发送结束");
        EventBus.getDefault().post(new AskStatusgeEvent(true));
    }

    private void initView() {
        if (TextUtils.equals(type, "0A")) {
            DiandongFragment diandongFragment = new DiandongFragment();
            replaceFragment(diandongFragment);
        } else if (TextUtils.equals(type, "0B")) {
            QinangFragment qinangFragment = new QinangFragment();
            replaceFragment(qinangFragment);
        } else if (TextUtils.equals(type, "0C")) {
            LengnuanFragment lengnuanFragment = new LengnuanFragment();
            replaceFragment(lengnuanFragment);
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment); // 同样的FrameLayout ID
        fragmentTransaction.commit();
    }
}