package com.sn.blackdianqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.util.LogUtils;


/**
 * 记忆view
 */
public class JiyiSmallView extends RelativeLayout {

    public static final String TAG = "JiyiView";

    Context mContext;

    ImageView imageView;
    TextView titleTextView;

    int bgNormalRes = -1;
    int bgSelectedRes = -1;

    public JiyiSmallView(Context context) {
        super(context, null);
    }

    public JiyiSmallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        String title = "";
        float size = 0;
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.JiyiView);
            bgNormalRes = typedArray.getResourceId(R.styleable.JiyiView_bgnormal, -1);
            bgSelectedRes = typedArray.getResourceId(R.styleable.JiyiView_bgselected, -1);
            title = typedArray.getString(R.styleable.JiyiView_title);
            size = typedArray.getDimension(R.styleable.JiyiView_size, 14);
            LogUtils.i(TAG, "title:" + title);
        }
        //setOrientation(HORIZONTAL);
        View contentView = inflate(getContext(), R.layout.view_jiyi_small, this);
        imageView = contentView.findViewById(R.id.img_dian);
        titleTextView = contentView.findViewById(R.id.text_title);
        titleTextView.setText(title);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
//        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, getResources().getDisplayMetrics()));
        if (bgNormalRes != -1) {
            setBackground(ContextCompat.getDrawable(mContext, bgNormalRes));
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            imageView.setImageResource(R.mipmap.dian_green);
            if (bgSelectedRes != -1) {
                setBackground(ContextCompat.getDrawable(mContext, bgSelectedRes));
            }
        } else {
            imageView.setImageResource(R.mipmap.dian_black);
            if (bgNormalRes != -1) {
                setBackground(ContextCompat.getDrawable(mContext, bgNormalRes));
            }
        }
    }
}
