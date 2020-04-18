package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;

/* Retrieves the number of transmitted LTE bytes */
public class LTETBData extends LTEPacketData {
    private static int VAL_POS = 8;

    public LTETBData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
        valPos = VAL_POS;
    }
}
