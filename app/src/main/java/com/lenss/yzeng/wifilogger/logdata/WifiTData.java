package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.os.Build;

/* Collect the number of transmitted packets */
public class WifiTData extends SingleDiffFileData{
    private static String[] COMMAND = {LogConstants.CAT_PATH, LogConstants.NETWORK_FILE};
    private static String SEARCH = LogConstants.WIFI_SEARCH;
    private static int VAL_POS = 9;

    public WifiTData(String name, Context context, Process rootProc){
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
