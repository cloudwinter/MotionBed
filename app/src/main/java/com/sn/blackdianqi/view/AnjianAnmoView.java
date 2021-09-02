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

import androidx.core.content.ContextCompat;

/**
 * 椭圆按键
 */
public class AnjianAnmoView extends RelativeLayout implements View.OnClickListener {

    public static final String TAG = "AnjianChangTuoYuanView";

    Context mContext;

    ChildClickListener childClickListener;

    ImageView iconImageView;
    TextView titleTextView;
    LinearLayout sanjiaoTopLayout;
    ImageView sanjiaoTopImageView;
    LinearLayout sanjiaoBottomLayout;
    ImageView sanjiaoBottomImageView;
    View line1View;
    View line2View;
    View line3View;
    View line4View;

    int bgNormalRes = -1;
    int lineCount = 3;

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
        line1View = contentView.findViewById(R.id.view_line1);
        line2View = contentView.findViewById(R.id.view_line2);
        line3View = contentView.findViewById(R.id.view_line3);
        line4View = contentView.findViewById(R.id.view_line4);
        if (lineCount == 4) {
            line4View.setVisibility(INVISIBLE);
        } else {
            line4View.setVisibility(GONE);
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
     * @param childClickListener
     */
    public void setChildClickListener(ChildClickListener childClickListener) {
        this.childClickListener = childClickListener;
    }

    /**
     * 设置级别
     * @param level
     */
    public void setLevel(int level) {
        switch (level) {
            case 0:
                line1View.setVisibility(INVISIBLE);
                line2View.setVisibility(INVISIBLE);
                line3View.setVisibility(INVISIBLE);
                if (lineCount == 4) {
                    line4View.setVisibility(INVISIBLE);
                } else {
                    line4View.setVisibility(GONE);
                }
                break;
            case 1:
                line1View.setVisibility(VISIBLE);
                line2View.setVisibility(INVISIBLE);
                line3View.setVisibility(INVISIBLE);
                if (lineCount == 4) {
                    line4View.setVisibility(INVISIBLE);
                } else {
                    line4View.setVisibility(GONE);
                }
                break;
            case 2:
                line1View.setVisibility(VISIBLE);
                line2View.setVisibility(VISIBLE);
                line3View.setVisibility(INVISIBLE);
                if (lineCount == 4) {
                    line4View.setVisibility(INVISIBLE);
                } else {
                    line4View.setVisibility(GONE);
                }
                break;
            case 3:
                line1View.setVisibility(VISIBLE);
                line2View.setVisibility(VISIBLE);
                line3View.setVisibility(VISIBLE);
                if (lineCount == 4) {
                    line4View.setVisibility(INVISIBLE);
                } else {
                    line4View.setVisibility(GONE);
                }
                break;
            case 4:
                line1View.setVisibility(VISIBLE);
                line2View.setVisibility(VISIBLE);
                line3View.setVisibility(VISIBLE);
                line4View.setVisibility(VISIBLE);
                break;
        }
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
