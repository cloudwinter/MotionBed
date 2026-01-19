package com.sn.blackdianqi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.RunningContext;
import com.sn.blackdianqi.adapter.WifiAdapter;
import com.sn.blackdianqi.base.BaseActivity;
import com.sn.blackdianqi.view.TranslucentActionBar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiListActivity extends BaseActivity implements TranslucentActionBar.ActionBarClickListener {

    @BindView(R.id.actionbar)
    TranslucentActionBar actionBar;
    @BindView(R.id.rv_wifi)
    RecyclerView rvWifi;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;

    private WifiAdapter wifiAdapter;
    private WifiManager mWifiManager;
    public static int RESULT_CODE = 109;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_wifi_list);
        ButterKnife.bind(this);
        actionBar.setData("WI-FI", R.mipmap.ic_back, null, 0, "", this);
        actionBar.setStatusBarHeight(getStatusBarHeight());
        rvWifi.setLayoutManager(new LinearLayoutManager(this));
        rvWifi.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        refresh.setOnRefreshListener(mRefreshListener);
        wifiAdapter = new WifiAdapter(this);
        rvWifi.setAdapter(wifiAdapter);
        initView();
        getWifiList();
    }


    /**
     * 刷新。
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getWifiList();
            refresh.setRefreshing(false);
        }
    };

    protected void initView() {
        wifiAdapter.setOnItemClickListener(new WifiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull String wifiname) {
                Intent intent = new Intent();
                intent.putExtra("wifiName", wifiname);
                setResult(RESULT_CODE, intent);
                finish();
            }
        });
    }

    /**
     * 获取WIFI 列表
     */
    private void getWifiList() {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        }

        if (RunningContext.checkLocationPermission(this, true)) {
            List<ScanResult> scanResults = mWifiManager.getScanResults();
            if (scanResults.size() > 0) {
                tvNoData.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
            } else {
                refresh.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            }
            wifiAdapter.notifyDataSetChanged(filterScanResult(scanResults));
        }
    }

    /**
     * 以 SSID 为关键字，过滤掉信号弱的选项以及为空的
     *
     * @param list
     * @return
     */
    public static List<ScanResult> filterScanResult(final List<ScanResult> list) {
        List<ScanResult> scanResults2 = new ArrayList<>();
        List<ScanResult> scanResults5 = new ArrayList<>();
        LinkedHashMap<String, ScanResult> linkedMap = new LinkedHashMap<>(list.size());
        for (ScanResult rst : list) {
            if (!TextUtils.isEmpty(rst.SSID)) {
                int frequency = rst.frequency;
                if (linkedMap.containsKey(rst.SSID)) {
                    if (rst.level > linkedMap.get(rst.SSID).level) {
                        linkedMap.put(rst.SSID, rst);
                    }
                    continue;
                }
                linkedMap.put(rst.SSID, rst);

            }
        }
        for (ScanResult scanResult : linkedMap.values()) {
            if (scanResult.frequency > 4000) {
                scanResults5.add(scanResult);
            } else {
                scanResults2.add(scanResult);
            }
        }
        list.clear();
        list.addAll(scanResults2);
        list.addAll(scanResults5);
        scanResults2.clear();
        scanResults2.clear();
        return list;
    }

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {

    }
}