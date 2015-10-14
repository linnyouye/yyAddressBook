package com.lyy.yyaddressbook.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileUtils {

    private static final String TAG = "lyy-FileUtils";
    private static final boolean D = true;

    private static String fileTmp;
    private static String zipTmp;
    private static String photoTmp;

    private FileUtils() {

    }

    public static void init(Context context) {
        String dirPath = null;

        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            dirPath = Environment.getExternalStorageDirectory().getPath();
        } else {
            dirPath = Environment.getDownloadCacheDirectory().getPath();
        }

        dirPath += "/samton/";

        fileTmp = dirPath + "fileTmp";
        zipTmp = dirPath + "zipTmp";
        photoTmp = dirPath + "photoTmp";
        mkDir(fileTmp);
        mkDir(zipTmp);
        mkDir(photoTmp);
    }

    private static void mkDir(String dir) {
        try {
            File file = new File(dir);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean createFile(String name, String content) {

        try {
            FileOutputStream fos = new FileOutputStream(fileTmp + "/" + name);
            fos.write(content.getBytes("utf-8"));
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void deleteAllTmp() {
        deleteDir(fileTmp);
        deleteDir(photoTmp);
        deleteDir(zipTmp);
    }

    public static void deleteDir(String dirPath) {
        File file = new File(dirPath);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File tmp : files) {
                tmp.delete();
            }
        }
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static String getFileTmp() {
        return fileTmp;
    }

    public static String getZipTmp() {
        return zipTmp;
    }

    public static String getPhotoTmp() {
        return photoTmp;
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.toString();
        } else {
            return null;
        }
    }

    public static String getCacheDir(Context context) {

        File appDir = context.getFilesDir();
        String cachePath = appDir.getAbsolutePath() + "/Samton/";
        File cacheDir = new File(cachePath);

        if (!cacheDir.exists()) {
            if (cacheDir.mkdirs()) {
                return cachePath;
            } else {
                return null;
            }
        }

        return cachePath;
    }

    public static String getFileContent(String filePath) {
        if (D)
            Log.i(TAG, "读取文件内容：" + filePath);
        InputStream is = null;
        String result = null;
        try {
            is = new FileInputStream(filePath);

            String line = null;
            result = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                result += line;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            is = null;
        }

        if (D)
            Log.i(TAG, "文件内容：" + result);

        return result;
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[8 * 1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
