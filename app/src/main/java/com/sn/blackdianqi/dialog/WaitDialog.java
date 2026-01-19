package com.sn.blackdianqi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sn.blackdianqi.R;


/**
 * Created by yanjunhui
 * 2016/8/4
 * email:303767416@qq.com
 */
public class WaitDialog extends Dialog {

    private Animation animation = null;
    private TextView hintTv;
    private ImageView image;
    private boolean isCanceled;

    public static WaitDialog showProgressDialog(Context context, String message) {
        WaitDialog dialog = new WaitDialog(context, message);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    public static WaitDialog showProgressDialog(Context context, String message, boolean canceledOnTouchOutside,
                                                boolean cancelable) {
        WaitDialog dialog = new WaitDialog(context, message);
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dialog.setCancelable(cancelable);
        dialog.show();
        return dialog;
    }

    public void setHint(String message) {
        hintTv.setText(message);
    }

    public WaitDialog(Context context) {
        super(context, R.style.waiting_dialog_style);
        initView(context, context.getString(R.string.connecting));
    }

    public WaitDialog(Context context, String message) {
        super(context, R.style.waiting_dialog_style);
        initView(context, message);
    }

    public WaitDialog(Context context, int resId) {
        super(context, R.style.waiting_dialog_style);
        initView(context, context.getString(resId));
    }

    private void initView(Context context, String message) {
        View view = LayoutInflater.from(context).inflate(R.layout.dailog_waiting, null);

        if (!TextUtils.isEmpty(message)) {
            hintTv = (TextView) view.findViewById(R.id.progress_message);
            hintTv.setText(message);
        }

        image = (ImageView) view.findViewById(R.id.progress_view);
        animation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
        setContentView(view);
    }


    @Override
    public void show() {
        super.show();
        image.startAnimation(animation);
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
            animation.cancel();
        }
        isCanceled = true;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

}
