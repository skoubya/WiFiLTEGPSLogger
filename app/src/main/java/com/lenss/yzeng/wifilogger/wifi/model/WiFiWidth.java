package com.lenss.yzeng.wifilogger.wifi.model;

/**
 * Created by yukun on 3/21/2018.
 */

public enum WiFiWidth {
    MHZ_20(20),
    MHZ_40(40),
    MHZ_80(80),
    MHZ_160(160),
    MHZ_80_PLUS(80); // should be two 80 and 80 - feature support

    private final int frequencyWidth;
    private final int frequencyWidthHalf;

    WiFiWidth(int frequencyWidth) {
        this.frequencyWidth = frequencyWidth;
        this.frequencyWidthHalf = frequencyWidth / 2;
    }

    public int getFrequencyWidth() {
        return frequencyWidth;
    }

    public int getFrequencyWidthHalf() {
        return frequencyWidthHalf;
    }
}
