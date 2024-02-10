package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.os.Build;

import com.example.system_stats.LogConstants;
import com.lenss.yzeng.wifilogger.LogService;
import com.example.system_stats.util.Utils;

import java.io.IOException;

/* Retrieves the number of idle cycles */
public class CPUIdleData extends LogService.LogData {
    private static String[] COMMAND = {LogConstants.CAT_PATH, LogConstants.CPU_FILE};
    private long prevIdleCycles;

    public CPUIdleData(String name, Context context, Process rootProc){
        super(name,context, rootProc);
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
                long idleCycles = 0;
                int idleIndex = 4; //4th number is the idle cycles (index 0 is the word "cpu")
                try{
                    idleCycles = Integer.parseInt(vals[idleIndex]);
                }
                catch(NumberFormatException e){
                    //just let pass
                }

                if (prevIdleCycles != -1) {
                    long idle = idleCycles - prevIdleCycles;
                    result = Long.toString(idle);
                }
                prevIdleCycles = idleCycles;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }
}
