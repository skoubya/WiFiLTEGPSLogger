package com.lenss.yzeng.wifilogger.wifi.model;

import android.support.annotation.NonNull;

import com.lenss.yzeng.wifilogger.R;
import com.lenss.yzeng.wifilogger.wifi.WiFiChannels;
import com.lenss.yzeng.wifilogger.wifi.WiFiChannelsGHZ2;
import com.lenss.yzeng.wifilogger.wifi.WiFiChannelsGHZ5;

/**
 * Created by yukun on 3/21/2018.
 */

public enum WiFiBand {
    GHZ2(R.string.wifi_band_2ghz, new WiFiChannelsGHZ2()),
    GHZ5(R.string.wifi_band_5ghz, new WiFiChannelsGHZ5());

    private final int textResource;
    private final WiFiChannels wiFiChannels;

    WiFiBand(int textResource, @NonNull WiFiChannels wiFiChannels) {
        this.textResource = textResource;
        this.wiFiChannels = wiFiChannels;
    }

    public int getTextResource() {
        return textResource;
    }

    @NonNull
    public WiFiBand toggle() {
        return isGHZ5() ? WiFiBand.GHZ2 : WiFiBand.GHZ5;
    }

    public boolean isGHZ5() {
        return WiFiBand.GHZ5.equals(this);
    }

    @NonNull
    public WiFiChannels getWiFiChannels() {
        return wiFiChannels;
    }
}
