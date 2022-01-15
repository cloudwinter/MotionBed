package com.sn.blackdianqi.util;

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
}
