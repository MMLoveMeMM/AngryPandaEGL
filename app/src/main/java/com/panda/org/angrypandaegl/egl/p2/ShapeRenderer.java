package com.panda.org.angrypandaegl.egl.p2;
import android.content.Context;
import android.opengl.GLES20;

import com.panda.org.angrypandaegl.egl.p2.egl.IRenderer;
import com.panda.org.angrypandaegl.egl.p2.shape.Triangle;

/**
 * Created by rd0348 on 2017/10/17 0017.
 */

public class ShapeRenderer implements IRenderer {

    private static final String TAG = "GLESRendererImpl";
    private Context mContext;
    private Triangle mTriangle;

    public ShapeRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated() {
        //LogHelper.d(TAG, //LogHelper.getThreadName());
        // 设置屏幕背景色RGBA
        GLES20.glClearColor(1, 0, 0, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        mTriangle = new Triangle(mContext);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        //LogHelper.d(TAG, //LogHelper.getThreadName());
        // 设置视窗大小及位置
        GLES20.glViewport(0, 0, width, height);
        mTriangle.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {// 绘制
        // 清除深度缓冲与颜色缓冲（就是清除缓存）
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        mTriangle.onDrawSelf();
    }

}
