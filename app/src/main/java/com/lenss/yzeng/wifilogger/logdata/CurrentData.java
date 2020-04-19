package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.os.BatteryManager;

import com.lenss.yzeng.wifilogger.LogService;

/* Gets the current flowing through the battery */
public class CurrentData extends LogService.LogData {

    public CurrentData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
    }

    @Override
    public String retrieve() {
        BatteryManager manager = (BatteryManager)cntxt.getSystemService(Context.BATTERY_SERVICE);
        long current = manager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        return Long.toString(current);
    }
}
