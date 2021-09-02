package com.sn.blackdianqi.file;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by wanghongchuang
 * on 2016/8/8.
 * email:844285775@qq.com
 */
public class FileUtils implements IPathProvider, IPathManager, IFileSizeProvider {

    private static FileUtils instance;

    public static FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }
        return instance;
    }

    @Override
    public String getRootPath() {
        return rootPath;
    }

    @Override
    public String getImagePath() {
        return rootPath + "/img";
    }

    @Override
    public String getImageTempPath() {
        return getCacheRoot() + "/temp";
    }

    @Override
    public String getImageCachePath() {
        return getCacheRoot() + "/img";
    }

    @Override
    public String getAudioPath() {
        return getCacheRoot() + "/audio";
    }

    @Override
    public String getAudioFilePath(String fileName) {
        return getAudioPath() + "/" + fileName;
    }

    @Override
    public String getPPTUploadPath() {
        return getCacheRoot() + "/ppt/upload";
    }

    @Override
    public String getPPTUploadFilePath(String fileName) {
        return getPPTUploadPath() + "/" + fileName;
    }

    @Override
    public String getImageTempFilePath(String fileName) {
        return getImageTempPath() + "/" + fileName + ".jpg";
    }

    @Override
    public String getCacheRoot() {
        return rootPath + "/cache";
    }

    @Override
    public String[] getCacheList() {
        return new String[]{getImageTempPath(), getImageCachePath(), getPPTUploadPath(), getImageTempPath()};
    }


    @Override
    public File createFile(String filePath) {
        return createFile(filePath, true);
    }

    @Override
    public void createFiles(String... filePaths) {
        for (String filpath : filePaths) {
            createFile(filpath);
        }
    }

    @Override
    public File createFile(String filePath, boolean isDirectory) {
        if (TextUtils.isEmpty(filePath)) {
            throw new NullPointerException("filePath can not be null .. ");
        }
        File file = new File(filePath);
        if (isExists(filePath, isDirectory)) {
            return file;
        }
        if (!isDirectory) {
            createFile(file.getParent());
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file.getParentFile().exists()) {
            file.mkdir();
        } else {
            file.mkdirs();
        }

        return file;
    }

    @Override
    public boolean deleteFile(String filePath) {
        return deleteFile(filePath, false);
    }

    @Override
    public boolean deleteFile(String filePath, boolean delFileOnly) {
        if (TextUtils.isEmpty(filePath)) {
            return true;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return true;
        }
        if (!file.isDirectory()) {
            return file.delete();
        }
        File[] files = file.listFiles();
        for (File itemFile : files) {
            if (!delFileOnly) { //全删
                itemFile.delete();
            } else {//只删文件
                if (itemFile.isFile()) {
                    itemFile.delete();
                } else if (itemFile.isDirectory()) {
                    deleteFile(itemFile.getAbsolutePath(), true);
                }
            }
        }

        return true;
    }

    @Override
    public boolean isExists(String filePath) {
        return isExists(filePath, true);
    }

    @Override
    public boolean isExists(String filePath, boolean isDirectory) {
        if (TextUtils.isEmpty(filePath)) {
            throw new NullPointerException("filePath can not be null .. ");
        }
        File file = new File(filePath);
        if (isDirectory) {
            return file.exists() && file.isDirectory();
        } else {
            return file.exists() && !file.isDirectory();
        }
    }

    @Override
    public long getSize(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return 0;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return 0;
        }
        if (!file.isDirectory()) {
            return file.length();
        }
        long dirSize = 0;
        File[] files = file.listFiles();
        for (File item : files) {
            if (item.isFile()) {
                dirSize += item.length();
            } else if (item.isDirectory()) {
                dirSize += item.length();
                dirSize += getSize(item.getAbsolutePath()); // 递归
            }
        }
        return dirSize;
    }

    @Override
    public String getCacheSize(Long... sizes) {
        double size = getSize(getCacheRoot());
        if (sizes != null) {
            for (long item : sizes) {
                size += item;
            }
        }
        size = (size + 0.0) / (1024 * 1024) - 0.2;//0.2MB为文件夹大小，是不允许删除的
        if (size < 0) {
            return "0";
        }
        return String.format("%.2f", size);
    }

    @Override
    public String getSizeStr(String filePath) {
        double size = getSize(filePath);
        size = (size + 0.0) / (1024 * 1024);
        return String.format("%.2f", Math.abs(size));
    }

    @Override
    public String getSizeByFormat(String filePath, DecimalFormat decimalFormat) {
        double size = getSize(filePath);
        size = (size + 0.0) / (1024 * 1024);
        return decimalFormat.format(size);
    }
}
