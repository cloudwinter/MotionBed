package com.sn.blackdianqi.util;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;


/**
 * 时间倒计时
 */
public class CountDownTimerUtils extends CountDownTimer {
    private TextView mTextView;
    private long origMillisInFuture;

    public CountDownTimerUtils(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
        origMillisInFuture = millisInFuture;
    }

    private boolean hasBg = false;

    public void setHasBg(boolean hasBg) {
        this.hasBg = hasBg;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(millisUntilFinished / 1000 + "s");  //设置倒计时时间
        Log.e("倒计时", millisUntilFinished / 1000 + "s");
    }

    @Override
    public void onFinish() {
        mTextView.setText(origMillisInFuture / 1000 + "s");
    }
}