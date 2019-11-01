package com.lenss.yzeng.wifilogger.wifi;

import android.support.annotation.NonNull;


import com.lenss.yzeng.wifilogger.wifi.model.WiFiDetail;
import com.lenss.yzeng.wifilogger.wifi.model.WiFiSignal;

import org.apache.commons.collections4.Closure;

import java.util.Locale;

/**
 * Created by yukun on 3/21/2018.
 */

public class WiFiDetailClosure implements Closure<WiFiDetail> {
    private final StringBuilder result;
    private final String timestamp;

    public WiFiDetailClosure(String timestamp, @NonNull StringBuilder result) {
        this.result = result;
        this.timestamp = timestamp;
    }

    @Override
    public void execute(WiFiDetail wiFiDetail) {
        WiFiSignal wiFiSignal = wiFiDetail.getWiFiSignal();
        result.append(String.format(Locale.ENGLISH, "%s|%s|%ddBm|%d%s|%.1fm%n",
                timestamp,
                wiFiDetail.getSSID(),
                wiFiSignal.getLevel(),
                wiFiSignal.getPrimaryFrequency(),
                WiFiSignal.FREQUENCY_UNITS,
                wiFiSignal.getDistance()));
    }
}