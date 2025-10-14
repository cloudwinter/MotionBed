package com.sn.blackdianqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.util.LogUtils;

/**
 * 椭圆按键
 */
public class AnjianWeitiaoVerticalBottomView extends LinearLayout implements View.OnTouchListener {

    public static final String TAG = "AnjianChangTuoYuanBottomView";

    Context mContext;

    ChildTouchListener childTouchListener;

    LinearLayout sanjiaoBottomLayout;
    ImageView sanjiaoBottomImageView;

    int bgNormalRes = -1;
    int bgSelectedRes = -1;

    public AnjianWeitiaoVerticalBottomView(Context context) {
        super(context, null);
    }

    public AnjianWeitiaoVerticalBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.AnjianTuoYuanView);
            bgNormalRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView_bgnormal, -1);
            bgSelectedRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView_bgselected, -1);
        }
        setOrientation(HORIZONTAL);
        View contentView = inflate(getContext(), R.layout.view_anjian_weitiao_vertical_bottom, this);
        sanjiaoBottomLayout = contentView.findViewById(R.id.layout_sanjiao_bottom);
        sanjiaoBottomImageView = contentView.findViewById(R.id.img_sanjiao_bottom);

        if (bgNormalRes != -1) {
            setBackground(ContextCompat.getDrawable(mContext, bgNormalRes));
        }
        sanjiaoBottomLayout.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (v.getId()) {
            case R.id.layout_sanjiao_bottom:
                if (childTouchListener != null) {
                    childTouchListener.onBottomTouch(event);
                }
                if (MotionEvent.ACTION_DOWN == action) {
                    sanjiaoBottomImageView.setSelected(true);
                    if (bgSelectedRes != -1) {
                        setBackground(ContextCompat.getDrawable(mContext, bgSelectedRes));
                    }
                } else if (MotionEvent.ACTION_UP == action) {
                    sanjiaoBottomImageView.setSelected(false);
                    if (bgNormalRes != -1) {
                        setBackground(ContextCompat.getDrawable(mContext, bgNormalRes));
                    }
                } else if (MotionEvent.ACTION_CANCEL == action) {
                    sanjiaoBottomImageView.setSelected(false);
                    if (bgNormalRes != -1) {
                        setBackground(ContextCompat.getDrawable(mContext, bgNormalRes));
                    }
                }
                break;
        }
        return true;
    }

    public void setChildTouchListener(ChildTouchListener childTouchListener) {
        this.childTouchListener = childTouchListener;
    }


}
