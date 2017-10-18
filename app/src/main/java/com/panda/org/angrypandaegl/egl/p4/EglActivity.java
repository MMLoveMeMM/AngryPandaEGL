package com.panda.org.angrypandaegl.egl.p4;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.panda.org.angrypandaegl.R;
import com.panda.org.pbufferwrapper.EglHelper;
import com.panda.org.pbufferwrapper.pbufferwrapper;

public class EglActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private Button button;
    private LinearLayout dispalyLayout;
    private ImageView displayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl);

        dispalyLayout = (LinearLayout) findViewById(R.id.displayLayout);
        displayView = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.show);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EglHelper.enableEGL();
                pbufferwrapper.getWrapperGLImage(bitmap);
                displayView.setImageBitmap(bitmap);

            }
        });

        EglHelper.initEGL();
        bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);

        Bitmap displayBitmap = ((BitmapDrawable)displayView.getDrawable()).getBitmap();
        Canvas canvas = new Canvas(bitmap);
        for(int i = 0; i < bitmap.getHeight(); i += displayBitmap.getHeight()) {
            for(int j = 0; j < bitmap.getWidth(); j += displayBitmap.getWidth()) {
                canvas.drawBitmap(displayBitmap, j, i, null);
            }
        }

    }
}
