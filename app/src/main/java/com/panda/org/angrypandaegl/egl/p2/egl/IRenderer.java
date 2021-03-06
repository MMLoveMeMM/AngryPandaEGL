package com.panda.org.angrypandaegl.egl.p2.egl;

/**
 * Created by rd0348 on 2017/10/17 0017.
 * 这个类是模仿GLSurfaceView类中的Renderer接口类
 * 可以直接把GLSurfaceView那个接口抄过来用
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
