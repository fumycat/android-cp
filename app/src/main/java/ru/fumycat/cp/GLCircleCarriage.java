package ru.fumycat.cp;

import android.content.Context;
import android.opengl.GLES20;

import java.util.ArrayList;

import android.opengl.GLUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import android.opengl.Matrix;

public class GLCircleCarriage {
    // Number of bytes of float type
    private static final int BYTES_PER_FLOAT = 4;
    // The number of coordinates of each vertex in the array
    private static final int COORDS_PER_VERTEX = 3;
    private static final int COORDS_PER_TEXTURE = 2;

    private final float radius; // the radius of the ball
    private final double angleSpan = Math.PI / 90f; // The angle at which the ball is divided into units
    private final int textureDataHandle;
    private final float color[];

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

    //float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public interface DrawCtrl{
        void shaders_init(int mProgramHandle);
        void shaders_free();
    }

    public GLCircleCarriage(Context context, float x, float y, float z, float radius, float[] obj_color, int texture,
                      DrawCtrl ctrl, String VERTEX_SHADER, String FRAGMENT_SHADER) {
        this.textureDataHandle = texture;
        this.radius = radius;
        this.color = Arrays.copyOf(obj_color, obj_color.length);

        initSphereVertex(x, y, z);
        createProgram(context, VERTEX_SHADER, FRAGMENT_SHADER);

        this.ctrl = ctrl;
    }

    public GLCircleCarriage(Context context, float x, float y, float z, float radius, float[] obj_color, int texture) {
        this.textureDataHandle = texture;
        this.radius = radius;
        this.color = Arrays.copyOf(obj_color, obj_color.length);

        initSphereVertex(x, y, z);
        createProgram(context, null, null);

        ctrl = new DrawCtrl() {
            @Override
            public void shaders_init(int mProgramHandle) {
                GLES20.glUniform4fv(muColorHandle, 1, color, 0);
            }

            @Override
            public void shaders_free() {
            }
        };
    }

    public void initSphereVertex(float x, float y, float z) {
        ArrayList <Float> vertex = new ArrayList<Float>();
        ArrayList <Float> texture = new ArrayList<Float>();

        for (double vAngle = 0; vAngle < Math.PI; vAngle = vAngle + angleSpan) { // vertical
            for (double hAngle = 0; hAngle < 2 * Math.PI; hAngle = hAngle + angleSpan) { // horizontal

                float x0 = (float) (x + radius * Math.sin(vAngle) * Math.cos(hAngle));
                float y0 = (float) (y + radius * Math.sin(vAngle) * Math.sin(hAngle));
                float z0 = (float) (z + radius * Math.cos((vAngle)));

                float x1 = (float) (x + radius * Math.sin(vAngle) * Math.cos(hAngle + angleSpan));
                float y1 = (float) (y + radius * Math.sin(vAngle) * Math.sin(hAngle + angleSpan));
                float z1 = (float) (z + radius * Math.cos(vAngle));

                float x2 = (float) (x + radius * Math.sin(vAngle + angleSpan) * Math.cos(hAngle + angleSpan));
                float y2 = (float) (y + radius * Math.sin(vAngle + angleSpan) * Math.sin(hAngle + angleSpan));
                float z2 = (float) (z+ radius * Math.cos(vAngle + angleSpan));

                float x3 = (float) (x + radius * Math.sin(vAngle + angleSpan) * Math.cos(hAngle));
                float y3 = (float) (y + radius * Math.sin(vAngle + angleSpan) * Math.sin(hAngle));
                float z3 = (float) (z + radius * Math.cos(vAngle + angleSpan));

                vertex.add(x1); texture.add((float)((vAngle)/(float)(Math.PI)));
                vertex.add(y1); texture.add((float)((hAngle + angleSpan)/(float)(Math.PI*2)));
                vertex.add(z1);

                vertex.add(x3); texture.add((float)((vAngle + angleSpan)/(float)(Math.PI)));
                vertex.add(y3); texture.add((float)(hAngle/(float)(Math.PI*2)));
                vertex.add(z3);

                vertex.add(x0); texture.add((float)((vAngle)/(float)(Math.PI)));
                vertex.add(y0); texture.add((float)((hAngle)/(float)(Math.PI*2)));
                vertex.add(z0);

                vertex.add(x1); texture.add((float)((vAngle)/(float)(Math.PI)));
                vertex.add(y1); texture.add((float)((hAngle + angleSpan)/(float)(Math.PI*2)));
                vertex.add(z1);

                vertex.add(x2); texture.add((float)((vAngle + angleSpan)/(float)(Math.PI)));
                vertex.add(y2); texture.add((float)((hAngle + angleSpan)/(float)(Math.PI*2)));
                vertex.add(z2);

                vertex.add(x3); texture.add((float)((vAngle + angleSpan)/(float)(Math.PI)));
                vertex.add(y3); texture.add((float)(hAngle/(float)(Math.PI*2)));
                vertex.add(z3);
            }
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

    /**
     * Create Program
     */
    private void createProgram(Context context, String vertex_sh, String fragment_sh) {
        String VERTEX_SHADER, FRAGMENT_SHADER;

        if (vertex_sh != null) {
            VERTEX_SHADER = vertex_sh;
        }
        else if (textureDataHandle != 0){
            VERTEX_SHADER = Utils.readStringFromResource(context, R.raw.cylinder_basic_vertex);
        }
        else {
            VERTEX_SHADER = Utils.readStringFromResource(context, R.raw.circle_basic_vertex);
        }

        if (fragment_sh != null) {
            FRAGMENT_SHADER = fragment_sh;
        }
        else if (textureDataHandle != 0) {
            FRAGMENT_SHADER = Utils.readStringFromResource(context, R.raw.cylinder_basic_fragment);
        }
        else {
            FRAGMENT_SHADER = Utils.readStringFromResource(context, R.raw.circle_basic_fragment);
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

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgramHandle);
        GLES20.glVertexAttribPointer(maPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glUniformMatrix4fv(muMatrixHandle, 1, false, mvpMatrix, 0);

        if (textureDataHandle != 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
            GLES20.glUniform1i(mTextureUniformHandle, 0);
            GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        }
        ctrl.shaders_init(mProgramHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);

        ctrl.shaders_free();
        GLES20.glDisableVertexAttribArray(maPositionHandle);
        GLES20.glUseProgram(0);
    }

}
