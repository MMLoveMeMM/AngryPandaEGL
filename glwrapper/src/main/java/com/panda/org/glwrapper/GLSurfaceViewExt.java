package com.panda.org.glwrapper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by rd0348 on 2017/10/18 0018.
 * 其实就相当于把opengl es 的绘制部分放到native层
 * 其他根本不需要变化
 */

public class GLSurfaceViewExt extends GLSurfaceView {
    private GLRendererExt mRenderer;
    private float mPreviousX;
    private float mPreviousY;
    private final float TOUCH_SCALE_FACTOR = 0.0001f;
    public GLSurfaceViewExt(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new GLRendererExt(context);
        this.setRenderer(mRenderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public boolean onTouchEvent(final MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                mRenderer.mAngleX += dy * TOUCH_SCALE_FACTOR;
                mRenderer.mAngleY += dx * TOUCH_SCALE_FACTOR;
                requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}
