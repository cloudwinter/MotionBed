package com.sn.blackdianqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.sn.blackdianqi.R;


/**
 * 可配置宽高比的自定义Layout
 * 
 * @author mowei May 23, 2015
 */
public class FixedRatioRelativeLayout extends RelativeLayout {

	private static final float DEFAULT_RATIO = 1.0f;
	
	/**
	 * mRatio = height / width
	 */
	private float mRatio = DEFAULT_RATIO;

	public FixedRatioRelativeLayout(Context context) {
		super(context);
	}

	public FixedRatioRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		Init(context, attrs);
	}

	public FixedRatioRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		Init(context, attrs);
	}

	private void Init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FixedRatioRelativeLayout);
		mRatio = a.getFloat(R.styleable.FixedRatioRelativeLayout_ratio, DEFAULT_RATIO);
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
		
		// mRatio大于0，高度需要计算，小于0保持原大小
		if (mRatio > 0) {
			aspectHeight = (int) (originalWidth * mRatio);
		}

		super.onMeasure(
				MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(aspectHeight, MeasureSpec.EXACTLY));
	}
}