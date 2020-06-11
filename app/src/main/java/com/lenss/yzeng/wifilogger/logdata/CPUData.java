package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.os.Build;

import com.lenss.yzeng.wifilogger.LogService;
import com.lenss.yzeng.wifilogger.util.Utils;

import java.io.IOException;

/* Retrieves the percent usage of CPU */
public class CPUData extends LogService.LogData {
    private static String[] COMMAND = {LogConstants.CAT_PATH, LogConstants.CPU_FILE};
    private long prevTotalCycles;
    private long prevIdleCycles;

    public CPUData(String name, Context context, Process rootProc){
        super(name,context, rootProc);
        prevTotalCycles = -1;
        prevIdleCycles = -1;
    }

    @Override
    public String retrieve(){
        String result = "NA";

        boolean needRoot = false;
        if(Build.VERSION.SDK_INT >= 26) { //TODO: not sure which SDK
            needRoot = true;
        }

        try {
            String row = Utils.searchCommandOutput(COMMAND, LogConstants.CPU_SEARCH, needRoot, rootProc);
            String[] vals = row.split("\\s");
            System.out.println(row);

            // There should be 10 values in val plus the word CPU
            boolean cantAccess = row.isEmpty() || vals.length < 11;

            if(!cantAccess) {
                long totalCycles = 0;
                long idleCycles = 0;
                int numCount = 0;
                int idleIndex = 4; //4th number is the idle cycles (index 0 is the word "cpu")
                // Last 3 values relate to virtualization and should be 0 on android
                // TODO: the 5th number is the iowait, this should probably be ignored but i haven't in earlier versions
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

                if (prevTotalCycles != -1 && prevIdleCycles != -1) {
                    double usage = 1 - (idleCycles - prevIdleCycles) / (double) (totalCycles - prevTotalCycles);
                    result = Double.toString(usage);
                }
                prevTotalCycles = totalCycles;
                prevIdleCycles = idleCycles;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }
}
