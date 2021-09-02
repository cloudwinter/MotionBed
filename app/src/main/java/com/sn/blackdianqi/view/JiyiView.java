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
 * 记忆view
 */
public class JiyiView extends RelativeLayout {

    public static final String TAG = "JiyiView";

    Context mContext;

    ImageView imageView;
    TextView titleTextView;

    int bgNormalRes = -1;
    int bgSelectedRes = -1;

    public JiyiView(Context context) {
        super(context,null);
    }

    public JiyiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        String title = "";
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.JiyiView);
            bgNormalRes = typedArray.getResourceId(R.styleable.JiyiView_bgnormal,-1);
            bgSelectedRes = typedArray.getResourceId(R.styleable.JiyiView_bgselected,-1);
            title = typedArray.getString(R.styleable.JiyiView_title);
            LogUtils.i(TAG, "title:" + title);
        }
        //setOrientation(HORIZONTAL);
        View contentView = inflate(getContext(), R.layout.view_jiyi, this);
        imageView = contentView.findViewById(R.id.img_dian);
        titleTextView = contentView.findViewById(R.id.text_title);
        titleTextView.setText(title);
        if (bgNormalRes != -1) {
            setBackground(ContextCompat.getDrawable(mContext,bgNormalRes));
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            imageView.setImageResource(R.mipmap.dian_green);
            if (bgSelectedRes != -1) {
                setBackground(ContextCompat.getDrawable(mContext,bgSelectedRes));
            }
        } else {
            imageView.setImageResource(R.mipmap.dian_black);
            if (bgNormalRes != -1) {
                setBackground(ContextCompat.getDrawable(mContext,bgNormalRes));
            }
        }
    }
}
