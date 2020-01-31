package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;

/* Collect the number of received Wifi packets */
public class WifiRData extends SingleDiffFileData {
    private static String[] COMMAND = {"/system/bin/cat", "/proc/net/dev"};
    private static String SEARCH = "wlan0:"; //TODO: get wifi name same way as LTE
    private static int VAL_POS = 1;

    public WifiRData(String name, Context context){
        super(name, context);
        command = COMMAND;
        search = SEARCH;
        valPos = VAL_POS;
    }
}
