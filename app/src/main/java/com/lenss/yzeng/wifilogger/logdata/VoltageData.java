package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.lenss.yzeng.wifilogger.LogService;

/* Gets the voltage across the battery */
public class VoltageData extends LogService.LogData {

    public VoltageData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
    }

    @Override
    public String retrieve() {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = cntxt.registerReceiver(null, iFilter);
        int voltage = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) : -1;
        return Integer.toString(voltage);
    }
}
