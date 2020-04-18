package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.lenss.yzeng.wifilogger.LogService;

/* Retrieves the Wifi received power */
public class WifiRPData extends LogService.LogData {
    public WifiRPData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
    }

    @Override
    public String retrieve(){
        String result = "NA";
        WifiManager wifiManager = (WifiManager) cntxt.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            result = Integer.toString(wifiInfo.getRssi());
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }

        return result;
    }
}
