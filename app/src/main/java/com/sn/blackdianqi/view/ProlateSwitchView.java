package com.sn.blackdianqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sn.blackdianqi.R;

import androidx.annotation.Nullable;

/**
 * 长椭圆的开关
 * Created by xiayundong on 2022/1/3.
 */
public class ProlateSwitchView extends LinearLayout {

    private Context mContext;

    private TextView mTitleView;
    private ImageView mSwitchImageView;

    public ProlateSwitchView(Context context) {
        super(context, null);
    }

    public ProlateSwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        String title = "";
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ProlateSwitchView);
            title = typedArray.getString(R.styleable.ProlateSwitchView_title);
        }
        setOrientation(HORIZONTAL);
        inflate(mContext,R.layout.view_prolate_switch,this);
        mTitleView = findViewById(R.id.tv_title);
        mSwitchImageView = findViewById(R.id.ic_switch);
        mTitleView.setText(title);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            mSwitchImageView.setImageResource(R.mipmap.dian_normal);
        } else {
            mSwitchImageView.setImageResource(R.mipmap.dian_selected);
        }
    }

    public void setTitle(String text) {
        mTitleView.setText(text);
    }
}
