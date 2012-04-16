package com.samsung.cares.util;

import android.util.Log;

public class Logger {
 
	private final static boolean SHOW_LOGS = true;
	private final static String TAG = "CARES";
 
    private Logger() {
    }

    public static void d(String msg) {
        if(SHOW_LOGS) {
            Log.d(TAG, TAG + "_D : " + msg);
        }
    }

    public static void e(String msg) {
        if(SHOW_LOGS) {
            Log.e(TAG, TAG + "_E : " + msg);
        }
    }

    public static void i(String msg) {
        if(SHOW_LOGS) {
            Log.i(TAG, TAG + "_I : " + msg);
        }
    }

    public static void w(String msg) {
        if(SHOW_LOGS) {
            Log.w(TAG, TAG + "_W : " + msg);
        }
    }
}