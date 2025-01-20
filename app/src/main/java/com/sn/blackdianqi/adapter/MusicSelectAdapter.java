package com.sn.blackdianqi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.bean.MusicBean;

import java.util.ArrayList;
import java.util.List;

public class MusicSelectAdapter extends RecyclerView.Adapter<MusicSelectAdapter.ViewHolder> {


    private Context context;
    private List<MusicBean> dataList = new ArrayList<>();
    private int selectIndex = -1;
    private OnItemClickListener mClickListener;

    public MusicSelectAdapter(Context context, List<MusicBean> musicList) {
        this.context = context;
        this.dataList = musicList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_music_select, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicBean musicBean = dataList.get(position);
        holder.tvName.setText(musicBean.getName());
        if (selectIndex == position) {
            holder.ivSelect.setVisibility(View.VISIBLE);
        } else {
            holder.ivSelect.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectIndex = position;
                mClickListener.onItemClick(position);
                notifyDataSetChanged();
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

        private final TextView tvName;
        private final ImageView ivSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            ivSelect = itemView.findViewById(R.id.ivSelect);
        }
    }
}
