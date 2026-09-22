package com.sn.blackdianqi.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.util.Prefer;
import com.sn.blackdianqi.view.TranslucentActionBar;
import androidx.databinding.DataBindingUtil;
import com.sn.blackdianqi.databinding.ActivityWebCommonBinding;


public class WebCommonActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener {

    TranslucentActionBar actionBar;
    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWebCommonBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_web_common);
        actionBar = binding.actionbar;
        webView = binding.web;
        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");

        actionBar.setData(title, R.mipmap.ic_back, null, 0, null, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            actionBar.setStatusBarHeight(getStatusBarHeight());
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        WebSettings webSettings = webView.getSettings();
        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
        webSettings.setJavaScriptEnabled(true);//支持js
        webSettings.setSupportZoom(true); // 可以缩放
        webSettings.setBuiltInZoomControls(true); // 显示放大缩小

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.loadUrl(url);
    }

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {

    }

}
