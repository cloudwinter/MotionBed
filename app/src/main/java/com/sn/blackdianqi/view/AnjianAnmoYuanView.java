package com.sn.blackdianqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sn.blackdianqi.R;

import androidx.core.content.ContextCompat;


public class AnjianAnmoYuanView extends LinearLayout {


    public static final String TAG = "JiyiView";

    Context mContext;

    ImageView iconImageView;
    TextView titleTextView;

    int bgNormalRes = -1;
    int bgSelectedRes = -1;
    int imgNormalRes = -1;
    int imgSelectedRes = -1;

    public AnjianAnmoYuanView(Context context) {
        super(context,null);
    }

    public AnjianAnmoYuanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        String title = "";
        int iconRes = -1;
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.AnjianAnmoYuanView);
            iconRes = typedArray.getResourceId(R.styleable.AnjianAnmoYuanView_icon,-1);
            bgNormalRes = typedArray.getResourceId(R.styleable.AnjianAnmoYuanView_bgnormal,-1);
            bgSelectedRes = typedArray.getResourceId(R.styleable.AnjianAnmoYuanView_bgselected,-1);
            imgNormalRes = typedArray.getResourceId(R.styleable.AnjianAnmoYuanView_imgnormal,-1);
            imgSelectedRes = typedArray.getResourceId(R.styleable.AnjianAnmoYuanView_imgselected,-1);
            title = typedArray.getString(R.styleable.AnjianAnmoYuanView_title);
        }
        setOrientation(VERTICAL);
        View contentView = inflate(mContext,R.layout.view_anjian_anmo_yuan,this);
        iconImageView = findViewById(R.id.img_icon);
        titleTextView = findViewById(R.id.text_title);
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
            titleTextView.setSelected(true);
            if (bgSelectedRes != -1) {
                setBackground(ContextCompat.getDrawable(mContext,bgSelectedRes));
            }
            if (imgSelectedRes != -1) {
                iconImageView.setBackground(ContextCompat.getDrawable(mContext,imgSelectedRes));
            }
        } else {
            titleTextView.setSelected(false);
            if (bgNormalRes != -1) {
                setBackground(ContextCompat.getDrawable(mContext,bgNormalRes));
            }
            if (imgNormalRes != -1) {
                iconImageView.setBackground(ContextCompat.getDrawable(mContext,imgNormalRes));
            }
        }
    }


}
