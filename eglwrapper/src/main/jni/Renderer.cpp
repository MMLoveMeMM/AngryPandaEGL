//
// Created by weiersyuan on 2016/11/30.
//
/*
* 创建思路参照GLSurfaceView
* 实现GLThread,Renderer接口,以及Renderer的实现类
*/
#include "GLUtil.h"
#include "Renderer.h"
#include "Shape.h"
#include "glm/mat4x4.hpp"
#include "glm/ext.hpp"
Shape mShape;
glm::mat4 projection;
glm::mat4 view;
//glm::mat4 module;

const char * vertexShaderCode = "attribute vec4 aPosition;\n"
        "attribute vec4 aColor;\n"
        "varying vec4 vColor;\n"
        "uniform mat4 uMVPMatrix;\n"
        "void main() \n"
        "{\n"
        "    gl_Position = uMVPMatrix * aPosition;\n"
        "    vColor = aColor;\n"
        "}";

const char * fragmentShaderCode = "precision mediump float;\n"
        "varying  vec4 vColor;\n"
        "void main()\n"
        "{\n"
        "    gl_FragColor = vColor;\n"
        "}";


Renderer::Renderer() {
    pthread_mutex_init(&mMutex, NULL);
    pthread_cond_init(&mCondVar, NULL);
    mDisplay = EGL_NO_DISPLAY;
    mSurface = EGL_NO_SURFACE;
    mContext = EGL_NO_CONTEXT;

}

Renderer::~Renderer() {
    pthread_mutex_destroy(&mMutex);
    pthread_cond_destroy(&mCondVar);
}

void Renderer::start() {
    /*
    * 创建GLThread线程
    */
    pthread_create(&mThread, NULL, startRenderThread, this);
}


void Renderer::requestInitEGL(ANativeWindow * pWindow) {
    LOGI(1, "-------requestInitEGL");
    pthread_mutex_lock(&mMutex);//这个是嵌套锁,因为在GLThread里面可能已经枷锁等待中
    mWindow = pWindow;
    mEnumRenderEvent = RE_SURFACE_CHANGED;
    LOGI(1, "-------mEnumRenderEvent=%d", mEnumRenderEvent);
    pthread_mutex_unlock(&mMutex);//必须成对释放,不然GLThread的wait无法解开,下同
    pthread_cond_signal(&mCondVar);//通知GLThread线程取消等待,开始处理
}
void Renderer::requestRenderFrame() {
    pthread_mutex_lock(&mMutex);
    mEnumRenderEvent = RE_DRAW_FRAME;
    pthread_mutex_unlock(&mMutex);
    pthread_cond_signal(&mCondVar);//通知GLThread线程取消等待,开始处理
}

void Renderer::requestDestroy() {
    pthread_mutex_lock(&mMutex);
    mEnumRenderEvent = RE_EXIT;
    pthread_mutex_unlock(&mMutex);
    pthread_cond_signal(&mCondVar);//通知GLThread线程取消等待,开始处理
}

/*
* GLThread线程状态运行处理
*/
void Renderer::run() {
    mISRenderering = true;
    while(mISRenderering) {
        pthread_mutex_lock(&mMutex);
        // 每完成一个事件就wait在这里直到有其他事件唤醒
        pthread_cond_wait(&mCondVar, &mMutex);

        LOGI(1, "-------this mEnumRenderEvent is %d", mEnumRenderEvent);
        switch (mEnumRenderEvent) {
            case RE_SURFACE_CHANGED:
                LOGI(1, "-------case RE_SURFACE_CHANGED");
                mEnumRenderEvent = RE_NONE;
                pthread_mutex_unlock(&mMutex);
                initEGL();
                onSurfaceCreated();
                onSurfaceChanged(mWidth, mHeight);
                break;
            case RE_DRAW_FRAME:
                mEnumRenderEvent = RE_NONE;
                pthread_mutex_unlock(&mMutex);
                // draw
                onDrawFrame();
                eglSwapBuffers(mDisplay, mSurface);//利用双缓冲进行Swap的时候，Display和Surface进行实际意义上的地址交换，来实现eglSwapBuffers的标准
                break;
            case RE_EXIT:
                mEnumRenderEvent = RE_NONE;
                pthread_mutex_unlock(&mMutex);
                releaseEGL();
                mISRenderering = false;
                break;
            default:
                mEnumRenderEvent = RE_NONE;
                pthread_mutex_unlock(&mMutex);
        }


    }
}

void *Renderer::startRenderThread(void * pVoid) {
    Renderer * render = (Renderer*) pVoid;
    /*
    * 启动类似的GLThread线程进行
    */
    render->run();
}

/*
* 初始化egl完后,才可以使用OPENGL ES
*/
void Renderer::initEGL() {
    const EGLint attribs[] = {
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_BLUE_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_RED_SIZE, 8,
            EGL_NONE
    };
    EGLint width, height, format;
    EGLint numConfigs;
    EGLConfig config;
    EGLSurface surface;
    EGLContext context;

    EGLDisplay display = eglGetDisplay(EGL_DEFAULT_DISPLAY);

    eglInitialize(display, 0, 0);

    eglChooseConfig(display, attribs, &config, 1, &numConfigs);

    surface = eglCreateWindowSurface(display, config, mWindow, NULL);
    EGLint attrs[]= {EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE};
    context = eglCreateContext(display, config, NULL, attrs);

    if (eglMakeCurrent(display, surface, surface, context) == EGL_FALSE) {
        LOGI(1, "------EGL-FALSE");
        return ;
    }

    eglQuerySurface(display, surface, EGL_WIDTH, &width);
    eglQuerySurface(display, surface, EGL_HEIGHT, &height);

    mDisplay = display;
    mSurface = surface;
    mContext = context;
    mWidth = width;
    mHeight = height;
    LOGI(1, "width:%d, height:%d", mWidth, mHeight);

}

void Renderer::releaseEGL() {
    if (mDisplay != EGL_NO_DISPLAY) {
        eglMakeCurrent(mDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        if (mContext != EGL_NO_CONTEXT) {
            eglDestroyContext(mDisplay, mContext);
        }
        if (mSurface != EGL_NO_SURFACE) {
            eglDestroySurface(mDisplay, mSurface);
        }
        eglTerminate(mDisplay);
    }

    mDisplay = EGL_NO_DISPLAY;
    mSurface = EGL_NO_SURFACE;
    mContext = EGL_NO_CONTEXT;
}


void Renderer::onSurfaceCreated() {

    mShape.initGL(vertexShaderCode, fragmentShaderCode);
    glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
    glDisable(GL_DEPTH_TEST);
}

void Renderer::onSurfaceChanged(EGLint width, EGLint height) {
    projection = glm::ortho(-1.0f, 1.0f, -(float) height / width, (float) height / width, 5.0f,
                            7.0f);
//    projection = glm::perspective(glm::radians(50.0f), (float)width/height, 5.0f ,7.0f);
    view = glm::lookAt(glm::vec3(0.0f, 0.0f, 6.0f),
                       glm::vec3(0.0f, 0.0f, 0.0f),
                       glm::vec3(0.0f, 1.0f, 0.0f));
    glViewport(0, 0, width, height);
}

void Renderer::onDrawFrame() {

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    /*
    module = glm::rotate(module, angleX, glm::vec3(1,0,0));
    module = glm::rotate(module, angleY, glm::vec3(0,1,0));
     */
    glm::mat4 mvpMatrix = projection * view/* * module*/;
    float *mvp = (float *) glm::value_ptr(mvpMatrix);
    // TODO
    mShape.draw(mvp);
}



