package com.panda.org.angrypandaegl.egl.p1;

import android.app.Activity;
import android.os.Bundle;
import java.util.concurrent.atomic.AtomicBoolean;

import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
public class TextureviewtestActivity extends Activity implements TextureView.SurfaceTextureListener{

    private TextureView mTextureView;
    private Thread mProducerThread = null;
    private GLRendererImpl mRenderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//隐藏虚拟按键，即navigator bar
        setContentView(mTextureView);

        mRenderer = new GLRendererImpl(this);
    }
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
                                          int height) {
        // TODO Auto-generated method stub
        mRenderer.setViewport(width, height);
        mProducerThread = new GLProducerThread(surface, mRenderer, new AtomicBoolean(true));
        mProducerThread.start();
    }
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        // TODO Auto-generated method stub
        mProducerThread = null;
        return true;
    }
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                            int height) {
        // TODO Auto-generated method stub
        mRenderer.resize(width, height);

    }
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // TODO Auto-generated method stub

    }
}
