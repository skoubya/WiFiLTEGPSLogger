package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.lenss.yzeng.wifilogger.LogService;

/* Retrieves the Wifi frequency (in MHz)*/
public class WifiFData extends LogService.LogData {
    public WifiFData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
    }

    @Override
    public String retrieve(){
        String result = "NA";
        WifiManager wifiManager = (WifiManager) cntxt.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            result = Integer.toString(wifiInfo.getFrequency());
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }

        return result;
    }
}
