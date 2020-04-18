package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;

/* Retrieves the number of transmitted LTE packets */
public class LTETData extends LTEPacketData {
    private static int VAL_POS = 9;

    public LTETData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
        valPos = VAL_POS;
    }
}
