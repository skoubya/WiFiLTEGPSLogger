package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;

/* Retrieves the number of received LTE bytes */
public class LTERBData extends LTEPacketData {
    private static int VAL_POS = 0;

    public LTERBData(String name, Context context){
        super(name, context);
        valPos = VAL_POS;
    }
}
