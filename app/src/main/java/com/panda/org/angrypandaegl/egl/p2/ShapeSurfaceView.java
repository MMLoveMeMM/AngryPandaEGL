package com.panda.org.angrypandaegl.egl.p2;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.panda.org.angrypandaegl.egl.p2.egl.GLTextureView;

/**
 * Created by rd0348 on 2017/10/17 0017.
 */

public class ShapeSurfaceView extends GLTextureView {


    public ShapeSurfaceView(Context context) {
        super(context);

        ShapeRenderer renerer = new ShapeRenderer(context);
        setRenderer(renerer);
        //setRenderMode(RENDERMODE_CONTINUOUSLY);//实时刷新
        setEGLContextClientVersion(2);//GLES20
        setRenderMode(RENDERMODE_WHEN_DIRTY);

    }

}
