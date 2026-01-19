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
public class FuweiDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private TextView tv_cancel;
    private TextView tv_fuwei;

    private OnFuweiClick onFuweiClick;

    public FuweiDialog(@NonNull Context context) {
        super(context, R.style.LanguageDialogStyle);
        mContext = context;
        initView(context);
    }

    public FuweiDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_fuwei, null);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);
        tv_fuwei = view.findViewById(R.id.tv_fuwei);
        tv_fuwei.setOnClickListener(this);
        setContentView(view);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        view.setLayoutParams(layoutParams);

        getWindow().setGravity(Gravity.CENTER);
    }

    public void setOnFuweiClick(OnFuweiClick onFuweiClick) {
        this.onFuweiClick = onFuweiClick;
    }

    @Override
    public void show() {
        super.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                if (onFuweiClick != null) {
                    onFuweiClick.onCancel(v);
                }
                break;
            case R.id.tv_fuwei:
                dismiss();
                if (onFuweiClick != null) {
                    onFuweiClick.onFuwei(v);
                }
                break;
        }
    }

    public interface OnFuweiClick {

        void onCancel(View view);

        void onFuwei(View view);
    }
}
