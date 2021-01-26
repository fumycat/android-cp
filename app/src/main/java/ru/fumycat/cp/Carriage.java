package ru.fumycat.cp;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class Carriage {
    float x, y, z;

    private float[] poleRotateMatrix = new float[16];
    private float[] poleMVPMatrix = new float[16];

    private CuboidTexturesWIP mPlatform;
    private GLCircleCarriage mCarriageBack, mCarriageFront;
    private GLCylinder mCylinder;
    private GLCylinder mWheel1;
    private GLCylinder mWheel2;
    private GLCylinder mWheel3;
    private GLCylinder mWheel4;
    private GLCylinder mStick1;
    private GLCylinder mStick2;

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

        float[] cubeDarkColorData = new float[36 * 4];
        for (int i = 0; i < 36 * 4; i++) {
            if (i % 4 == 2) {
                cubeDarkColorData[i] = 0.4f;
            } else {
                cubeDarkColorData[i] = 0.4f;
            }
        }
        mPlatform = new CuboidTexturesWIP(context,
                x - 7.5f, y-3, z,
                14f, 1f, 7f,
                texture, cubeDarkColorData);
        mCarriageBack = new GLCircleCarriage(context, x, y,z-9.9f, 3, color, texture, ctrl,
                Utils.readStringFromResource(context, R.raw.carriage_circle_vertex),
                Utils.readStringFromResource(context, R.raw.carriage_circle_fragment));
        mCarriageFront = new GLCircleCarriage(context,x, y,z + 9.9f, 3, color, texture, ctrl,
                Utils.readStringFromResource(context, R.raw.carriage_circle_vertex),
                Utils.readStringFromResource(context, R.raw.carriage_circle_fragment));
        mCylinder = new GLCylinder(context, x, y, z/2, 3f,10f, color, texture);

        float black_color[] = { 50 / 255f, 50 / 255f, 50 / 255f, 1.0f };
        float gray_color[] = { 100 / 255f, 100 / 255f, 100 / 255f, 1.0f };

        mWheel1 = new GLCylinder(context, x -3, y -4, z/2, 1f,0.5f, black_color, texture);
        mStick1 = new GLCylinder(context, x -3, y -4, z/2f - 2.5f, 0.2f,5f, gray_color, texture);
        mWheel2 = new GLCylinder(context, x -3, y -4, z/2 - 5, 1f,0.5f, black_color, texture);

        mWheel3 = new GLCylinder(context, x -12, y -4, z/2, 1f,0.5f, black_color, texture);
        mStick2 = new GLCylinder(context, x -12, y -4, z/2 - 2.5f, 0.2f,5f, gray_color, texture);
        mWheel4 = new GLCylinder(context, x -12, y -4, z/2 - 5, 1f,0.5f, black_color, texture);
    }

    public void draw(float[] mvpMatrix, float[] projMatx, float decX, float decY, float decZ, float rX, float rY, float rZ, float angle)
    {
        mPlatform.draw(projMatx, decX, decY, decZ);

        GLES20.glCullFace(GLES20.GL_FRONT);
        mWheel1.draw(mvpMatrix);
        mStick1.draw(mvpMatrix);
        mWheel2.draw(mvpMatrix);
        mWheel3.draw(mvpMatrix);
        mStick2.draw(mvpMatrix);
        mWheel4.draw(mvpMatrix);
        GLES20.glCullFace(GLES20.GL_BACK);

        Matrix.setIdentityM(poleRotateMatrix, 0);
        Matrix.rotateM(poleRotateMatrix, 0, angle, rX, rY, rZ);
        Matrix.multiplyMM(poleMVPMatrix, 0, mvpMatrix, 0, poleRotateMatrix, 0);

        GLES20.glCullFace(GLES20.GL_FRONT);
        mCarriageBack.draw(poleMVPMatrix);
        mCarriageFront.draw(poleMVPMatrix);
        mCylinder.draw(poleMVPMatrix);
        GLES20.glCullFace(GLES20.GL_BACK);
    }
}
