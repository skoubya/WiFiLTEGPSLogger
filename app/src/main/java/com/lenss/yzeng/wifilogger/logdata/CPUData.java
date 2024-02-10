package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;

import com.example.system_stats.Stats;
import com.lenss.yzeng.wifilogger.LogService;

/* Retrieves the percent usage of CPU */
public class CPUData extends LogService.LogData {
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

        long[] cycles = Stats.getCPUCycles(rootProc);

        if (cycles != null){
            double percent = Stats.getCPUPercentage(cycles[0], cycles[1],
                                                    prevIdleCycles, prevTotalCycles);
            result = Double.toString(percent);
            prevIdleCycles = cycles[0];
            prevTotalCycles = cycles[1];
        }

        System.out.println("CPU %: " + result);
        return result;
    }
}
