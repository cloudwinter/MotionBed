package com.sn.blackdianqi.dialog;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sn.blackdianqi.R;


/**
 * PermissionsDialog
 * 二次确认框
 *
 * @author caojianzhen
 * @version v1.0
 * @Time 2018-6-13
 */
public class DoubleConfirmDialog extends Dialog implements View.OnClickListener {

    private View mView;
    private Context mContext;
    /**
     * 标题、内容
     */
    private TextView mTitle;
    private EditText mMsg;
    /**
     * 取消、确定
     */
    private Button mCancleBut;
    private Button mDetermineBut;
    private OnPermissionsDialogListener listener;

    public static DoubleConfirmDialog builder(Context context) {
        return new DoubleConfirmDialog(context);
    }

    public DoubleConfirmDialog setLeftButName(String msg) {
        mCancleBut.setText(msg);
        return this;
    }

    public DoubleConfirmDialog setLeftButVisibility(int visibility) {
        mCancleBut.setVisibility(visibility);
        return this;
    }

    public DoubleConfirmDialog setMsgEdit(boolean isEnabled) {
        mMsg.setEnabled(isEnabled);
        return this;
    }

    public DoubleConfirmDialog setInputType(int type) {
        mMsg.setInputType(type);
        return this;
    }

    public DoubleConfirmDialog setMsgEdit(String num) {
        mMsg.setText(num);
        return this;
    }

    public DoubleConfirmDialog setMsgEditHint(String hint) {
        mMsg.setHint(hint);
        return this;
    }

    public DoubleConfirmDialog setRightButName(String msg) {
        mDetermineBut.setText(msg);
        return this;
    }

    public DoubleConfirmDialog setContent(String msg) {
//        mMsg.setText(Html.fromHtml(msg));
        mMsg.setText(msg);
        return this;
    }

    public DoubleConfirmDialog setTitle(String msg) {
        mTitle.setText(msg);
        return this;
    }

    public DoubleConfirmDialog setListener(OnPermissionsDialogListener listener) {
        this.listener = listener;
        return this;
    }

    public DoubleConfirmDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog);
        mView = LayoutInflater.from(context).inflate(R.layout.login_out_dialog, null);
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
        mMsg = mView.findViewById(R.id.msg);
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
            listener.cancleOnClick(DoubleConfirmDialog.this);
        } else if (i == R.id.determineBut) {
            String content = mMsg.getText().toString().trim();
            listener.determineOnClick(DoubleConfirmDialog.this, content);
        }
    }

    public interface OnPermissionsDialogListener {
        void cancleOnClick(DoubleConfirmDialog dialog);

        void determineOnClick(DoubleConfirmDialog dialog, String content);

    }
}
