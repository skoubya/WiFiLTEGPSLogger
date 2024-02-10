package com.example.system_stats;

import android.os.Build;

import com.example.system_stats.util.Utils;

import java.io.IOException;

public class Stats {
    public static long[] getCPUCycles(Process rootProc){
        String[] COMMAND = {LogConstants.CAT_PATH, LogConstants.CPU_FILE};

        //TODO: not sure which SDK
        boolean needRoot = Build.VERSION.SDK_INT >= 26;

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

                long[] cycles = new long[2];
                cycles[0] = idleCycles;
                cycles[1] = totalCycles;
                return cycles;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public static double getCPUPercentage(long idleCycles, long totalCycles, long prevIdleCycles, long prevTotalCycles) {
        return 1 - (idleCycles - prevIdleCycles) / (double) (totalCycles - prevTotalCycles);
    }

    public static int getNumCpu(Process rootProc){
        String[] COMMAND = {LogConstants.CAT_PATH, LogConstants.CPU_INFO_FILE};
        // TODO(askouby): not sure if need root to access /proc/cpuinfo
        boolean needRoot = false;

        try{
            String row = Utils.searchCommandOutput(COMMAND, LogConstants.CPU_COUNT_SEARCH, needRoot, rootProc);
            String[] vals = row.split(":");

            boolean cantAccess = row.isEmpty() || vals.length != 2;

            if(!cantAccess) {

                try{
                    return Integer.parseInt(vals[1].replaceAll("\\s", ""));
                }
                catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return 0;
    }
}
