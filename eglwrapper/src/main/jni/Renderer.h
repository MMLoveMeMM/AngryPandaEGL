//
// Created by weiersyuan on 2016/11/30.
//

#ifndef NATIVEGLESVIEWWITHEGL_RENDERDER_H
#define NATIVEGLESVIEWWITHEGL_RENDERDER_H

#include <pthread.h>
#include <android/native_window.h>
#include <EGL/egl.h>
#include <GLES/gl.h>
class Renderer {
public:
    Renderer();
    virtual ~Renderer();

    /*
    * GLThread 线程周期处理函数
    */
    void run();
    void start();
    void stop();

    /*
    * 对应GLSurfaceView回调函数
    */
    void requestInitEGL(ANativeWindow * pWindow);
    void requestRenderFrame();
    void requestDestroy();

    /*
    * 下面即相当于java的Renderer的接口
    */
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
     public void onSurfaceChanged(EGLint width, EGLint height);

     /**
      * 绘制每一帧
      */
     public void onDrawFrame();


private:
    /*
    * 状态机
    */
    enum RenderEvent {
        RE_NONE,
        RE_SURFACE_CREATED,
        RE_SURFACE_CHANGED,
        RE_DRAW_FRAME,
        RE_EXIT
    };

    enum RenderEvent mEnumRenderEvent;
    pthread_t  mThread;
    pthread_mutex_t mMutex;
    pthread_cond_t mCondVar;

    /*
    * 显示窗体,即surface,java传下来的
    */
    ANativeWindow *mWindow;

    EGLDisplay mDisplay;
    EGLSurface mSurface;
    EGLContext mContext;

    /*
    * 创建GLThread线程启动的function
    */
    static void *startRenderThread(void *);

    /*
    * EGL 初始化等
    */
    void initEGL();

    EGLint mWidth;
    EGLint mHeight;

    void releaseEGL();

    /*
    *   GLThread线程是否继续work
    */
    bool mISRenderering;

};





#endif //NATIVEGLESVIEWWITHEGL_RENDERDER_H
