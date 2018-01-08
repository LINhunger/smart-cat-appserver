package com.qg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by hunger on 2017/8/4.
 */
public class FileUtil {


    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /***
     *  过滤后缀名，遍历文件夹文件
     * @param path
     * @param sufixStr
     *            :后缀名
     * @return
     */
    public static File[] getFilesByPathAndSuffix(File path,
                                                 final String sufixStr) {
        File[] fileArr = path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if ((sufixStr.isEmpty() || (dir.isDirectory() && name
                        .endsWith(sufixStr)))) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        return fileArr;

    }

    /**
     * 后缀名
     * 过滤后缀名遍历文件夹文件
     *
     * @param pathStr
     * @param sufixStr
     * @return
     */
    public static File[] getFilesByPathAndSuffix(String pathStr,
                                                 final String sufixStr) {
        File path = new File(pathStr);
        return getFilesByPathAndSuffix(path, sufixStr);
    }

    /**
     * 创建文件
     *
     * @param destFileName
     * @return
     */
    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {
            LOGGER.error("创建单个文件" + destFileName + "失败，目标文件已存在！");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            LOGGER.error("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return false;
        }
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            LOGGER.info("目标文件所在目录不存在，准备创建它！");
            if (!file.getParentFile().mkdirs()) {
                LOGGER.error("创建目标文件所在目录失败！");
                return false;
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                LOGGER.info("创建单个文件" + destFileName + "成功！");
                return true;
            } else {
                LOGGER.error("创建单个文件" + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            LOGGER.error("创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
        }
    }


    /**
     * 创建文件夹
     *
     * @param destDirName
     * @return
     */
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            LOGGER.error("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            LOGGER.info("创建目录" + destDirName + "成功！");
            return true;
        } else {
            LOGGER.error("创建目录" + destDirName + "失败！");
            return false;
        }
    }


    public static boolean clearDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录
            for (int i = 0; i < children.length; i++) {
                boolean success = clearDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        } else {
            return dir.delete();
        }
        return false;
    }

    /**
     * 删除目录下全部文件
     *
     * @param root
     */
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }


    /**
     * 修改文件名
     *
     * @param toBeRenamed
     * @param newFile
     */
    public static void renameFile(File toBeRenamed, File newFile) {

        //检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            LOGGER.error("File does not exist: " + toBeRenamed.getName());
            return;
        }
        //修改文件名
        if (toBeRenamed.renameTo(newFile)) {
//            LOGGER.info("File has been renamed.");
        } else {
            LOGGER.error("Error renaming file");
        }
    }


    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
}
