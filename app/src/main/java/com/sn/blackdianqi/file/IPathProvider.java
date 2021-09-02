package com.sn.blackdianqi.file;

/**
 * Created by wanghongchuang
 * on 2016/8/25.
 * email:844285775@qq.com
 */
public interface IPathProvider {

    //根目录地址
    String rootPath = "/sdcard/sn.blackdianqi";

    /**
     * 获取根目录
     *
     * @return
     */
    String getRootPath();

    /**
     * 获取图片目录
     *
     * @return
     */
    String getImagePath();


    /**
     * 获取图片临时地址
     *
     * @return
     */
    String getImageTempPath();


    /**
     * 获取图片缓存地址
     *
     * @return
     */
    String getImageCachePath();

    /**
     * 获取录音缓存目录
     *
     * @return
     */
    String getAudioPath();

    /**
     * 获取录音文件缓存目录
     *
     * @return
     */
    String getAudioFilePath(String fileName);

    /**
     * 获取PPT图片目录
     *
     * @return
     */
    String getPPTUploadPath();

    /**
     * 获取PPT图片上传地址
     *
     * @return
     */
    String getPPTUploadFilePath(String fileName);


    /**
     * 获取图片临时地址
     *
     * @return
     */
    String getImageTempFilePath(String fileName);

    /**
     * 获取缓存目录
     *
     * @return
     */
    public String getCacheRoot();

    /**
     * 获取缓存列表
     *
     * @return
     */
    public String[] getCacheList();

}
