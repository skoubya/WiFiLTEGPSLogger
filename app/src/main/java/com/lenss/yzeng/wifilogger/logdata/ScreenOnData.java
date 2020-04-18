package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.os.PowerManager;

import com.lenss.yzeng.wifilogger.LogService;

/* Retrieves if the screen is on */
public class ScreenOnData extends LogService.LogData {
    public ScreenOnData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
    }

    @Override
    public String retrieve(){
        String result = "NA";

        try {
            PowerManager pm = (PowerManager) cntxt.getSystemService(Context.POWER_SERVICE);
            result = Boolean.toString(pm.isInteractive());
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }

        return result;
    }
}
