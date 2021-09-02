package com.sn.blackdianqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sn.blackdianqi.R;


/**
 * 可配置宽高比的自定义LinearLayout
 * 
 * @author mowei May 23, 2015
 */
public class FixedRatioView extends View {

	/**
	 * 默认比例
	 */
	private static final float DEFAULT_RATIO = 0.0f;

	/**
	 * mRatio =  width/height
	 */
	private float mRatio = DEFAULT_RATIO;

	public FixedRatioView(Context context) {
		super(context);
	}

	public FixedRatioView(Context context, AttributeSet attrs) {
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
			originalWidth = (int) (originalHeight * mRatio);
		}

		super.onMeasure(
				MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(aspectHeight, MeasureSpec.EXACTLY));
	}

}
