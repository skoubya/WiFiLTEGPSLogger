package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.lenss.yzeng.wifilogger.LogService;

/* Retrieves the battery percentage */
public class BatteryData extends LogService.LogData {

    public BatteryData(String name, Context context){
        super(name, context);
    }

    @Override
    public String retrieve() {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = cntxt.registerReceiver(null, iFilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        float batteryPct = level / (float) scale;
        return Float.toString(batteryPct);
    }
}
