package com.panda.org.angrypandaegl.egl.p2.egl;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;

import java.lang.ref.WeakReference;

/**
 * Created by rd0348 on 2017/10/17 0017.
 * 这个类是模仿GLSurfaceView类中的GLThread类
 */

public class GLThread extends Thread{

    private static final String TAG = "GLThread";
    private SurfaceTexture mSurfaceTexture;
    private IRenderer mRenderer;

    private EGL10 mEgl;
    private EGLDisplay mEglDisplay = EGL10.EGL_NO_DISPLAY;// 显示设备
    private EGLSurface mEglSurface = EGL10.EGL_NO_SURFACE;
    private EGLContext mEglContext = EGL10.EGL_NO_CONTEXT;
    //    private GL mGL;
    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    private static final int EGL_OPENGL_ES2_BIT = 4;

    private boolean mNeedRenderring = true;
    private Object LOCK = new Object();

    private boolean mIsPaused = false;

    private EventQueue mEventQueue=new EventQueue();

    /*
    * 构造里面和GLSurfaceView中的GLThread保持一致,采用弱引用传值
    * */
    public GLThread(WeakReference<SurfaceTexture> surface/*SurfaceTexture surface*/, IRenderer renderer) {
        mSurfaceTexture = surface.get();
        mRenderer = renderer;
    }

    @Override
    public void run() {

        initGLESContext();

        mRenderer.onSurfaceCreated();
        while (mNeedRenderring) {
            mEventQueue.runPendings();
            mRenderer.onDrawFrame();// 绘制
            // 一帧完成之后，调用eglSwapBuffers(EGLDisplay dpy, EGLContext ctx)来显示
            mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);// 这一句不能少啊，少了就GG了，一片空白
            // 1.凡是onPause都要停止，2.如果是onResume的状态，如果是循环刷新则会继续下一次循环，否则会暂停等待调用requestRender()
            if (mIsPaused) {
                pauseWhile();//这个地方会block,直到requestRender方法被调用
            } else if (mRendererMode == GLTextureView.RENDERMODE_WHEN_DIRTY) {
                pauseWhile();
            }
        }
        destoryGLESContext();
    }

    /*
    * 步骤如下 :
    * <1> : 获取显示设备
    * <2> : 获取配置特性列表attributes
    * <3> : 根据上面attributes获取configs
    * <4> : 创建eglContext
    * <5> : 创建surface管理[显示]
    * <6> : 设置为当前渲染环境
    * 初始化egl完毕
    * */
    private void initGLESContext() {

        mEgl = (EGL10) EGLContext.getEGL();
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);// 获取显示设备
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetdisplay failed : " + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, version)) {// //version中存放EGL 版本号，int[0]为主版本号，int[1]为子版本号
            throw new RuntimeException("eglInitialize failed : " + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        // 构造需要的特性列表
        int[] configAttribs = { //
                EGL10.EGL_BUFFER_SIZE, 32,//
                EGL10.EGL_ALPHA_SIZE, 8, // 指定Alpha大小，以下四项实际上指定了像素格式
                EGL10.EGL_BLUE_SIZE, 8, // 指定B大小
                EGL10.EGL_GREEN_SIZE, 8,// 指定G大小
                EGL10.EGL_RED_SIZE, 8,// 指定RGB中的R大小（bits）
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,// 指定渲染api类别,这里或者是硬编码的4，或者是EGL14.EGL_OPENGL_ES2_BIT
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT, EGL10.EGL_NONE// 总是以EGL10.EGL_NONE结尾
        };

        int[] numConfigs = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        // eglChooseConfig(display, attributes, configs, num, configNum);
        // 用于获取满足attributes的所有config，参数1、2其意明显，参数3用于存放输出的configs，参数4指定最多输出多少个config，参数5由EGL系统写入，表明满足attributes的config一共有多少个
        if (!mEgl.eglChooseConfig(mEglDisplay, configAttribs, configs, 1, numConfigs)) {// 获取所有满足attributes的configs,并选择一个
            throw new RuntimeException("eglChooseConfig failed : " + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        int[] contextAttribs = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};// attrib_list,目前可用属性只有EGL_CONTEXT_CLIENT_VERSION, 1代表OpenGL ES 1.x,
        // 2代表2.0。同样在Android4.2之前，没有EGL_CONTEXT_CLIENT_VERSION这个属性，只能使用硬编码0x3098代替
        mEglContext = mEgl.eglCreateContext(mEglDisplay, configs[0], EGL10.EGL_NO_CONTEXT, // share_context,是否有context共享，共享的contxt之间亦共享所有数据。EGL_NO_CONTEXT代表不共享
                contextAttribs);// 创建context
        mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, configs[0], mSurfaceTexture,// 负责对Android Surface的管理
                null// Surface属性
        );// 获取显存，create a new EGL window surface
        if (mEglSurface == EGL10.EGL_NO_SURFACE || mEglContext == EGL10.EGL_NO_CONTEXT) {
            int error = mEgl.eglGetError();
            if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                throw new RuntimeException("eglCreateWindowSurface returned  EGL_BAD_NATIVE_WINDOW. ");
            }
            throw new RuntimeException("eglCreateWindowSurface failed : " + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }

        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {// 设置为当前的渲染环境
            throw new RuntimeException("eglMakeCurrent failed : " + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
//        mGL = mEglContext.getGL();
    }

    private void destoryGLESContext() {
        mEgl.eglDestroyContext(mEglDisplay, mEglContext);
        mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
        mEglContext = EGL10.EGL_NO_CONTEXT;
        mEglSurface = EGL10.EGL_NO_SURFACE;
    }

    /*
    * 周期决定是否继续渲染,以及渲染模式
    * */
    private void pauseWhile() {
        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSurfaceChanged(final int width, final int height) {
        mEventQueue.addToPending(new Runnable() {// 在GL线程中执行
            @Override
            public void run() {
                mRenderer.onSurfaceChanged(width, height);
            }
        });
    }

    private int mRendererMode = GLTextureView.RENDERMODE_CONTINUOUSLY;

    public void setRenderMode(int mode) {
        mRendererMode = mode;
    }

    public void requestRender() {
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    public void onPause() {
        mIsPaused = true;
    }

    public void onResume() {
        mIsPaused = false;
        requestRender();
    }

    public void onDestroy() {
        mNeedRenderring = false;
        destoryGLESContext();
    }
}
