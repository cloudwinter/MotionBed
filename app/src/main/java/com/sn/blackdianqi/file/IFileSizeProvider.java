package com.sn.blackdianqi.file;

import java.text.DecimalFormat;

/**
 * [文件大小提供者]
 * Created by wanghongchuang
 * on 2016/8/25.
 * email:844285775@qq.com
 */
public interface IFileSizeProvider {

    /**
     * 获取大小
     *
     * @param filePath
     * @return
     */
    public long getSize(String filePath);

    public String getCacheSize(Long... sizes);

    public String getSizeStr(String filePath);

    public String getSizeByFormat(String filePath, DecimalFormat decimalFormat);


}
