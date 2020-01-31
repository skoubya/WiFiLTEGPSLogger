package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;

/* Collect the number of transmitted packets */
public class WifiTData extends SingleDiffFileData{
    private static String[] COMMAND = {"/system/bin/cat", "/proc/net/dev"};
    private static String SEARCH = "wlan0:";
    private static int VAL_POS = 9;

    public WifiTData(String name, Context context){
        super(name, context);
        command = COMMAND;
        search = SEARCH;
        valPos = VAL_POS;
    }
}
