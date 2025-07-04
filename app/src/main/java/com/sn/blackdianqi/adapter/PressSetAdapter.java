package com.sn.blackdianqi.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.bean.PressBean;
import com.sn.blackdianqi.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class PressSetAdapter extends RecyclerView.Adapter<PressSetAdapter.ViewHolder> {

    private Context context;
    private List<PressBean> dataList = new ArrayList<>();
    private OnItemClickListener mClickListener;
    private int selectIndex;

    public PressSetAdapter(Context context, List<PressBean> musicList) {
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

    public int getSelectValue() {
        return dataList.get(selectIndex).getValue();
    }

    public List<PressBean> getData() {
        return dataList;
    }

    public void setData(List<PressBean> pressList) {
        dataList = pressList;
        notifyDataSetChanged();
        LogUtils.e("pressValue", pressList.toString() + "");
    }

    public void setSelectValue(int position, int value) {
        dataList.get(position).setValue(value);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_press_set, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PressBean pressBean = dataList.get(position);
        holder.tvName.setText(pressBean.getName());

        ViewGroup.LayoutParams params = holder.tvValue.getLayoutParams();
        float dpHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (pressBean.value) * 10, context.getResources().getDisplayMetrics());
        params.height = (int) dpHeight; // 90dp转换为px
        holder.tvValue.setLayoutParams(params);

        if (selectIndex == position) {
            holder.ivSelect.setVisibility(View.VISIBLE);
        } else {
            holder.ivSelect.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(position);
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
        void onItemClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvValue;
        private final TextView tvName;
        private final ImageView ivSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(R.id.tvValue);
            tvName = itemView.findViewById(R.id.tvName);
            ivSelect = itemView.findViewById(R.id.ivSelect);
        }
    }
}
