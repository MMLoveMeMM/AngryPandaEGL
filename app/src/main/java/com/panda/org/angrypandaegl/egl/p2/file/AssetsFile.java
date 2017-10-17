package com.panda.org.angrypandaegl.egl.p2.file;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
/**
 * Created by rd0348 on 2017/10/17 0017.
 */

public class AssetsFile{
        private static final String TAG = AssetsFile.class.getSimpleName();

        /**
         * 从assets目录下拷贝整个文件夹，不管是文件夹还是文件都能拷贝
         *
         * @param context
         *            上下文
         * @param rootDirFullPath
         *            文件目录，要拷贝的目录如assets目录下有一个SBClock文件夹：SBClock
         * @param targetDirFullPath
         *            目标文件夹位置如：/sdcrad/SBClock
         */
        public static boolean copyFolderFromAssets(Context context, String rootDirFullPath, String targetDirFullPath) {
            File file = new File(targetDirFullPath);
            if (file.exists()) {
            } else {
                boolean success = file.mkdirs();
                if (!success) {
                    return false;
                }
            }
            try {
                String[] listFiles = context.getAssets().list(rootDirFullPath);// 遍历该目录下的文件和文件夹
                for (String string : listFiles) {// 看起子目录是文件还是文件夹，这里只好用.做区分了
                    if (isFileByName(string)) {// 文件
                        copyFileFromAssets(context, rootDirFullPath + "/" + string, targetDirFullPath + "/" + string);
                    } else {// 文件夹
                        String childRootDirFullPath = rootDirFullPath + "/" + string;
                        String childTargetDirFullPath = targetDirFullPath + "/" + string;
                        new File(childTargetDirFullPath).mkdirs();
                        copyFolderFromAssets(context, childRootDirFullPath, childTargetDirFullPath);
                    }
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        private static boolean isFileByName(String string) {
            if (string.contains(".")) {
                return true;
            }
            return false;
        }

        /**
         * 从assets目录下拷贝文件
         *
         * @param context
         *            上下文
         * @param assetsFilePath
         *            文件的路径名如：hello/cuteowl_dot.png
         * @param targetFileFullPath
         *            目标文件路径如：/sdcard/hello/cuteowl_dot.png
         */
        public static void copyFileFromAssets(Context context, String assetsFilePath, String targetFileFullPath) {

            InputStream assestsFileImputStream;
            try {
                assestsFileImputStream = context.getAssets().open(assetsFilePath);
                FileHelper.copyFile(assestsFileImputStream, targetFileFullPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 从assets目录下获取文本文件内容
         *
         * @param context
         *            上下文
         * @param fileAssetPath
         *            文本文件路径
         * @return
         */
        public static String getFileContentFromAsset(Context context, String fileAssetPath) {
            InputStream ins;
            try {
                ins = context.getAssets().open(fileAssetPath);
                byte[] contentByte = new byte[ins.available()];
                ins.read(contentByte);
                return new String(contentByte);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return "";
        }

        public static InputStream getInputStreamFromAsset(Context context, String fileAssetPath) {

            InputStream ins = null;
            try {
                ins = context.getAssets().open(fileAssetPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ins;
        }

        /**
         * <pre>
         * 保存一张图片
         * </pre>
         *
         * @param bitmap
         * @param path
         * @param needRecycleBitmap
         */
        public static void saveImage(Bitmap bitmap, String path, boolean needRecycleBitmap) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path);// 注意app的sdcard读写权限问题
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);// 压缩成png,100%显示效果
            try {
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (needRecycleBitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
}
