package com.panda.org.pbufferwrapper;

import android.graphics.Bitmap;

/**
 * Created by rd0348 on 2017/10/18 0018.
 * 注意 : 这个NDK我是用命令行编译的,所以没有build.gradle文件中配置
 */

public class pbufferwrapper {

    static {
        System.loadLibrary("wrapper");
    }

    /*
    * 相当于GLES20.glReadPixels函数功能
    * */
    public static native void getWrapperGLImage(Bitmap bm);

}
