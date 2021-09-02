package com.sn.blackdianqi.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.dialog.LanguageDialog;
import com.sn.blackdianqi.util.BlueUtils;
import com.sn.blackdianqi.view.LoggerView;
import com.sn.blackdianqi.view.TranslucentActionBar;

import java.util.Locale;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sn.blackdianqi.BuildConfig.Debuggable;


public class SettingActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener, View.OnClickListener {

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;

    @BindView(R.id.ll_connect)
    LinearLayout llConnect;
    @BindView(R.id.tv_connect)
    TextView tvConnect;

    @BindView(R.id.ll_language)
    LinearLayout llLanguage;
    @BindView(R.id.tv_language)
    TextView tvLanguage;


    @BindView(R.id.ll_version)
    LinearLayout llVersion;
    @BindView(R.id.ll_privacy)
    LinearLayout llPrivacy;

    @BindView(R.id.ll_debug)
    LinearLayout llDebug;


    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
        // 设置title
        actionBar.setData(getString(R.string.blue_equipment), R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }

        initView();
    }

    private void initView() {
        llConnect.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        llPrivacy.setOnClickListener(this);
        llDebug.setOnClickListener(this);
        if (Debuggable) {
            llDebug.setVisibility(View.VISIBLE);
        }
        // 获取当前系统的语言
        Locale curLocale = getResources().getConfiguration().locale;
        //通过Locale的equals方法，判断出当前语言环境
        if (curLocale.getLanguage().equals("en")) {
            //英文
            tvLanguage.setText(R.string.english);
        } else {
            //法文
            tvLanguage.setText(R.string.french);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BlueUtils.isConnected()) {
            tvConnect.setText(R.string.connected);
        } else {
            tvConnect.setText(R.string.not_connected);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_connect:
                Intent intent = new Intent(SettingActivity.this, ConnectActivity.class);
                intent.putExtra("from","set");
                startActivity(intent);
                break;
            case R.id.ll_language:
                LanguageDialog languageDialog = new LanguageDialog(this);
                languageDialog.show();
                break;
            case R.id.ll_privacy:
                Intent webIntent = new Intent(SettingActivity.this, WebActivity.class);
                startActivity(webIntent);
                break;
            case R.id.ll_debug:
                LoggerView.me.loggerSwitch();
                break;
        }
    }
}
