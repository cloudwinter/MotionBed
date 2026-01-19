package com.sn.blackdianqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.util.LogUtils;

import androidx.core.content.ContextCompat;

/**
 * 椭圆按键
 */
public class AnjianTuoYuanView extends LinearLayout {

    public static final String TAG = "AnjianTuoYuanView";

    Context mContext;

    ImageView iconImageView;
    TextView titleTextView;

    int bgNormalRes = -1;
    int bgSelectedRes = -1;

    public AnjianTuoYuanView(Context context) {
        super(context,null);
    }

    public AnjianTuoYuanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        String title = "";
        int iconRes = -1;
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.AnjianTuoYuanView);
            iconRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView_icon,-1);
            bgNormalRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView_bgnormal,-1);
            bgSelectedRes = typedArray.getResourceId(R.styleable.AnjianTuoYuanView_bgselected,-1);
            title = typedArray.getString(R.styleable.AnjianTuoYuanView_title);
            LogUtils.i(TAG, "title:" + title);
        }
        setOrientation(HORIZONTAL);
        View contentView = inflate(getContext(), R.layout.view_anjian_tuoyuan, this);
        iconImageView = contentView.findViewById(R.id.img_xr);
        titleTextView = contentView.findViewById(R.id.text_title);
        titleTextView.setText(title);
        if (iconRes != -1) {
            iconImageView.setBackground(ContextCompat.getDrawable(mContext, iconRes));
        }
        if (bgNormalRes != -1) {
            setBackground(ContextCompat.getDrawable(mContext,bgNormalRes));
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            if (bgSelectedRes != -1) {
                setBackground(ContextCompat.getDrawable(mContext,bgSelectedRes));
            }
        } else {
            if (bgNormalRes != -1) {
                setBackground(ContextCompat.getDrawable(mContext,bgNormalRes));
            }
        }
    }
}
