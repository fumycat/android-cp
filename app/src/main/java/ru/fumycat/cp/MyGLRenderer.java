package ru.fumycat.cp;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Context context;

    private Building mBuilding;
    private GLCircleCarriage mCarriageBack, mCarriageFront;
    private GLCylinder mCylinder;
    private Cuboid mCuboid;
    private CuboidTexturesWIP mCuboidTextured;

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mvMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private final float[] rotationMatrix = new float[16];

    public final float cubeAngel = 0f;

    public volatile float fi = 30f;
    public volatile float tetta = 120f;
    public volatile float radius = -9;

    private int mTextureDataHandleBrick;

    public MyGLRenderer(Context context) {
        this.context = context;
    }

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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
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
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);

        mTextureDataHandleBrick = Utils.loadTexture(context, R.drawable.stone_wall_public_domain);

        mBuilding = new Building();
        mCarriageBack = new GLCircleCarriage(0, 0,-3.9f);
        mCarriageFront = new GLCircleCarriage(0, 0,3.9f);
        mCylinder = new GLCylinder(context, 0, 4,0, 4f);
        mCuboid = new Cuboid(context, 1.5f, 0, 0, 2, 1,2);
        mCuboidTextured = new CuboidTexturesWIP(context,
                -1.5f, 0f, 0f,
                2f, 2f, 2f,
                mTextureDataHandleBrick);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 80);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Camera
        double tettaRad = Math.toRadians(tetta);
        double fiRad = Math.toRadians(fi);
        float decZ = (float) (radius * Math.sin(tettaRad) * Math.cos(fiRad));
        float decX = (float) (radius * Math.sin(tettaRad) * Math.sin(fiRad));
        float decY = (float) (radius * Math.cos(tettaRad));

        Matrix.setLookAtM(viewMatrix, 0,
                decX, decY, decZ,
                0, 0, 0f,
                0f, 1.0f, 0.0f);

        // Calculate
        Matrix.multiplyMM(mvMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        Matrix.setRotateM(rotationMatrix, 0, cubeAngel, 0, 0, 1.0f);

        float[] finalMatrixCube = new float[16];
        Matrix.multiplyMM(finalMatrixCube, 0, mvMatrix, 0, rotationMatrix, 0);

        // draw
        // mBuilding.draw(finalMatrixCube);
        mCarriageBack.draw(finalMatrixCube);
        mCarriageFront.draw(finalMatrixCube);
        mCylinder.draw(finalMatrixCube);

        mCuboid.draw(finalMatrixCube);
        mCuboidTextured.draw(finalMatrixCube, viewMatrix);
    }
}
