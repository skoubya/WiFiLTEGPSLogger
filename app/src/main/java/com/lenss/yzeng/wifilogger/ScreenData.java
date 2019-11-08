package com.lenss.yzeng.wifilogger;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

public class ScreenData extends LogService.LogData {
    public ScreenData(String name, Context context){
        super(name, context);
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
