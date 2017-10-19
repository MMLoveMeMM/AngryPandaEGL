package com.panda.org.glwrapper;

import android.content.Context;
import android.opengl.GLSurfaceView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by rd0348 on 2017/10/19 0019.
 */

public class GLRendererExt implements GLSurfaceView.Renderer {

    public float mAngleX;
    public float mAngleY;
    private Context mContext;

    public GLRendererExt(Context context) {
        super();
        mContext = context;
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vetexShaderStr = LoadShaderStr(mContext, R.raw.vshader);
        String fragmentShaderStr = LoadShaderStr(mContext, R.raw.fshader);
        nativeOnSurfaceCreated(vetexShaderStr, fragmentShaderStr);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        nativeOnDrawFrame(mAngleX, mAngleY);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        nativeOnSurfaceChanged(width, height);
    }

    private String LoadShaderStr(Context context, int resId) {
        StringBuffer strBuf = new StringBuffer();
        try {
            InputStream inputStream = context.getResources().openRawResource(resId);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String read = in.readLine();
            while (read != null) {
                strBuf.append(read + "\n");
                read = in.readLine();
            }
            strBuf.deleteCharAt(strBuf.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strBuf.toString();
    }

    static {
        System.loadLibrary("NativeGles");
    }

    public static native void nativeOnSurfaceCreated(String vertexShaderCode, String fragmentShaderCode);//这个传递是不必要的
    private static native void nativeOnDrawFrame(float angleX, float angleY); // 接受touch事件
    private static native void nativeOnSurfaceChanged(int width, int height);

}
