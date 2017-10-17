package com.panda.org.angrypandaegl.egl.p2.egl;

import java.util.LinkedList;

/**
 * Created by rd0348 on 2017/10/17 0017.
 * 这个是仿照GLSurfaceView类中那个EventQueue
 */

public class EventQueue {

    LinkedList<Runnable> mRunOnDraw = new LinkedList<Runnable>();

    public void runPendings() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run();
        }
    }

    public void addToPending(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.addLast(runnable);
        }
    }
}
