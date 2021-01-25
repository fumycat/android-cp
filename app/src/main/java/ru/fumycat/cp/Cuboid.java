package ru.fumycat.cp;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

public class Cuboid {

    private final String vertexShaderString;
    private final String fragmentShaderString;

    private float[] vertices;
    private byte[] order = {
            0, 4, 5, 0, 5, 1,
            1, 5, 6, 1, 6, 2,
            2, 6, 7, 2, 7, 3,
            3, 7, 4, 3, 4, 0,
            4, 7, 6, 4, 6, 5,
            3, 0, 1, 3, 1, 2
    };

    private float[][] colors = {
            {1.0f, 0.5f, 0.0f, 1.0f},  // 0. orange
            {1.0f, 0.0f, 1.0f, 1.0f},  // 1. violet
            {0.0f, 1.0f, 0.0f, 1.0f},  // 2. green
            {0.0f, 0.0f, 1.0f, 1.0f},  // 3. blue
            {1.0f, 0.0f, 0.0f, 1.0f},  // 4. red
            {1.0f, 1.0f, 0.0f, 1.0f}   // 5. yellow
    };

    private FloatBuffer vertexBuffer;
    private ByteBuffer orderBuffer;
    private final int program;

    private int posHandle;
    private int colHandle;
    private int vPMatrixHandle;

    /**
     * context - чтобы читать шейдеры из файла
     * centerX/Y/Z - центр фигуры
     * dimX/Y/Z - размеры
     */
    public Cuboid(Context context, float centerX, float centerY, float centerZ, float dimX, float dimY, float dimZ) {
        vertexShaderString = Utils.readFromResource(context, R.raw.cuboid_vertex);
        fragmentShaderString = Utils.readFromResource(context, R.raw.cuboid_fragment);

        float halfX = dimX / 2f;
        float halfY = dimY / 2f;
        float halfZ = dimZ / 2f;
        vertices = new float[]{
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f,  1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f,  1.0f,
                1.0f, -1.0f,  1.0f,
                1.0f,  1.0f,  1.0f,
                -1.0f,  1.0f,  1.0f
        };
        for (int i = 0; i < vertices.length; i++) {
            switch (i % 3) {
                case 0:
                    vertices[i] *= (dimX / 2f);
                    break;
                case 1:
                    vertices[i] *= (dimY / 2f);
                    break;
                case 2:
                    vertices[i] *= (dimZ / 2f);
                    break;
            }
        }
        for (int i = 0; i < vertices.length; i++) {
            switch (i % 3) {
                case 0:
                    vertices[i] += centerX;
                    break;
                case 1:
                    vertices[i] += centerY;
                    break;
                case 2:
                    vertices[i] += centerZ;
                    break;
            }
        }

        Log.println(Log.INFO, "Cuboid", Arrays.toString(vertices));

        vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        orderBuffer = ByteBuffer.allocateDirect(order.length);
        orderBuffer.put(order);
        orderBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderString);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderString);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(program);

        posHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(posHandle);
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);

        vPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        colHandle = GLES20.glGetUniformLocation(program, "vColor");

        for (int face = 0; face < order.length / 6; face++) {
            GLES20.glUniform4fv(colHandle, 1, colors[face], 0);
            orderBuffer.position(face * 6);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_BYTE, orderBuffer);
        }

        GLES20.glDisableVertexAttribArray(posHandle);
    }
}
