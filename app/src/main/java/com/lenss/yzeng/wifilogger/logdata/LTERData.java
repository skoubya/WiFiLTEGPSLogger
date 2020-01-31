package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;


/* Retrieves the number of received LTE packets */
public class LTERData extends LTEPacketData {
    private static int VAL_POS = 1;

    public LTERData(String name, Context context){
        super(name, context);
        valPos = VAL_POS;
    }
}
