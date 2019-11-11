package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;

import com.lenss.yzeng.wifilogger.LogService;
import com.lenss.yzeng.wifilogger.util.Utils;

import java.io.IOException;
import java.io.InputStream;

public class CPUData extends LogService.LogData {
    private static String[] COMMAND = {"/system/bin/cat", "/proc/stat"};
    private int prevTotalCycles;
    private int prevIdleCycles;

    public CPUData(String name, Context context){
        super(name,context);
        prevTotalCycles = -1;
        prevIdleCycles = -1;
    }

    @Override
    public String retrieve(){
        String result = "NA";

        try {
            String row = Utils.searchCommandOutput(COMMAND, "cpu ");
            String[] vals = row.split("\\s");

            int totalCycles = 0;
            int idleCycles = 0;
            int numCount = 0;
            int idleIndex = 4; //4th number is the idle cycles
            for(String val : vals){
                try{
                    totalCycles += Integer.parseInt(val);
                    numCount++;
                    if(numCount == idleIndex){
                        idleCycles = Integer.parseInt(val);
                    }
                }
                catch(NumberFormatException e){
                    //just let pass
                }
            }

            if(prevTotalCycles != -1 && prevIdleCycles != -1){
                double usage = 1 - (idleCycles-prevIdleCycles)/(double)(totalCycles - prevTotalCycles);
                result = Double.toString(usage);
            }
            prevTotalCycles = totalCycles;
            prevIdleCycles = idleCycles;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }
}
