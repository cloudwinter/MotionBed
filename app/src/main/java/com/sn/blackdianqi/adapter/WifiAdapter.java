package com.sn.blackdianqi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.bean.TempModel;

import java.util.ArrayList;
import java.util.List;

public class WifiAdapter  extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {

    private Context context;
    private List<ScanResult> dataList = new ArrayList<>();
    private OnItemClickListener mClickListener;
    private int selectIndex = 4;

    public WifiAdapter(Context context) {
        this.context = context;
    }

    public void notifyDataSetChanged(List<ScanResult> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_wifi, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult model = dataList.get(position);
        holder.tvWifiName.setText(model.SSID);

        holder.tvWifiName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(model.SSID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(String wifiName);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvWifiName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWifiName = itemView.findViewById(R.id.tvWifiName);
        }
    }
}
