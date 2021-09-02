package com.sn.blackdianqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sn.blackdianqi.R;


/**
 * 可配置宽高比的自定义LinearLayout
 * 
 * @author mowei May 23, 2015
 */
public class FixedRatioLinearLayout extends LinearLayout {

	/**
	 * 默认比例
	 */
	private static final float DEFAULT_RATIO = 0.0f;

	/**
	 * mRatio = height / width
	 */
	private float mRatio = DEFAULT_RATIO;

	public FixedRatioLinearLayout(Context context) {
		super(context);
	}

	public FixedRatioLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		Init(context, attrs);
	}

	private void Init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FixedRatioRelativeLayout);
		mRatio = a.getFloat(R.styleable.FixedRatioRelativeLayout_ratio,
				DEFAULT_RATIO);
		a.recycle();
	}

	/**
	 * 重新设置宽高比例，并且刷新视图
	 * 
	 * @param ratio
	 */
	public void resetRatio(float ratio) {
		this.mRatio = ratio;
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
		int originalHeight = MeasureSpec.getSize(heightMeasureSpec);
		int aspectHeight = originalHeight;

		// mRatio大于0，高度需要计算，小于等于0保持原大小
		if (mRatio > 0) {
			aspectHeight = (int) (originalWidth * mRatio);
		}

		super.onMeasure(
				MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(aspectHeight, MeasureSpec.EXACTLY));
	}

}
