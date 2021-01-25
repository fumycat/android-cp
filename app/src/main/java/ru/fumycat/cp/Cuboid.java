package ru.fumycat.cp;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Cuboid {

    private final String vertexShader;

    public Cuboid(Context context) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.cuboid_vertex)));
        StringBuilder total = new StringBuilder();
        for (String line; (line = r.readLine()) != null; ) {
            total.append(line).append('\n');
        }
        vertexShader = total.toString();

        Log.println(Log.INFO, "shader", vertexShader);
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
