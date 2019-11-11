package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;

import com.lenss.yzeng.wifilogger.LogService;
import com.lenss.yzeng.wifilogger.util.Utils;

import java.io.IOException;

public class CtxtData extends LogService.LogData {
    private static String[] COMMAND = {"/system/bin/cat", "/proc/stat"};
    private int prevCtxtSwitch;

    public CtxtData(String name, Context context){
        super(name, context);
        prevCtxtSwitch = -1;
    }

    @Override
    public String retrieve(){
        String result = "NA";

        try {
            String row = Utils.searchCommandOutput(COMMAND, "cpu ");
            String[] vals = row.split("\\s");

            int ctxtSwitch = 0;
            for(String val : vals){
                try{
                    ctxtSwitch = Integer.parseInt(val); //gets 1st integer value
                    break;
                }
                catch(NumberFormatException e){
                    //just let pass
                }
            }

            if(prevCtxtSwitch != -1){
                int usage = (ctxtSwitch- prevCtxtSwitch);
                result = Integer.toString(usage);
            }
            prevCtxtSwitch = ctxtSwitch;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }
}
