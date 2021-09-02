package com.sn.blackdianqi.file;

import java.io.File;

/**
 * Created by wanghongchuang
 * on 2016/8/25.
 * email:844285775@qq.com
 */
public interface IPathManager {

    /**
     * 创建文件
     *
     * @param filePath
     * @return
     */
    File createFile(String filePath);

    /**
     * 创建多个文件
     *
     * @param filePaths
     * @return
     */
    void createFiles(String... filePaths);

    /**
     * 创建文件
     *
     * @param filePath
     * @param isDirectory 是不是文件夹
     * @return
     */
    File createFile(String filePath, boolean isDirectory);

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    boolean deleteFile(String filePath);

    public boolean deleteFile(String filePath, boolean delFileOnly);

    /**
     * 文件是否存在
     *
     * @param filePath
     * @return
     */
    boolean isExists(String filePath);

    /**
     * 文件是否存在
     *
     * @param filePath
     * @param isDirectory 是不是文件夹
     * @return
     */
    boolean isExists(String filePath, boolean isDirectory);

}
