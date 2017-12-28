package com.votafore.warlords.v3;

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

        String tag = TAG.isEmpty() ? Constants.TAG : TAG;
        tag = String.format(Constants.format+":", tag);

        android.util.Log.d(tag, prefix.concat(msg));
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
