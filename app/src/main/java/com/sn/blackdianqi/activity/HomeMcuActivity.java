package com.sn.blackdianqi.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.sn.blackdianqi.MyApplication;
import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.adapter.TabPagerAdapter;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.base.BaseFragment;
import com.sn.blackdianqi.bean.AlarmBean;
import com.sn.blackdianqi.bean.AskStatusgeEvent;
import com.sn.blackdianqi.bean.AudioEvent;
import com.sn.blackdianqi.bean.DateBean;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.bean.MessageEvent;
import com.sn.blackdianqi.blue.BluetoothLeService;
import com.sn.blackdianqi.fragment.AnmoFragment;
import com.sn.blackdianqi.fragment.DengguangFragment;
import com.sn.blackdianqi.fragment.DiandongFragment;
import com.sn.blackdianqi.fragment.KuaijieK11Fragment;
import com.sn.blackdianqi.fragment.KuaijieK1Fragment;
import com.sn.blackdianqi.fragment.KuaijieK2Fragment;
import com.sn.blackdianqi.fragment.KuaijieK2MFragment;
import com.sn.blackdianqi.fragment.KuaijieK3Fragment;
import com.sn.blackdianqi.fragment.KuaijieK4Fragment;
import com.sn.blackdianqi.fragment.KuaijieK5Fragment;
import com.sn.blackdianqi.fragment.KuaijieK8Fragment;
import com.sn.blackdianqi.fragment.KuaijieK9Fragment;
import com.sn.blackdianqi.fragment.LengnuanFragment;
import com.sn.blackdianqi.fragment.QinangFragment;
import com.sn.blackdianqi.fragment.SmartSleepFragment;
import com.sn.blackdianqi.fragment.WeitiaoW10Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW11Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW12Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW13Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW14Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW18Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW1Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW2Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW3Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW4Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW6Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW7Fragment;
import com.sn.blackdianqi.fragment.WeitiaoW8Fragment;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.util.LogUtils;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.util.ToastUtils;
import com.sn.blackdianqi.view.NoScrollViewPager;
import com.sn.blackdianqi.view.TranslucentActionBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeMcuActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    public static final String TAG = "HomeMcuActivity";

    // 设置tab个数
    private final static int tabCount = 3;

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.ll_content)
    RelativeLayout relativeLayout;

    @BindView(R.id.vp_home)
    NoScrollViewPager viewPager;

    @BindView(R.id.bottom_tabs)
    LinearLayout bottomTabs;

    @BindView(R.id.tab1)
    LinearLayout tab1;
    @BindView(R.id.tab1_img)
    ImageView tab1Img;
    @BindView(R.id.tab1_text)
    TextView tab1TextView;

    @BindView(R.id.tab2)
    LinearLayout tab2;
    @BindView(R.id.tab2_img)
    ImageView tab2Img;
    @BindView(R.id.tab2_text)
    TextView tab2TextView;


    @BindView(R.id.tab3)
    LinearLayout tab3;
    @BindView(R.id.tab3_img)
    ImageView tab3Img;
    @BindView(R.id.tab3_text)
    TextView tab3TextView;


    List<TextView> tabTextViews;
    List<ImageView> tabImageViews;

    List<BaseFragment> fragments;
    TabPagerAdapter tabPagerAdapter;


    // 特征值
    protected BluetoothGattCharacteristic characteristic;
    private String cmdMain;//总控设备状态码
    private String deviceType;//当前显示的第一个tab
    private int tabBarNum = 0;//总共有几个子集
    private String bedState;
    private String qiNangState;
    private String lengNuanState;

    @Override
    public void onLeftClick() {
        Intent intent = new Intent(this, MainMcuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onRightClick() {
        Intent intent = new Intent(HomeMcuActivity.this, Setting2Activity.class);
        intent.putExtra("cmd", cmdMain);
        startActivityForResult(intent, 10000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10000 && resultCode == 20000) {
            cmdMain = data.getStringExtra("cmd");
            initView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_home_mcu);
        ButterKnife.bind(this);
        actionBar.setData(null, R.mipmap.ic_back, null, R.mipmap.ic_set, getString(R.string.setting), this);
        actionBar.setStatusBarHeight(getStatusBarHeight());

        cmdMain = getIntent().getStringExtra("cmd");
        deviceType = getIntent().getStringExtra("deviceType");

        initView();
    }


    private void initView() {
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
        tabTextViews = new ArrayList<>();
        tabTextViews.add(tab1TextView);
        tabTextViews.add(tab2TextView);
        tabTextViews.add(tab3TextView);

        tabImageViews = new ArrayList<>();
        tabImageViews.add(tab1Img);
        tabImageViews.add(tab2Img);
        tabImageViews.add(tab3Img);

        fragments = new ArrayList<>();

        bedState = cmdMain.substring(18, 20);
        qiNangState = cmdMain.substring(24, 26);
        lengNuanState = cmdMain.substring(30, 32);

        if (TextUtils.equals(bedState, "0A")) {
            tabBarNum++;
            tab1.setVisibility(View.VISIBLE);
            fragments.add(new DiandongFragment());
        } else {
            tab1.setVisibility(View.GONE);
        }
        if (TextUtils.equals(qiNangState, "0B")) {
            tabBarNum++;
            tab2.setVisibility(View.VISIBLE);
            fragments.add(new QinangFragment());
        } else {
            tab2.setVisibility(View.GONE);
        }
        if (TextUtils.equals(lengNuanState, "0C")) {
            tabBarNum++;
            tab3.setVisibility(View.VISIBLE);
            fragments.add(new LengnuanFragment());
        } else {
            tab3.setVisibility(View.GONE);
        }

        if (tabBarNum > 1) {
            bottomTabs.setVisibility(View.VISIBLE);
        } else {
            bottomTabs.setVisibility(View.GONE);
        }

        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.setScroll(true);
        viewPager.setOffscreenPageLimit(3);

        if (TextUtils.equals(deviceType, "0A")) {
            setCurrentTab(0);
            actionBar.setTitle(getResources().getString(R.string.diandongtitle));
        } else if (TextUtils.equals(deviceType, "0B")) {
            setCurrentTab(1);
            actionBar.setTitle(getResources().getString(R.string.qinangtitle));
        } else if (TextUtils.equals(deviceType, "0C")) {
            setCurrentTab(2);
            actionBar.setTitle(getResources().getString(R.string.lengnuantitle));
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("onPageSelected:", position + "");
                BaseFragment baseFragment = fragments.get(position);
                baseFragment.onResume();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setCurrentTab(int tabIndex) {
        for (int i = 0; i < tabCount; i++) {
            if (tabIndex == i) {
                tabTextViews.get(i).setSelected(true);
                tabImageViews.get(i).setSelected(true);
            } else {
                tabTextViews.get(i).setSelected(false);
                tabImageViews.get(i).setSelected(false);
            }
        }
        switch (tabIndex) {//由于tab与fragment不一定能完全对应
            case 1:
                if (!TextUtils.equals(bedState, "0A")) {//电动床存在
                    tabIndex = 0;
                }
                break;
            case 2:
                if (!TextUtils.equals(bedState, "0A") || !TextUtils.equals(qiNangState, "0B")) {//电动床存在、气囊不存在
                    tabIndex = 1;
                }
                break;
        }
        viewPager.setCurrentItem(tabIndex, false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab1:
                setCurrentTab(0);
                actionBar.setTitle(getResources().getString(R.string.diandongtitle));
                break;
            case R.id.tab2:
                setCurrentTab(1);
                actionBar.setTitle(getResources().getString(R.string.qinangtitle));
                break;
            case R.id.tab3:
                setCurrentTab(2);
                actionBar.setTitle(getResources().getString(R.string.lengnuantitle));
                break;
            default:
                break;
        }
    }
}