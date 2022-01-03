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
 * 星期的itemView
 * Created by xiayundong on 2021/9/21.
 */
public class WeekItemView extends LinearLayout {

    private Context mContext;

    private TextView mTitleTV;
    private ImageView mSelectedImg;

    public WeekItemView(Context context) {
        super(context,null);
    }

    public WeekItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        String title = "";
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.WeekItemView);
            title = typedArray.getString(R.styleable.WeekItemView_title);
        }
        setOrientation(VERTICAL);
        inflate(mContext,R.layout.view_week_item,this);
        mTitleTV = findViewById(R.id.tv_title);
        mSelectedImg = findViewById(R.id.ic_selected);
        mTitleTV.setText(title);
        mSelectedImg.setVisibility(GONE);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            mSelectedImg.setVisibility(VISIBLE);
        } else {
            mSelectedImg.setVisibility(GONE);
        }
    }
}
