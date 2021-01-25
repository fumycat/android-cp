package ru.fumycat.cp;

import android.content.Context;
import android.opengl.GLES20;

import java.util.ArrayList;

import android.opengl.GLUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.Matrix;

public class GLCylinder {
    private float radius = 1.0f; // the radius of the ball
    final double angleSpan = Math.PI / 90f; // The angle at which the ball is divided into units
    private FloatBuffer mVertexBuffer;// Vertex coordinates
    int mVertexCount = 0;// The number of vertices, first initialized to 0

    // Number of bytes of float type
    private static final int BYTES_PER_FLOAT = 4;

    // The number of coordinates of each vertex in the array
    private static final int COORDS_PER_VERTEX = 3;

    private int mProgramHandle;
    private int maPositionHandle;
    private int muColorHandle;
    private int muMatrixHandle;

    public int getHandle(){
        return mProgramHandle;
    }

    DrawCtrl ctrl;

    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private void createProgram(Context context, String vertex_sh, String fragment_sh) {
        String VERTEX_SHADER, FRAGMENT_SHADER;

        if (vertex_sh != null) {
            VERTEX_SHADER = vertex_sh;
        }
        else {
            VERTEX_SHADER = Utils.readStringFromResource(context, R.raw.basic_vertex);
        }

        if (fragment_sh != null) {
            FRAGMENT_SHADER = fragment_sh;
        }
        else {
            FRAGMENT_SHADER = Utils.readStringFromResource(context, R.raw.basic_fragment);
        }

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        mProgramHandle = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgramHandle, vertexShader);
        GLES20.glAttachShader(mProgramHandle, fragmentShader);

        GLES20.glLinkProgram(mProgramHandle);

        if (vertex_sh == null) {
            muMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
            maPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        }

        if (fragment_sh == null) {
            muColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Color");
        }

    }

    public interface DrawCtrl{
        void shaders_init();
        void shaders_free();
    }

    public GLCylinder(Context context, float x, float y, float z, float radius, float length,
                      DrawCtrl ctrl, String VERTEX_SHADER, String FRAGMENT_SHADER) {
        this.radius = radius;

        this.ctrl = ctrl;

        initCylinderVertex(x, y, z, length);
        createProgram(context, VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public GLCylinder(Context context, float x, float y, float z, float radius, float length) {
        this.radius = radius;

        ctrl = new DrawCtrl(){
            @Override
            public void shaders_init()
            {
                GLES20.glUniform4fv(muColorHandle, 1, color, 0);
            }
            @Override
            public void shaders_free()
            {
            }
        };

        initCylinderVertex(x, y, z, length);
        createProgram(context, null, null);
    }

    public void initCylinderVertex(float x, float y, float z, float length) {
        ArrayList<Float> vertex = new ArrayList<Float>();

        for (double hAngle = 0; hAngle < 2 * Math.PI; hAngle = hAngle + angleSpan) {
            float div_len = (float) length / 2;
            float x0 = (float) (x + radius * Math.cos(hAngle));
            float y0 = (float) (y + radius * Math.sin(hAngle));
            float z0 = (float) z + (-1 * div_len);

            float x1 = (float) (x + radius * Math.cos(hAngle + angleSpan));
            float y1 = (float) (y + radius * Math.sin(hAngle + angleSpan));
            float z1 = (float) z + (-1 * div_len);

            float x2 = (float) (x + radius * Math.cos(hAngle + angleSpan));
            float y2 = (float) (y + radius * Math.sin(hAngle + angleSpan));
            float z2 = (float) z + div_len;

            float x3 = (float) (x + radius * Math.cos(hAngle));
            float y3 = (float) (y + radius * Math.sin(hAngle));
            float z3 = (float) z + div_len;

            float zc1 = (float) z + (-1 * div_len);

            float zc2 = (float) z + div_len;

            //vertical "caps"
            //1
            vertex.add(x);
            vertex.add(y);
            vertex.add(zc1);

            vertex.add(x0);
            vertex.add(y0);
            vertex.add(z0);

            vertex.add(x1);
            vertex.add(y1);
            vertex.add(z1);

            //2
            vertex.add(x);
            vertex.add(y);
            vertex.add(zc2);

            vertex.add(x2);
            vertex.add(y2);
            vertex.add(z2);

            vertex.add(x3);
            vertex.add(y3);
            vertex.add(z3);

            //horizontal
            //1
            vertex.add(x0);
            vertex.add(y0);
            vertex.add(z0);

            vertex.add(x3);
            vertex.add(y3);
            vertex.add(z3);

            vertex.add(x2);
            vertex.add(y2);
            vertex.add(z2);

            //2
            vertex.add(x0);
            vertex.add(y0);
            vertex.add(z0);

            vertex.add(x2);
            vertex.add(y2);
            vertex.add(z2);

            vertex.add(x1);
            vertex.add(y1);
            vertex.add(z1);
        }


        mVertexCount = vertex.size() / COORDS_PER_VERTEX;
        float vertices[] = new float[vertex.size()];
        for (int i = 0; i < vertex.size(); i++) {
            vertices[i] = vertex.get(i);
        }
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                vertices.length * BYTES_PER_FLOAT);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        mVertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        mVertexBuffer.put(vertices);
        // set the buffer to read the first coordinate
        mVertexBuffer.position(0);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgramHandle);
        GLES20.glVertexAttribPointer(maPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glUniformMatrix4fv(muMatrixHandle, 1, false, mvpMatrix, 0);
        ctrl.shaders_init();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);

        ctrl.shaders_free();
        GLES20.glDisableVertexAttribArray(maPositionHandle);
        GLES20.glUseProgram(0);
    }

}
