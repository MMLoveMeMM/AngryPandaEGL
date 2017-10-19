package com.panda.org.eglwrapper;

import android.content.Context;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by rd0348 on 2017/10/19 0019.
 * 这个module还是非常好
 * 彻底的模仿了前面java层GLSurfaceView类处理机制
 * 实现的GLThread比较好,体现了java层的处理方式,不过EventQueue未实现
 * 理解比较透彻.
 * 从整体上: 需要实现类似GLSurfaceView,GLThread线程类,在线程中实现EGL初始化等操作
 * ,以及对应Renderer接口(OPENGL es),注意: OPENGL es的使用必须要在EGL初始化完成之后才能够使用,主要是要获得EGL初始化后的Context上下报文
 * 本人仅仅修改了一些函数名和添加注释,方便更好的立即
 */

public class EglSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public EglSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        nativeRequestRender();
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setRender();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        nativeSurfaceChanged(surfaceHolder.getSurface());
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        nativeSurfaceDestroyed();
    }

    private void setRender() {
        nativeStartRender();
    }

    private static native void nativeSurfaceChanged(Surface surface);// 传入的Surface给AWindowNative用于显示

    private static native void nativeSurfaceDestroyed();

    private static native void nativeStartRender();

    private static native void nativeRequestRender();
    static {
        System.loadLibrary("NativeWithEGL");
    }
}