package com.lenss.yzeng.wifilogger.logdata;

import android.app.ActivityManager;
import android.content.Context;

import com.lenss.yzeng.wifilogger.LogService;

import static android.content.Context.ACTIVITY_SERVICE;

/* Retrieves the percentage of the memory used */
public class MemData extends LogService.LogData {
    public MemData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
    }

    @Override
    public String retrieve(){
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) cntxt.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        double percentAvail = mi.availMem / (double)mi.totalMem;
        return Double.toString(1-percentAvail);
    }
}
