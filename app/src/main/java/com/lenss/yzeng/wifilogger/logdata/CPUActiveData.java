package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.os.Build;

import com.example.system_stats.LogConstants;
import com.lenss.yzeng.wifilogger.LogService;
import com.example.system_stats.util.Utils;

import java.io.IOException;

/* Retrieves the number of active cycles */
public class CPUActiveData extends LogService.LogData {
    private static String[] COMMAND = {LogConstants.CAT_PATH, LogConstants.CPU_FILE};
    private long prevActiveCycles;

    public CPUActiveData(String name, Context context, Process rootProc){
        super(name,context, rootProc);
        prevActiveCycles = -1;
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
                long activeCycles = 0;
                int[] activeIndices = {1,2,3,6,7};
                //4th number is the idle cycles (index 0 is the word "cpu")
                // Last 3 values relate to virtualization and should be 0 on android
                // the 5th number is the iowait, this should be ignored
                for(int index : activeIndices){
                    try{
                        activeCycles += Integer.parseInt(vals[index]);
                    }
                    catch(NumberFormatException e){
                        //just let pass
                    }
                }

                if (prevActiveCycles != -1) {
                    long active = activeCycles - prevActiveCycles;
                    result = Double.toString(active);
                }
                prevActiveCycles = activeCycles;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }
}
