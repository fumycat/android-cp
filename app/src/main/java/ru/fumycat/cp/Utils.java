package ru.fumycat.cp;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {
    public static String readFromResource(Context context, int id) {
        BufferedReader r = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(id)));
        StringBuilder total = new StringBuilder();
        try {
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
            return total.toString();
        } catch (IOException e) {
            Log.println(Log.ERROR, "RESOURCE READING", "are you retarded?");
            return "";
        }
    }
}
