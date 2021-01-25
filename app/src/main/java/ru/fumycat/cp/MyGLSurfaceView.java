package ru.fumycat.cp;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MyGLSurfaceView extends GLSurfaceView {
    private MyGLRenderer mRenderer;

    private int scale_waiter = 0;
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

        mScaleDetector.onTouchEvent(e);

        float x = e.getX();
        float y = e.getY();

        if (e.getPointerCount() == 1 && (++scale_waiter) > 1) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    float dx = x - previousX;
                    float dy = y - previousY;

                    // Log.println(Log.INFO, "dx", String.valueOf(dx));
                    // Log.println(Log.INFO, "dy", String.valueOf(dy));

                    mRenderer.setFi((mRenderer.getFi() - dx * 0.1f) % 360f);
                    mRenderer.setTetta((mRenderer.getTetta() + dy * 0.1f) % 360f);

                    Log.println(Log.INFO, "tetta/fi",
                            String.valueOf(mRenderer.getTetta()) + " / " + String.valueOf(mRenderer.getFi()));

                    requestRender();
            }
        }
        else if (e.getPointerCount() > 1)
        {
            scale_waiter = 0;
        }

        previousX = x;
        previousY = y;

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            if (detector.getScaleFactor() > 1.) {
                mRenderer.setRadius(mRenderer.getRadius() + 0.2f);
            } else {
                mRenderer.setRadius(mRenderer.getRadius() - 0.2f);
            }

            requestRender();
            invalidate();
            return true;
        }
    }
}
