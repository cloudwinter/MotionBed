package com.sn.blackdianqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sn.blackdianqi.R;
import com.sn.blackdianqi.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

/**
 * 椭圆按键
 */
public class AnjianAnmoView extends RelativeLayout implements View.OnClickListener {

    public static final String TAG = "AnjianChangTuoYuanView";

    private final int MAX_LINE_COUNT = 10;

    Context mContext;

    ChildClickListener childClickListener;

    ImageView iconImageView;
    TextView titleTextView;
    LinearLayout sanjiaoTopLayout;
    ImageView sanjiaoTopImageView;
    LinearLayout sanjiaoBottomLayout;
    ImageView sanjiaoBottomImageView;

    List<View> lineViewList = null;

    int bgNormalRes = -1;
    int lineCount = 3;

    private int level;

    public AnjianAnmoView(Context context) {
        super(context, null);
    }

    public AnjianAnmoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        String title = "";
        int iconRes = -1;
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.AnjianAnmoView);
            iconRes = typedArray.getResourceId(R.styleable.AnjianAnmoView_icon, -1);
            bgNormalRes = typedArray.getResourceId(R.styleable.AnjianAnmoView_bgnormal, -1);
            title = typedArray.getString(R.styleable.AnjianAnmoView_title);
            lineCount = typedArray.getInteger(R.styleable.AnjianAnmoView_linecount, lineCount);
            LogUtils.i(TAG, "title:" + title);
        }
        View contentView = inflate(getContext(), R.layout.view_anjian_anmo, this);
        iconImageView = contentView.findViewById(R.id.img_xr);
        titleTextView = contentView.findViewById(R.id.text_title);
        sanjiaoTopLayout = contentView.findViewById(R.id.layout_sanjiao_top);
        sanjiaoBottomLayout = contentView.findViewById(R.id.layout_sanjiao_bottom);
        sanjiaoTopImageView = contentView.findViewById(R.id.img_sanjiao_top);
        sanjiaoBottomImageView = contentView.findViewById(R.id.img_sanjiao_bottom);
        lineViewList = new ArrayList<>();

        lineViewList.add(contentView.findViewById(R.id.view_line1));
        lineViewList.add(contentView.findViewById(R.id.view_line2));
        lineViewList.add(contentView.findViewById(R.id.view_line3));
        lineViewList.add(contentView.findViewById(R.id.view_line4));
        lineViewList.add(contentView.findViewById(R.id.view_line5));
        lineViewList.add(contentView.findViewById(R.id.view_line6));
        lineViewList.add(contentView.findViewById(R.id.view_line7));
        lineViewList.add(contentView.findViewById(R.id.view_line8));
        lineViewList.add(contentView.findViewById(R.id.view_line9));
        lineViewList.add(contentView.findViewById(R.id.view_line10));
        if (lineCount >= MAX_LINE_COUNT) {
            lineCount = MAX_LINE_COUNT;
            for (int i = 0; i < MAX_LINE_COUNT; i++) {
                if (i >= lineCount) {
                    lineViewList.get(i).setVisibility(View.GONE);
                } else {
                    lineViewList.get(i).setVisibility(View.INVISIBLE);
                }
            }
        }
        titleTextView.setText(title);
        if (iconRes != -1) {
            iconImageView.setBackground(ContextCompat.getDrawable(mContext, iconRes));
        }
        if (bgNormalRes != -1) {
            setBackground(ContextCompat.getDrawable(mContext, bgNormalRes));
        }
        sanjiaoTopLayout.setOnClickListener(this);
        sanjiaoBottomLayout.setOnClickListener(this);
    }

    /**
     * 设置点击事件
     *
     * @param childClickListener
     */
    public void setChildClickListener(ChildClickListener childClickListener) {
        this.childClickListener = childClickListener;
    }

    /**
     * 设置级别
     *
     * @param level
     */
    public void setLevel(int level) {
        if (level < 0) {
            level = 0;
        }
        this.level = level;
        for (int i = 0; i < MAX_LINE_COUNT; i++) {
            if (i < lineCount) {
                if(i >= level) {
                    lineViewList.get(i).setVisibility(View.INVISIBLE);
                } else {
                    lineViewList.get(i).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 获取级别
     * @return
     */
    public int getLevel() {
        return level;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_sanjiao_top:
                if (childClickListener != null) {
                    childClickListener.minusClick();
                }
                break;
            case R.id.layout_sanjiao_bottom:
                if (childClickListener != null) {
                    childClickListener.plusClick();
                }
                break;
            default:
                break;
        }
    }


    public interface ChildClickListener {

        public void minusClick();

        public void plusClick();

    }


}
