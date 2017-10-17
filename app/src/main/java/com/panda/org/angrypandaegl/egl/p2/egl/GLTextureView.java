package com.panda.org.angrypandaegl.egl.p2.egl;

import android.opengl.GLSurfaceView;
import android.view.TextureView;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;

import java.lang.ref.WeakReference;

/**
 * Created by rd0348 on 2017/10/17 0017.
 * 这个是模仿GLSurfaceView类,等同于GLSurfaceView类
 * 这个类被使用的时候同样是被子类extends GLTextureView去实现具体业务
 */

public class GLTextureView extends TextureView implements TextureView.SurfaceTextureListener{

    /*
    * 渲染模式
    * */
    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;

    private GLThread mGLThread;

    private IRenderer mRenderer;
    private int mRendererMode = RENDERMODE_CONTINUOUSLY;

    public GLTextureView(Context context) {
        super(context);
        init(context);
    }

    public GLTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 类似于GLSurfaceView的setRenderer
     */
    public void setRenderer(IRenderer renderer) {
        mRenderer = renderer;
    }

    /**
     * 类似于GLSurfaceView的setRenderMode
     * 渲染模式，是循环刷新，还是请求的时候刷新
     */
    public void setRenderMode(int mode) {
        mRendererMode = mode;
    }

    /*
    * 仿照GLSurfaceView,预留的,方便在实现类里面保持和GLSurfaceView无差别的操作配置
    * */
    public void setEGLContextClientVersion(int version){

    }

    /**
     * Request that the renderer render a frame. This method is typically used when the render mode has been set to {@link #RENDERMODE_WHEN_DIRTY}, so
     * that frames are only rendered on demand. May be called from any thread. Must not be called before a renderer has been set.
     */
    public void requestRender() {
        if (mRendererMode != RENDERMODE_WHEN_DIRTY) {
            return;
        }
        mGLThread.requestRender();
    }

    private void init(Context context) {
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        WeakReference<SurfaceTexture> surfaceReference =new WeakReference<SurfaceTexture>(surface); // GLSurfaceView采用弱引用,防止内存泄漏
        mGLThread = new GLThread(surfaceReference, mRenderer);// 创建一个线程，作为GL线程,GL渲染和UI显示分开
        mGLThread.setRenderMode(mRendererMode);
        mGLThread.start();//开始渲染
        mGLThread.onSurfaceChanged(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mGLThread.onSurfaceChanged(width, height);
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /*
    * 下面随界面周期变化更新
    * */
    public void onResume() {
        if (mGLThread != null) {
            mGLThread.onResume();
        }
    }

    public void onPause() {
        if (mGLThread != null) {
            mGLThread.onPause();
        }
    }

    public void onDestroy() {
        if (mGLThread != null) {
            mGLThread.onDestroy();
        }
    }
}
