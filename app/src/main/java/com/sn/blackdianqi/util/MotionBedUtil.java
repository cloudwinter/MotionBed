package com.sn.blackdianqi.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by xiayundong on 2022/1/11.
 */
public class MotionBedUtil {

    public static int[] splitArray(int[] oriArray, int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex > oriArray.length) {
            throw new IndexOutOfBoundsException();
        }
        int[] newArray = new int[endIndex - startIndex + 1];
        for (int i = 0; i < oriArray.length; i++) {
            if (i >= startIndex && i <= endIndex) {
                newArray[i - startIndex] = oriArray[i];
            }
        }
        return newArray;
    }


    /**
     * dp转换为px
     * @param context
     * @param dp
     * @return
     */
    public static int dpToPx(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
        return Math.round(px);
    }
}
