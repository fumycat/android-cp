package ru.fumycat.cp;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Building mBuilding;
    private GLCircleCarriage mCarriageBack, mCarriageFront;
    private GLCylinder mCylinder;

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private float[] rotationMatrix = new float[16];

    public volatile float mAngle;

    public float getmX() {
        return mX;
    }

    public void setmX(float mX) {
        this.mX = mX;
    }

    public float getmY() {
        return mY;
    }

    public void setmY(float mY) {
        this.mY = mY;
    }

    public volatile float mX;
    public volatile float mY;
    public volatile float mZ = -5;

    public volatile float fi = .01f;
    public volatile float tetta = .01f;

    public float getFi() {
        return fi;
    }

    public void setFi(float fi) {
        this.fi = fi;
    }

    public float getTetta() {
        return tetta;
    }

    public void setTetta(float tetta) {
        this.tetta = tetta;
    }

    public float getmZ() {
        return mZ;
    }

    public void setmZ(float mZ) {
        this.mZ = mZ;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mBuilding = new Building();
        mCarriageBack = new GLCircleCarriage(0, 0,-3.9f);
        mCarriageFront = new GLCircleCarriage(0, 0,3.9f);
        mCylinder = new GLCylinder(0, 0,0, 4f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // когда переворачиваем экран
        GLES20.glViewport(0, 0, width, height);
        // GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 80);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        float[] scratch = new float[16];

        // Set the camera position (View matrix)
        double tettaRad = Math.toRadians(tetta);
        double fiRad = Math.toRadians(fi);
        float decZ = (float) (mZ * Math.sin(tettaRad) * Math.cos(fiRad));
        float decX = (float) (mZ * Math.sin(tettaRad) * Math.sin(fiRad));
        float decY = (float) (mZ * Math.cos(tettaRad));

        Matrix.setLookAtM(viewMatrix, 0,
                decX, decY, decZ,
                0, 0, 0f,
                0f, 1.0f, 0.0f);

        //Matrix.setLookAtM(viewMatrix, 0, 0, 0, mZ, mX, mY, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Create a rotation for the triangle
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);
        Matrix.setRotateM(rotationMatrix, 0, mAngle, 0, 0, -1.0f);

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);

        // Draw triangle
        //mBuilding.draw(scratch);
        //mCarriageBack.draw(scratch);
        //mCarriageFront.draw(scratch);
        mCylinder.draw(scratch);

    }
}
