package com.sn.blackdianqi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.util.ToastUtils;

import androidx.annotation.NonNull;

/**
 * 语言对话框
 */
public class DataEntryInputDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private TextView tv_cancel;
    private TextView tv_sure;
    private EditText et_param;

    private OnButtonClick onButtonClick;

    public DataEntryInputDialog(@NonNull Context context) {
        super(context, R.style.LanguageDialogStyle);
        mContext = context;
        initView(context);
    }

    public DataEntryInputDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_data_entry_input, null);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);
        tv_sure = view.findViewById(R.id.tv_sure);
        tv_sure.setOnClickListener(this);
        et_param = view.findViewById(R.id.et_param);
        setContentView(view);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        view.setLayoutParams(layoutParams);

        getWindow().setGravity(Gravity.CENTER);
    }

    public void setOnButtonClick(OnButtonClick onButtonClick) {
        this.onButtonClick = onButtonClick;
    }

    @Override
    public void show() {
        super.show();
    }

    public void show(String val) {
        et_param.setText(val);
        show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                if (onButtonClick != null) {
                    onButtonClick.onCancel(v);
                }
                break;
            case R.id.tv_sure:
                String etParamV = et_param.getText().toString();
                if (TextUtils.isEmpty(etParamV)) {
                    ToastUtils.showToast(mContext,mContext.getString(R.string.please_input));
                    return;
                }
                dismiss();
                if (onButtonClick != null) {
                    onButtonClick.onSure(v,Integer.parseInt(etParamV));
                }
                break;
        }
    }

    public interface OnButtonClick {

        void onCancel(View view);

        void onSure(View view,int num);
    }
}
