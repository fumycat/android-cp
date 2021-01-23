package ru.fumycat.cp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    // private OpenGLViewSquare sOpenGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MyGLSurfaceView mMyGLSurfaceView = new MyGLSurfaceView(this);
        setContentView(mMyGLSurfaceView);
    }
}