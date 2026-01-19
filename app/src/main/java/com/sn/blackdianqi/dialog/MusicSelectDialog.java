package com.sn.blackdianqi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.sn.blackdianqi.R;
import com.sn.blackdianqi.adapter.MusicSelectAdapter;
import com.sn.blackdianqi.bean.MusicBean;

import java.util.List;

public class MusicSelectDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private List<MusicBean> musicList;
    private MusicSelectAdapter musicSelectAdapter;
    private CallBackDialogListener listener;

    public MusicSelectDialog(Context context, List<MusicBean> musicList, CallBackDialogListener listener) {
        super(context, R.style.LanguageDialogStyle);
        mContext = context;
        this.listener = listener;
        this.musicList = musicList;
        initView(context);
        setCancelable(true);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_music, null);
        RecyclerView rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(context));
        musicSelectAdapter = new MusicSelectAdapter(context, musicList);
        DividerItemDecoration mDivider = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        rvList.addItemDecoration(mDivider);
        rvList.setAdapter(musicSelectAdapter);
        Button btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        setContentView(view);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels - 160;
        view.setLayoutParams(layoutParams);
        getWindow().setGravity(Gravity.CENTER);

        musicSelectAdapter.setOnItemClickListener(new MusicSelectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                listener.selectOnClick(musicList.get(position).getValue(),musicList.get(position).getName());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSave) {
            listener.saveOnClick();
            dismiss();
        }
    }

    public interface CallBackDialogListener {
        void selectOnClick(String value,String name);

        void saveOnClick();
    }
}
