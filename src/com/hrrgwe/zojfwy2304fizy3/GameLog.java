package com.hrrgwe.zojfwy2304fizy3;

import android.util.Log;

public class GameLog {
    private static String LOG_TAG = "jyhy";
    private static boolean isShowLog = true;
    
    public static void logInfo(String msg)
    {
        if (isShowLog)
            Log.i(LOG_TAG, msg);
    }
    
    public static void logWarning(String msg)
    {
    	if (isShowLog)
    		Log.w(LOG_TAG, msg);
    }
    
    public static void logError(String msg)
    {
        if (isShowLog)
            Log.e(LOG_TAG, msg);
    }
    
    public static void logError(String msg, Throwable tr)
    {
        if (isShowLog)
            Log.e(LOG_TAG, msg, tr);
    }
    
    public static void setIsShowLog(boolean v)
    {
        isShowLog = v;
    }
}
