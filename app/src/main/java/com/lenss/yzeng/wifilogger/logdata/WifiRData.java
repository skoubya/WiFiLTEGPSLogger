package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.os.Build;

/* Collect the number of received Wifi packets */
public class WifiRData extends SingleDiffFileData {
    private static String[] COMMAND = {LogConstants.CAT_PATH, LogConstants.NETWORK_FILE};
    private static String SEARCH = LogConstants.WIFI_SEARCH; //TODO: get wifi name same way as LTE
    private static int VAL_POS = 1;

    public WifiRData(String name, Context context, Process rootProc){
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
