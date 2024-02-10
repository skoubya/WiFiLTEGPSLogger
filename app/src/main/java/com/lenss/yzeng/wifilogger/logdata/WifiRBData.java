package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.os.Build;

import com.example.system_stats.LogConstants;

/* Retrieve the number of Wifi received bytes */
public class WifiRBData extends SingleDiffFileData {
    private static String[] COMMAND = {LogConstants.CAT_PATH, LogConstants.NETWORK_FILE};
    private static String SEARCH = LogConstants.WIFI_SEARCH;
    private static int VAL_POS = 0;

    public WifiRBData(String name, Context context, Process rootProc){
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
