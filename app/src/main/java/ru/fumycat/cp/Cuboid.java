package ru.fumycat.cp;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class Cuboid {

    private final String vertexShader;
    private final String fragmentShader;

    public Cuboid(Context context) {
        vertexShader = Utils.readFromResource(context, R.raw.cuboid_vertex);
        fragmentShader = Utils.readFromResource(context, R.raw.cuboid_fragment);
    }

    public void draw() {
        // float[] mvpMatrix
//        GLES20.glUseProgram(mProgramHandle);
//        GLES20.glVertexAttribPointer(maPositionHandle, COORDS_PER_VERTEX,
//                GLES20.GL_FLOAT, false, 0, mVertexBuffer);
//        GLES20.glEnableVertexAttribArray(maPositionHandle);
//        GLES20.glUniformMatrix4fv(muMatrixHandle, 1, false, mvpMatrix, 0);
//        GLES20.glUniform4fv(muColorHandle, 1, color, 0);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);
//        GLES20.glDisableVertexAttribArray(maPositionHandle);
//        GLES20.glUseProgram(0);
    }
}
