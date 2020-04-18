package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.os.Build;

/* Retrieve the number of Wifi transmitted bytes */
public class WifiTBData extends SingleDiffFileData {
    private static String[] COMMAND = {"/system/bin/cat", "/proc/net/dev"};
    private static String SEARCH = "wlan0:";
    private static int VAL_POS = 8;

    public WifiTBData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
        command = COMMAND;
        search = SEARCH;
        valPos = VAL_POS;

        if(Build.VERSION.SDK_INT >= 28){//Android 9 and later
            needRoot = true;
        }
        else {
            needRoot = false;
        }
    }
}
