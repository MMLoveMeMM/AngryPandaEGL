package com.panda.org.angrypandaegl.egl.p3.egl2;

import com.panda.org.angrypandaegl.egl.p3.surface.GLSurface;

/**
 * Created by rd0348 on 2017/10/18 0018.
 */

public interface ISurface {
    public void addSurface(final GLSurface surface);
    public void removeSurface(final GLSurface surface);
    public void startRender();
    public void stopRender();
    public boolean postRunnable(Runnable runnable);
    public void requestRender();
    public void release();
}
