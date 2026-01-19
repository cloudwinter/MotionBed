package com.sn.blackdianqi.dialog;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.util.Prefer;


/**
 * PermissionsDialog
 * 二次确认框
 *
 * @author caojianzhen
 * @version v1.0
 * @Time 2018-6-13
 */
public class PrivacyPolicyDialog extends Dialog implements View.OnClickListener {

    private View mView;
    private Context mContext;
    /**
     * 标题、内容
     */
    private TextView mTitle;
    private ImageView ivPP;
    private View line;
    /**
     * 取消、确定
     */
    private Button mCancleBut;
    private Button mDetermineBut;
    private OnPermissionsDialogListener listener;

    public static PrivacyPolicyDialog builder(Context context) {
        return new PrivacyPolicyDialog(context);
    }

    public PrivacyPolicyDialog setLeftButName(String msg) {
        mCancleBut.setText(msg);
        return this;
    }

    public PrivacyPolicyDialog setLeftButVisibility(int visibility) {
        mCancleBut.setVisibility(visibility);
        return this;
    }

    public PrivacyPolicyDialog setLineVisibility(int visibility) {
        line.setVisibility(visibility);
        return this;
    }

    public PrivacyPolicyDialog setRightButName(String msg) {
        mDetermineBut.setText(msg);
        return this;
    }

    public PrivacyPolicyDialog setContent() {
        // 获取当前系统的语言
        String language = Prefer.getInstance().getSelectedLanguage();
        if (language.equals("fr")) {
            ivPP.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.protocol_fr));
        } else if (language.equals("ja")) {
            ivPP.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.protocol_japan));
        } else if (language.equals("zh-rTW")) {
            ivPP.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.protocol_zh));
        } else if (language.equals("zh-rCN")) {
            ivPP.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.protocol_zh_rcn));
        } else if (language.equals("en")) {
            ivPP.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.protocol_en));
        }
        return this;
    }

    public PrivacyPolicyDialog setTitle(String msg) {
        mTitle.setText(msg);
        return this;
    }

    public PrivacyPolicyDialog setListener(OnPermissionsDialogListener listener) {
        this.listener = listener;
        return this;
    }

    public PrivacyPolicyDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog);
        mView = LayoutInflater.from(context).inflate(R.layout.privacy_policy_dialog, null);
        this.mContext = context;
        init();
    }

    private void init() {
        setContentView(mView);
        setCancelable(false);
        findViewByIds();
        setListener();
    }

    private void findViewByIds() {
        mTitle = mView.findViewById(R.id.title);
        ivPP = mView.findViewById(R.id.ivPP);
        line = mView.findViewById(R.id.line);
        mCancleBut = mView.findViewById(R.id.cancleBut);
        mDetermineBut = mView.findViewById(R.id.determineBut);
    }

    private void setListener() {
        mCancleBut.setOnClickListener(this);
        mDetermineBut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cancleBut) {
            listener.cancleOnClick(PrivacyPolicyDialog.this);
        } else if (i == R.id.determineBut) {
            listener.determineOnClick(PrivacyPolicyDialog.this);
        }
    }

    public interface OnPermissionsDialogListener {
        void cancleOnClick(PrivacyPolicyDialog dialog);

        void determineOnClick(PrivacyPolicyDialog dialog);

    }
}
