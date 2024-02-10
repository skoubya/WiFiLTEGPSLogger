package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;

import com.lenss.yzeng.wifilogger.LogService;
import com.example.system_stats.util.Utils;

import java.io.IOException;

/* An abstract class to retrieve a value from a row and give the difference since the last value */
public abstract class SingleDiffFileData extends LogService.LogData {
    protected String[] command;
    protected String search;
    protected int valPos; // 0 indexed position of the value (only including numbers)
    protected long prevVal;
    protected boolean needRoot;

    public SingleDiffFileData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
        prevVal = -1;
        needRoot = false;
    }

    @Override
    public String retrieve(){
        String result = "NA";

        try {
            String row = Utils.searchCommandOutput(command, search, needRoot, rootProc);
            System.out.println(row);
            String[] vals = row.split("\\s");
            for (String val : vals){
                System.out.println(val);
            }

            Long currVal = null;
            int numCount = 0;
            for(String val : vals){
                try{
                    long iVal = Long.parseLong(val); //gets 1st integer value

                    if(numCount == valPos) {
                        currVal = iVal;
                        System.out.println("Chosen value is = " + String.valueOf(iVal));
                        break;
                    }
                    numCount++;
                }
                catch(NumberFormatException e){
                    //just let pass (ignores non-integer values in indices)
                }
            }

            if(currVal != null) { //catches that failed to get value
                if (prevVal != -1) {
                    long diff = (currVal - prevVal);
                    result = Long.toString(diff);
                }
                prevVal = currVal;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Result is = " + String.valueOf(result));
        return result;
    }
}
