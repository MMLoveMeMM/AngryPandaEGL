package com.panda.org.angrypandaegl.egl.p3.egl2;

import com.panda.org.angrypandaegl.egl.p3.surface.GLSurface;

/**
 * Created by rd0348 on 2017/10/18 0018.
 * 这个类做个代理
 */

public class GLObjectView implements ISurface{

    private GLThread glThread;
    private PBufferRenderer pBufferRenderer;

    public GLObjectView(){
        glThread=new GLThread(pBufferRenderer);
    }

    public GLObjectView(GLSurface surface){
        glThread=new GLThread(pBufferRenderer);
        glThread.addSurface(surface);
    }

    public void addSurface(final GLSurface surface){
        glThread.addSurface(surface);
    }
    public void removeSurface(final GLSurface surface){
        glThread.removeSurface(surface);
    }
    public void startRender(){
        glThread.startRender();
    }
    public void stopRender(){
        glThread.stopRender();
    }
    public boolean postRunnable(Runnable runnable){
        glThread.postRunnable(runnable);
        return true;
    }
    public void requestRender(){
        glThread.requestRender();
    }
    public void release(){
        glThread.release();
    }

}
