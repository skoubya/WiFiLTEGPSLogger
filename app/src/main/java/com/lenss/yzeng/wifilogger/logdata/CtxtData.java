package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.os.Build;

/* Retrieves the number of context switches */
public class CtxtData extends SingleDiffFileData {
    private static String[] COMMAND = {LogConstants.CAT_PATH, LogConstants.CPU_FILE};
    private static String SEARCH = "ctxt ";
    private static int VAL_POS = 0;

    public CtxtData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
        command = COMMAND;
        search = SEARCH;
        valPos = VAL_POS;

        if(Build.VERSION.SDK_INT >= 26){//TODO: not sure which SDK
            needRoot = true;
        }
        else {
            needRoot = false;
        }
    }
}
