package com.lenss.yzeng.wifilogger.wifi;

import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;

import com.lenss.yzeng.wifilogger.util.EnumUtils;
import com.lenss.yzeng.wifilogger.wifi.FrequencyPredicate;
import com.lenss.yzeng.wifilogger.wifi.model.WiFiBand;
import com.lenss.yzeng.wifilogger.wifi.model.WiFiWidth;

import java.lang.reflect.Field;

/**
 * Created by yukun on 3/21/2018.
 */

public class WifiCalc {
    enum Fields {
        centerFreq0,
        //        centerFreq1,
        channelWidth
    }
//    static WiFiBand getWiFiBand(@NonNull){
//        return EnumUtils.find(WiFiBand.class, new FrequencyPredicate(primaryFrequency), WiFiBand.GHZ2);
//    }
    public static WiFiWidth getWiFiWidth(@NonNull ScanResult scanResult) {
        try {
            return EnumUtils.find(WiFiWidth.class, getFieldValue(scanResult, Fields.channelWidth), WiFiWidth.MHZ_20);
        } catch (Exception e) {
            return WiFiWidth.MHZ_20;
        }
    }

    public static int getCenterFrequency(@NonNull ScanResult scanResult, @NonNull WiFiWidth wiFiWidth) {
        try {
            int centerFrequency = getFieldValue(scanResult, Fields.centerFreq0);
            if (centerFrequency == 0) {
                centerFrequency = scanResult.frequency;
            } else if (isExtensionFrequency(scanResult, wiFiWidth, centerFrequency)) {
                centerFrequency = (centerFrequency + scanResult.frequency) / 2;
            }
            return centerFrequency;
        } catch (Exception e) {
            return scanResult.frequency;
        }
    }

    public static boolean isExtensionFrequency(@NonNull ScanResult scanResult, @NonNull WiFiWidth wiFiWidth, int centerFrequency) {
        return WiFiWidth.MHZ_40.equals(wiFiWidth) && Math.abs(scanResult.frequency - centerFrequency) >= WiFiWidth.MHZ_40.getFrequencyWidthHalf();
    }

    public static int getFieldValue(@NonNull ScanResult scanResult, @NonNull Fields field) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = scanResult.getClass().getDeclaredField(field.name());
        return (int) declaredField.get(scanResult);
    }
}
