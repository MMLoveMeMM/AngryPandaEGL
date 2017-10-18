package com.panda.org.angrypandaegl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.panda.org.angrypandaegl.egl.p1.TextureviewtestActivity;
import com.panda.org.angrypandaegl.egl.p2.GLTextureActivity;
import com.panda.org.angrypandaegl.egl.p3.BitmapActivity;
import com.panda.org.angrypandaegl.egl.p3.PBufferActivity;
import com.panda.org.angrypandaegl.egl.p4.EglActivity;

public class MainActivity extends Activity {

    private Button mStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartBtn=(Button)findViewById(R.id.start);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EglActivity.class));
            }
        });

    }
}
