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
 * 长椭圆的item
 * Created by xiayundong on 2022/1/3.
 */
public class ProlateItemSwitchView extends LinearLayout {

    private Context mContext;

    private TextView mTitleView;
    private TextView mDescView;
    private ImageView mSwitchImageView;

    private boolean mSelected;

    public ProlateItemSwitchView(Context context) {
        super(context, null);
    }

    public ProlateItemSwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        String title = "";
        String desc = "";
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ProlateItemSwitchView);
            title = typedArray.getString(R.styleable.ProlateItemSwitchView_title);
            desc = typedArray.getString(R.styleable.ProlateItemSwitchView_desc);
        }
        setOrientation(HORIZONTAL);
        inflate(mContext,R.layout.view_prolate_item_switch,this);
        mTitleView = findViewById(R.id.tv_title);
        mDescView = findViewById(R.id.tv_desc);
        mSwitchImageView = findViewById(R.id.ic_switch);
        mTitleView.setText(title);
        mDescView.setText(desc);
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
        if (selected) {
            mSwitchImageView.setImageResource(R.mipmap.dian_normal);
        } else {
            mSwitchImageView.setImageResource(R.mipmap.dian_selected);
        }
    }

    public boolean getSelected() {
        return mSelected;
    }

    public void setDesc(String desc) {
        mDescView.setText(desc);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }
}
