package com.votafore.warlords.v4.constant;

import android.text.TextUtils;

import com.votafore.warlords.v2.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Votafore
 * Created on 28.12.2017.
 */

public class Log {

    private static String TAG="";
    private static List<String> levels = new ArrayList<>();

    public static void setTAG(String tag){
        TAG = tag;
    }

    public static void setLevel(String level){
        levels.add(level);
    }

    public static void reset(){
        TAG = "";
        levels.clear();
    }

    /******************** show logs *******************/

    public static void d(String msg) {
        //android.util.Log.v(TAG, getLocation() + msg);

        String prefix = "";
        for (String pfx : levels) {
            prefix = prefix + String.format(Constants.format+"|| ", pfx);
        }

        android.util.Log.d(handleTAG(TAG), prefix.concat(msg));
    }

    private static String handleTAG(String tag){
         String hTAG = tag.isEmpty() ? Constants.TAG : tag;
         return String.format(Constants.format+":", hTAG);
    }




    public static void d(String tag, String message){
        android.util.Log.d(handleTAG(tag), message);
    }

    public static void d1(String tag, String... messages){
        android.util.Log.d(handleTAG(tag), String.format(Constants.format1, messages[0], messages[1]));
    }

    public static void d2(String tag, String... messages){
        android.util.Log.d(handleTAG(tag), String.format(Constants.format2, messages[0], messages[1], messages[2]));
    }

    public static void d3(String tag, String... messages){
        android.util.Log.d(handleTAG(tag), String.format(Constants.format3, messages[0], messages[1], messages[2], messages[3]));
    }

    public static void d4(String tag, String... messages){
        android.util.Log.d(handleTAG(tag), String.format(Constants.format4, messages[0], messages[1], messages[2], messages[3], messages[4]));
    }




    /********************* miscellaneous *********************/



    /********************* utils *********************/

    //https://habrahabr.ru/post/116376/

    private static String getLocation() {
        final String className = Log.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;

        for (int i = 0; i < traces.length; i++) {
            StackTraceElement trace = traces[i];

            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber() + "]: ";
                    }
                }
                else if (trace.getClassName().startsWith(className)) {
                    found = true;
                    continue;
                }
            }
            catch (ClassNotFoundException e) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }
}
