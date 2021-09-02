package com.sn.blackdianqi.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.sn.blackdianqi.R;
import com.sn.blackdianqi.bean.DeviceBean;
import com.sn.blackdianqi.util.BlueUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙设备自定义adapter
 */
public class BlueDeviceListAdapter extends BaseAdapter {

    private List<DeviceBean> deviceBeanList;

    private LayoutInflater mInflator;

    private List<String> addresList;

    public BlueDeviceListAdapter(Activity context) {
        super();
        deviceBeanList = new ArrayList<>();
        addresList = new ArrayList<>();
        mInflator = context.getLayoutInflater();
    }

    public DeviceBean addDevice(BluetoothDevice device, boolean isConnected) {
        if (device != null && !TextUtils.isEmpty(device.getName()) && !addresList.contains(device.getAddress())) {
            DeviceBean deviceBean = new DeviceBean();
            deviceBean.setConnected(isConnected);
            deviceBean.setTitle(BlueUtils.transferBlueName(device.getName()));
            deviceBean.setAddress(device.getAddress());
            deviceBeanList.add(deviceBean);
            addresList.add(device.getAddress());
            notifyDataSetChanged();
            return deviceBean;
        }
        return null;
    }


    public void addDevice(DeviceBean deviceBean) {
        if (deviceBean != null && !addresList.contains(deviceBean.getAddress())) {
            deviceBeanList.add(deviceBean);
            addresList.add(deviceBean.getAddress());
            notifyDataSetChanged();
        }
    }

    /**
     * 获取设备列表
     * @return
     */
    public List<DeviceBean> getDeviceList() {
        return deviceBeanList;
    }



    public void clear() {
        addresList.clear();
        deviceBeanList.clear();
    }

    @Override
    public int getCount() {
        return deviceBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return deviceBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * 重写getview
     **/
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // General ListView optimization code.
        // 加载listview每一项的视图
        view = mInflator.inflate(R.layout.item_connect_layout, null);
        // 初始化三个textview显示蓝牙信息
        TextView deviceName = view.findViewById(R.id.tv_title);
        // 去连接
        TextView tvConnect = view.findViewById(R.id.tv_connect);
        // 已连接
        LinearLayout lvConnected = view.findViewById(R.id.lv_connected);

        String device = deviceBeanList.get(i).getTitle();
        deviceName.setText(device);
        if (deviceBeanList.get(i).isConnected()) {
            tvConnect.setVisibility(View.GONE);
            lvConnected.setVisibility(View.VISIBLE);
        } else {
            tvConnect.setVisibility(View.VISIBLE);
            lvConnected.setVisibility(View.GONE);
        }
        return view;
    }
}
