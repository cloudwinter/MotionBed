package com.sn.blackdianqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sn.blackdianqi.R;

import androidx.annotation.Nullable;

/**
 * 长椭圆的item
 * Created by xiayundong on 2022/1/3.
 */
public class ProlateItemView extends LinearLayout {

    private Context mContext;

    private TextView mTitleView;
    private TextView mDescView;

    public ProlateItemView(Context context) {
        super(context, null);
    }

    public ProlateItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        String title = "";
        String desc = "";
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ProlateItemView);
            title = typedArray.getString(R.styleable.ProlateItemView_title);
            desc = typedArray.getString(R.styleable.ProlateItemView_desc);
        }
        setOrientation(HORIZONTAL);
        inflate(mContext,R.layout.view_prolate_item,this);
        mTitleView = findViewById(R.id.tv_title);
        mDescView = findViewById(R.id.tv_desc);
        mTitleView.setText(title);
        mDescView.setText(desc);
    }


    public void setDesc(String desc) {
        mDescView.setText(desc);
    }
}
