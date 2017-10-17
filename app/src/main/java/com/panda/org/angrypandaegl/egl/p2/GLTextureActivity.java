package com.panda.org.angrypandaegl.egl.p2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GLTextureActivity extends Activity {

    private ShapeSurfaceView shapeSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        shapeSurfaceView=new ShapeSurfaceView(this);
        setContentView(shapeSurfaceView);

    }
}
