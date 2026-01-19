package com.sn.blackdianqi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.view.VerticalSeekBar;

public class MusicAudioDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private int audio;
    private CallBackDialogListener listener;
    private VerticalSeekBar sb;

    public MusicAudioDialog(Context context, int audio, CallBackDialogListener listener) {
        super(context, R.style.LanguageDialogStyle);
        mContext = context;
        this.listener = listener;
        this.audio = audio;
        initView(context);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_music_audio, null);
        sb = view.findViewById(R.id.sb);
        sb.setProgress(audio);
        setContentView(view);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels - 160;
        view.setLayoutParams(layoutParams);
        getWindow().setGravity(Gravity.CENTER);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        sb.setOnProgressChangedListener(new VerticalSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChange(int progress) {
                if (audio != progress) {
                    audio = progress;
                    listener.audioOnClick(audio + 1);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
    }

    public interface CallBackDialogListener {
        void audioOnClick(int value);
    }
}
