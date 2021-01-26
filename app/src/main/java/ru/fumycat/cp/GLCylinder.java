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
    // Number of bytes of float type
    private static final int BYTES_PER_FLOAT = 4;
    // The number of coordinates of each vertex in the array
    private static final int COORDS_PER_VERTEX = 3;
    private static final int COORDS_PER_TEXTURE = 2;

    private final float radius; // the radius of the ball
    private final double angleSpan = Math.PI / 90f; // The angle at which the ball is divided into units
    private final int textureDataHandle;

    private FloatBuffer mVertexBuffer;// Vertex coordinates
    private FloatBuffer mTextureBuffer;
    int mVertexCount = 0;// The number of vertices, first initialized to 0
    int mTextureCount = 0;

    private int mProgramHandle;
    private int maPositionHandle;
    private int muColorHandle;
    private int muMatrixHandle;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle = 0;

    DrawCtrl ctrl;

    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private void createProgram(Context context, String vertex_sh, String fragment_sh) {
        String VERTEX_SHADER, FRAGMENT_SHADER;

        if (vertex_sh != null) {
            VERTEX_SHADER = vertex_sh;
        }
        else {
            VERTEX_SHADER = Utils.readStringFromResource(context, R.raw.cylinder_basic_vertex);
        }

        if (fragment_sh != null) {
            FRAGMENT_SHADER = fragment_sh;
        }
        else {
            FRAGMENT_SHADER = Utils.readStringFromResource(context, R.raw.cylinder_basic_fragment);
        }

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        mProgramHandle = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgramHandle, vertexShader);
        GLES20.glAttachShader(mProgramHandle, fragmentShader);

        GLES20.glLinkProgram(mProgramHandle);

        muMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        maPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");

        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");

        //if (vertex_sh == null) {
        //}

        if (fragment_sh == null) {
            muColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Color");
        }

    }

    public interface DrawCtrl{
        void shaders_init();
        void shaders_free();
    }

    public GLCylinder(Context context, float x, float y, float z, float radius, float length, int texture,
                      DrawCtrl ctrl, String VERTEX_SHADER, String FRAGMENT_SHADER) {
        this.textureDataHandle = texture;
        this.radius = radius;

        initCylinderVertex(x, y, z, length);
        createProgram(context, VERTEX_SHADER, FRAGMENT_SHADER);

        this.ctrl = ctrl;
    }

    public GLCylinder(Context context, float x, float y, float z, float radius, float length, int texture) {
        this.textureDataHandle = texture;
        this.radius = radius;

        initCylinderVertex(x, y, z, length);
        createProgram(context, null, null);

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
    }

    public void initCylinderVertex(float x, float y, float z, float length) {
        ArrayList <Float> vertex = new ArrayList<Float>();
        ArrayList <Float> texture = new ArrayList<Float>();

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
            vertex.add(x); texture.add(0f);
            vertex.add(y); texture.add(0f);
            vertex.add(zc1);

            vertex.add(x0); texture.add(0f);
            vertex.add(y0); texture.add(0f);
            vertex.add(z0);

            vertex.add(x1); texture.add(0f);
            vertex.add(y1); texture.add(0f);
            vertex.add(z1);

            //2
            vertex.add(x); texture.add(0f);
            vertex.add(y); texture.add(0f);
            vertex.add(zc2);

            vertex.add(x2); texture.add(0f);
            vertex.add(y2); texture.add(0f);
            vertex.add(z2);

            vertex.add(x3); texture.add(0f);
            vertex.add(y3); texture.add(0f);
            vertex.add(z3);

            //horizontal
            //1
            vertex.add(x0); texture.add(0f);
            vertex.add(y0); texture.add((float)(hAngle/(float)(Math.PI*2)));
            vertex.add(z0);

            vertex.add(x3); texture.add(1f);
            vertex.add(y3); texture.add((float) (hAngle/(float)(Math.PI*2)));
            vertex.add(z3);

            vertex.add(x2); texture.add(1f);
            vertex.add(y2); texture.add((float)(hAngle + angleSpan)/(float)(Math.PI*2));
            vertex.add(z2);

            //2
            vertex.add(x0); texture.add(0f);
            vertex.add(y0); texture.add((float)(hAngle/(float)(Math.PI*2)));
            vertex.add(z0);

            vertex.add(x2); texture.add(1f);
            vertex.add(y2); texture.add((float)(hAngle + angleSpan)/(float)(Math.PI*2));
            vertex.add(z2);

            vertex.add(x1); texture.add(0f);
            vertex.add(y1); texture.add((float)(hAngle + angleSpan)/(float)(Math.PI*2));
            vertex.add(z1);
        }

        //vertexes
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

        //textures
        mTextureCount = texture.size() / COORDS_PER_TEXTURE;
        float vtextures[] = new float[texture.size()];
        for (int i = 0; i < texture.size(); i++) {
            vtextures[i] = texture.get(i);
        }
        mTextureBuffer = ByteBuffer.allocateDirect(vtextures.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureBuffer.put(vtextures).position(0);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgramHandle);
        GLES20.glVertexAttribPointer(maPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glUniformMatrix4fv(muMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        ctrl.shaders_init();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);

        ctrl.shaders_free();
        GLES20.glDisableVertexAttribArray(maPositionHandle);
        GLES20.glUseProgram(0);
    }

}
