package com.sn.blackdianqi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

public class TempAdapter extends RecyclerView.Adapter<TempAdapter.ViewHolder> {

    private Context context;
    private List<TempModel> dataList = new ArrayList<>();
    private OnItemClickListener mClickListener;
    private int selectIndex = 4;

    public TempAdapter(Context context, List<TempModel> musicList) {
        this.context = context;
        this.dataList = musicList;
    }

    public void setSelectIndex(int index) {
        this.selectIndex = index;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_temp, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TempModel tempModel = dataList.get(position);
        holder.tvValue.setText(tempModel.getTemp());
        holder.tvValue.setTextColor(Color.parseColor(tempModel.getTempColor()));
        holder.tvUnit.setTextColor(Color.parseColor(tempModel.getTempColor()));

        if (position == selectIndex) {
            if (position == 4) {//关闭文字
                holder.tvValue.setTextSize(12);
                holder.tvUnit.setVisibility(View.GONE);
            } else {
                holder.tvValue.setTextSize(18);
                holder.tvUnit.setVisibility(View.VISIBLE);
            }
            holder.tvValue.setTypeface(null,Typeface.BOLD);
        } else {
            if (position == 4) {//关闭文字
                holder.tvValue.setTextSize(10);
            } else {
                holder.tvValue.setTextSize(14);
            }
            holder.tvUnit.setVisibility(View.GONE);
            holder.tvValue.setTypeface(null,Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvValue, tvUnit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(R.id.tvValue);
            tvUnit = itemView.findViewById(R.id.tvUnit);

        }
    }
}
