package com.sn.blackdianqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.util.LogUtils;

import androidx.core.content.ContextCompat;

/**
 * 椭圆按键
 */
public class AnjianDuanWeitiaoView extends LinearLayout implements View.OnTouchListener {

    public static final String TAG = "AnjianChangTuoYuanView";

    Context mContext;

    ChildTouchListener childTouchListener;

    ImageView iconImageView;
    TextView titleTextView;
    LinearLayout sanjiaoTopLayout;
    ImageView sanjiaoTopImageView;
    LinearLayout sanjiaoBottomLayout;
    ImageView sanjiaoBottomImageView;

    int bgNormalRes = -1;

    public AnjianDuanWeitiaoView(Context context) {
        super(context, null);
    }

    public AnjianDuanWeitiaoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        String title = "";
        int iconRes = -1;
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.AnjianTuoYuanView);
            iconRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView_icon, -1);
            bgNormalRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView_bgnormal, -1);
            title = typedArray.getString(R.styleable.AnjianTuoYuanView_title);
            LogUtils.i(TAG, "title:" + title);
        }
        setOrientation(HORIZONTAL);
        View contentView = inflate(getContext(), R.layout.view_anjian_duan_weitiao, this);
        iconImageView = contentView.findViewById(R.id.img_xr);
        titleTextView = contentView.findViewById(R.id.text_title);
        sanjiaoTopLayout = contentView.findViewById(R.id.layout_sanjiao_top);
        sanjiaoBottomLayout = contentView.findViewById(R.id.layout_sanjiao_bottom);
        sanjiaoTopImageView = contentView.findViewById(R.id.img_sanjiao_top);
        sanjiaoBottomImageView = contentView.findViewById(R.id.img_sanjiao_bottom);
        titleTextView.setText(title);
        if (iconRes != -1) {
            iconImageView.setBackground(ContextCompat.getDrawable(mContext, iconRes));
        }
        if (bgNormalRes != -1) {
            setBackground(ContextCompat.getDrawable(mContext, bgNormalRes));
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
                    childTouchListener.onTopTouch(event);
                }
                if (MotionEvent.ACTION_DOWN == action) {
                    sanjiaoTopImageView.setSelected(true);
                } else if (MotionEvent.ACTION_UP == action) {
                    sanjiaoTopImageView.setSelected(false);
                } else if (MotionEvent.ACTION_CANCEL == action) {
                    sanjiaoTopImageView.setSelected(false);
                }

                break;
            case R.id.layout_sanjiao_bottom:
                if (childTouchListener != null) {
                    childTouchListener.onBottomTouch(event);
                }
                if (MotionEvent.ACTION_DOWN == action) {
                    sanjiaoBottomImageView.setSelected(true);
                } else if (MotionEvent.ACTION_UP == action) {
                    sanjiaoBottomImageView.setSelected(false);
                } else if (MotionEvent.ACTION_CANCEL == action) {
                    sanjiaoBottomImageView.setSelected(false);
                }

                break;

        }
        return true;
    }

    public void setChildTouchListener(ChildTouchListener childTouchListener) {
        this.childTouchListener = childTouchListener;
    }


}
