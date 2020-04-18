package com.lenss.yzeng.wifilogger.logdata;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.lenss.yzeng.wifilogger.LogService;

/* Retrieves the screen brightness */
public class ScreenData extends LogService.LogData {
    public ScreenData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
    }

    @Override
    public String retrieve(){
        ContentResolver cResolver = cntxt.getContentResolver();
        String brightness = "NA";

        try {
            //TODO: doesn't seem to work on the Nexus phone
            // the value is the same (82)
            brightness = Integer.toString(Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS));
        }
        catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
        }

        return brightness;
    }
}
