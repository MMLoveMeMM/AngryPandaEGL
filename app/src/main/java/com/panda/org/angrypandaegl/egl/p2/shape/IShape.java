package com.panda.org.angrypandaegl.egl.p2.shape;

/**
 * Created by rd0348 on 2017/10/17 0017.
 */

public interface IShape {
    public void initVertexData();
    public void initShader();
    public void onDrawSelf();
    public void onSurfaceChanged(int width, int height);
}
