package com.sn.blackdianqi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sn.blackdianqi.R;

import androidx.annotation.NonNull;

/**
 * 语言对话框
 */
public class FuweiNextDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private TextView tv_next;

    private OnNextClick onNextClick;

    public FuweiNextDialog(@NonNull Context context) {
        super(context, R.style.LanguageDialogStyle);
        mContext = context;
        initView(context);
    }

    public FuweiNextDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_fuwei_next, null);
        tv_next = view.findViewById(R.id.tv_next);
        tv_next.setOnClickListener(this);
        setContentView(view);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        view.setLayoutParams(layoutParams);

        getWindow().setGravity(Gravity.CENTER);
    }

    public void setOnFuweiClick(OnNextClick onNextClick) {
        this.onNextClick = onNextClick;
    }

    @Override
    public void show() {
        super.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                dismiss();
                if (onNextClick != null) {
                    onNextClick.onNext(v);
                }
                break;
        }
    }

    public interface OnNextClick {

        void onNext(View view);
    }
}
