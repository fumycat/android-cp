package ru.fumycat.cp;

import android.opengl.GLES20;

import java.util.ArrayList;

import android.opengl.GLUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.Matrix;

public class GLCircleCarriage {

    private static final String VERTEX_SHADER =
            "uniform mat4 u_Matrix;//The final transformation matrix\n" +
                    "attribute vec4 a_Position;//Vertex position\n" +
                    "varying vec4 vPosition;//The vertex position used to pass to the fragment shader\n" +
                    "void main() {\n" +
                    "    \n" +
                    "    gl_Position = u_Matrix * vec4(a_Position.x, a_Position.y, a_Position.z*0.5, a_Position.w);\n" +
                    "    vPosition = a_Position;\n" +
                    "}";

    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "varying vec4 vPosition;\n" +
                    "void main() {\n" +
                    "float uR = 0.6;//The radius of the ball\n" +
                    "    vec4 color;\n" +
                    "float n = 8.0;//divided into n layers, n columns and n rows\n" +
                    "float span = 2.0*uR/n;//square length\n" +
                    "//Calculate the number of ranks\n" +
                    "int i = int((vPosition.x + uR)/span);//number of rows\n" +
                    "int j = int((vPosition.y + uR)/span);//number of layers\n" +
                    "int k = int((vPosition.z + uR)/span);//Number of columns\n" +
                    "    int colorType = int(mod(float(i+j+k),2.0));\n" +
                    "if(colorType == 1) {//green when odd number\n" +
                    "        color = vec4(0.2,1.0,0.129,0);\n" +
                    "} else {//White when even number\n" +
                    "color = vec4(1.0,1.0,1.0,0);//white\n" +
                    "    }\n" +
                    "// Give the calculated color to this piece\n" +
                    "    gl_FragColor = color;\n" +
                    "}";

    /*private final String FRAGMENT_SHADER =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";*/

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

    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public GLCircleCarriage(float x, float y, float z) {
        initSphereVertex(x, y, z);
        createProgram();
    }

    /**
     * Calculate the vertices of the spherical surface
     */
    public void initSphereVertex(float x, float y, float z) {
        ArrayList<Float> vertex = new ArrayList<Float>();

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

                vertex.add(x1);
                vertex.add(y1);
                vertex.add(z1);

                vertex.add(x3);
                vertex.add(y3);
                vertex.add(z3);

                vertex.add(x0);
                vertex.add(y0);
                vertex.add(z0);

                vertex.add(x1);
                vertex.add(y1);
                vertex.add(z1);

                vertex.add(x2);
                vertex.add(y2);
                vertex.add(z2);

                vertex.add(x3);
                vertex.add(y3);
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
                vertices.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        mVertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        mVertexBuffer.put(vertices);
        // set the buffer to read the first coordinate
        mVertexBuffer.position(0);
    }

    /**
     * Create Program
     */
    private void createProgram() {
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        mProgramHandle = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgramHandle, vertexShader);
        GLES20.glAttachShader(mProgramHandle, fragmentShader);

        GLES20.glLinkProgram(mProgramHandle);

        maPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        muMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Matrix");
        //muColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "vColor");
    }

    /**
     * Draw a sphere
     */
    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgramHandle);
        GLES20.glVertexAttribPointer(maPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glUniformMatrix4fv(muMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniform4fv(muColorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);
        GLES20.glDisableVertexAttribArray(maPositionHandle);
        GLES20.glUseProgram(0);
    }

}
