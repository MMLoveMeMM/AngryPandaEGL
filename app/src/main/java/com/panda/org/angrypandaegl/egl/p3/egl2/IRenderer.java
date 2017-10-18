package com.panda.org.angrypandaegl.egl.p3.egl2;

/**
 * Created by rd0348 on 2017/10/18 0018.
 */

public interface IRenderer {
    /**
     * Surface创建好之后
     */
    public void onSurfaceCreated();

    /**
     * 界面大小有更改
     *
     * @param width
     * @param height
     */
    public void onSurfaceChanged(int width, int height);

    /**
     * 绘制每一帧
     */
    public void onDrawFrame();
}
