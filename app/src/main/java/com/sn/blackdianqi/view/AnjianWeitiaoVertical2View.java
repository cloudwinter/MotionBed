package com.sn.blackdianqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.util.LogUtils;

/**
 * 椭圆按键
 */
public class AnjianWeitiaoVertical2View extends LinearLayout implements View.OnTouchListener {

    public static final String TAG = "AnjianChangTuoYuanTopView";

    Context mContext;

    ChildTouchListener childTouchListener;

    LinearLayout sanjiaoTopLayout;
    ImageView sanjiaoTopImageView;

    ImageView iconImageView;
    TextView titleTextView;

    LinearLayout sanjiaoBottomLayout;
    ImageView sanjiaoBottomImageView;


    int bgNormalTopRes = -1;
    int bgSelectedTopRes = -1;
    int bgNormalBottomRes = -1;
    int bgSelectedBottomRes = -1;
    int iconRes = -1;
    int selectIconRes = -1;

    public AnjianWeitiaoVertical2View(Context context) {
        super(context, null);
    }

    public AnjianWeitiaoVertical2View(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.AnjianTuoYuanView2);
            iconRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView2_icon, -1);
            selectIconRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView2_selecticon, -1);
            bgNormalTopRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView2_bgnormaltop, -1);
            bgSelectedTopRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView2_bgselectedtop, -1);
            bgNormalBottomRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView2_bgnormalbottom, -1);
            bgSelectedBottomRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView2_bgselectedbottom, -1);
        }
        setOrientation(HORIZONTAL);
        View contentView = inflate(getContext(), R.layout.view_anjian_weitiao_vertical2, this);
        iconImageView = contentView.findViewById(R.id.img_xr);
        titleTextView = contentView.findViewById(R.id.text_title);
        sanjiaoTopLayout = contentView.findViewById(R.id.layout_sanjiao_top);
        sanjiaoTopImageView = contentView.findViewById(R.id.img_sanjiao_top);
        sanjiaoBottomImageView = contentView.findViewById(R.id.img_sanjiao_bottom);
        sanjiaoBottomLayout = contentView.findViewById(R.id.layout_sanjiao_bottom);

        if (iconRes != -1){
            iconImageView.setBackground(ContextCompat.getDrawable(mContext, iconRes));
        }

        sanjiaoTopLayout.setOnTouchListener(this);
        sanjiaoBottomLayout.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (v.getId()) {
            case R.id.layout_sanjiao_top:
                if (childTouchListener != null) {
                    childTouchListener.onBottomTouch(event);
                }
                if (MotionEvent.ACTION_DOWN == action) {
                    LogUtils.d(TAG, "ACTION_DOWN");
                    sanjiaoTopImageView.setSelected(true);
                    if (bgSelectedTopRes != -1) {
                        sanjiaoTopLayout.setBackground(ContextCompat.getDrawable(mContext, bgSelectedTopRes));
                    }
                    if (selectIconRes != -1) {
                        iconImageView.setBackground(ContextCompat.getDrawable(mContext, selectIconRes));
                    }
                } else if (MotionEvent.ACTION_UP == action) {
                    LogUtils.d(TAG, "ACTION_UP");
                    sanjiaoTopImageView.setSelected(false);
                    if (bgNormalTopRes != -1) {
                        sanjiaoTopLayout.setBackground(ContextCompat.getDrawable(mContext, bgNormalTopRes));
                    }
                    if (iconRes != -1) {
                        iconImageView.setBackground(ContextCompat.getDrawable(mContext, iconRes));
                    }
                } else if (MotionEvent.ACTION_CANCEL == action) {
                    LogUtils.d(TAG, "ACTION_CANCEL");
                    sanjiaoTopImageView.setSelected(false);
                    if (bgNormalTopRes != -1) {
                        sanjiaoTopLayout.setBackground(ContextCompat.getDrawable(mContext, bgNormalTopRes));
                    }
                    if (iconRes != -1) {
                        iconImageView.setBackground(ContextCompat.getDrawable(mContext, iconRes));
                    }
                }
                break;
            case R.id.layout_sanjiao_bottom:
                if (childTouchListener != null) {
                    childTouchListener.onBottomTouch(event);
                }
                if (MotionEvent.ACTION_DOWN == action) {
                    sanjiaoBottomImageView.setSelected(true);
                    if (bgSelectedBottomRes != -1) {
                        sanjiaoBottomLayout.setBackground(ContextCompat.getDrawable(mContext, bgSelectedBottomRes));
                    }
                    if (selectIconRes != -1) {
                        iconImageView.setBackground(ContextCompat.getDrawable(mContext, selectIconRes));
                    }
                } else if (MotionEvent.ACTION_UP == action) {
                    sanjiaoBottomImageView.setSelected(false);
                    if (bgNormalBottomRes != -1) {
                        sanjiaoBottomLayout.setBackground(ContextCompat.getDrawable(mContext, bgNormalBottomRes));
                    }
                    if (iconRes != -1) {
                        iconImageView.setBackground(ContextCompat.getDrawable(mContext, iconRes));
                    }
                } else if (MotionEvent.ACTION_CANCEL == action) {
                    sanjiaoBottomImageView.setSelected(false);
                    if (bgNormalBottomRes != -1) {
                        sanjiaoBottomLayout.setBackground(ContextCompat.getDrawable(mContext, bgNormalBottomRes));
                    }
                    if (iconRes != -1) {
                        iconImageView.setBackground(ContextCompat.getDrawable(mContext, iconRes));
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
