package ru.fumycat.cp;

import android.content.Context;
import android.opengl.GLES20;

public class Carriage {
    float x, y, z;

    private GLCircleCarriage mCarriageBack, mCarriageFront;
    private GLCylinder mCylinder;

    float color[] = { 226 / 255f, 176 / 255f, 0 / 255f, 1.0f };

    Carriage(Context context, float x, float y, float z, int texture)
    {
        GLCircleCarriage.DrawCtrl ctrl = new GLCircleCarriage.DrawCtrl(){
            @Override
            public void shaders_free() {
            }

            @Override
            public void shaders_init(int mProgramHandle) {
                float color[] = { 226 / 255f, 176 / 255f, 0 / 255f, 1.0f };
                GLES20.glUniform4fv(GLES20.glGetUniformLocation(mProgramHandle, "u_Color"), 1, color, 0);
            }
        };
        mCarriageBack = new GLCircleCarriage(context, x, y,z-3.9f, 1, color, texture, ctrl,
                Utils.readStringFromResource(context, R.raw.carriage_circle_vertex),
                Utils.readStringFromResource(context, R.raw.carriage_circle_fragment));
        mCarriageFront = new GLCircleCarriage(context,x, y,z + 3.9f, 1, color, texture, ctrl,
                Utils.readStringFromResource(context, R.raw.carriage_circle_vertex),
                Utils.readStringFromResource(context, R.raw.carriage_circle_fragment));
        mCylinder = new GLCylinder(context, x, y, z/2, 1f,4f, color, texture);
    }

    public void draw(float[] mvpMatrix)
    {
        mCarriageBack.draw(mvpMatrix);
        mCarriageFront.draw(mvpMatrix);
        mCylinder.draw(mvpMatrix);
    }
}
