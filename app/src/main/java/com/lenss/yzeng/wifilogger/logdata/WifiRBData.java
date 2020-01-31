package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;

/* Retrieve the number of Wifi received bytes */
public class WifiRBData extends SingleDiffFileData {
    private static String[] COMMAND = {"/system/bin/cat", "/proc/net/dev"};
    private static String SEARCH = "wlan0:";
    private static int VAL_POS = 0;

    public WifiRBData(String name, Context context){
        super(name, context);
        command = COMMAND;
        search = SEARCH;
        valPos = VAL_POS;
    }
}
