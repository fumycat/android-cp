package ru.fumycat.cp;

import android.util.Log;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void weirdJavaJokes() {
        float[] expected = {
                -2f, -2f, 2f,
                2f, -2f, 2f
        };

        float[] arr = {
                -1f, -1f, 1f,
                1f, -1f, 1f
        };
        float dimX = 4f;
        float dimY = 4f;
        float dimZ = 4f;
        for (int i = 0; i < arr.length; i++) {
            switch (i % 3) {
                case 0:
                    arr[i] *= (dimX / 2f);
                    break;
                case 1:
                    arr[i] *= (dimY / 2f);
                    break;
                case 2:
                    arr[i] *= (dimZ / 2f);
                    break;
            }
        }

        System.out.println(Arrays.toString(arr));
        assertArrayEquals(expected, arr, 0f);
    }


}