package ru.fumycat.cp;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MyGLSurfaceView extends GLSurfaceView {
    private MyGLRenderer mRenderer;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    private ScaleGestureDetector mScaleDetector;

    public MyGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        // TODO номарльная камера

        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - previousX;
                float dy = y - previousY;

                // Log.println(Log.INFO, "dx", String.valueOf(dx));
                // Log.println(Log.INFO, "dy", String.valueOf(dy));

                mRenderer.setFi((mRenderer.getFi() + dy * 0.1f) % 360f);
                mRenderer.setTetta((mRenderer.getTetta() + dx * 0.1f) % 360f);

                Log.println(Log.INFO, "tetta/fi",
                        String.valueOf(mRenderer.getTetta()) + " / " + String.valueOf(mRenderer.getFi()));

                requestRender();

        }

        previousX = x;
        previousY = y;

        // mScaleDetector.onTouchEvent(e);

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            // TODO ограничения
            if (detector.getScaleFactor() > 1.) {
                mRenderer.setmZ(mRenderer.getmZ() + 0.03f);
            } else {
                mRenderer.setmZ(mRenderer.getmZ() - 0.03f);
            }
            // Log.println(Log.INFO, "whatever", String.valueOf(mRenderer.getmZ()));
            requestRender();
            invalidate();
            return true;
        }
    }
}
