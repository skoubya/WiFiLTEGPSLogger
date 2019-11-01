package com.lenss.yzeng.wifilogger;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.lenss.yzeng.wifilogger.util.Utils;
import com.lenss.yzeng.wifilogger.wifi.WiFiDetailClosure;
import com.lenss.yzeng.wifilogger.wifi.WifiCalc;
import com.lenss.yzeng.wifilogger.wifi.model.WiFiDetail;
import com.lenss.yzeng.wifilogger.wifi.model.WiFiSignal;
import com.lenss.yzeng.wifilogger.wifi.model.WiFiWidth;

import org.apache.commons.collections4.IterableUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;

/**
 * Created by yukun on 3/21/2018.
 */

public class WiFiLogService extends Service {
    private WifiManager wifiManager = null;

    private FileOutputStream fout = null;
    private OutputStreamWriter out = null;
    String fileName = "wifi.log";
    String filePath = "/distressnet/MStorm/WifiLTEGPSLogger/";
    private BroadcastReceiver wifiScanReceiver = null;
    private int interval=2000;
    private Thread logTh;

    public class WifiLogger extends Thread{
        @Override
        public void run() {
            while (!this.isInterrupted()){
                performWiFiScan();
                try{
                    sleep(interval);
                }catch (InterruptedException e){
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Notification notification = new Notification();
        //startForeground(101, notification);
        Bundle extras=intent.getExtras();
        interval=Integer.valueOf(extras.get("interval").toString());
        logTh = new WifiLogger();
        logTh.start();

        // Start this service as foreground service
        Notification.Builder builder = new Notification.Builder (this.getApplicationContext());
        Intent nfIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_wifi_logger))
                .setContentTitle("WiFi logger Is Running")
                .setSmallIcon(R.drawable.ic_wifi_logger)
                .setContentText("WiFi logger Is Running")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        startForeground(103, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        this.wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        fout = Utils.setupFile(this,filePath, fileName);
        out = new OutputStreamWriter(fout);

        try {
            out.append("\n\n\n==============" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()) + "==================\n");
            out.flush();
            fout.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private List<WiFiDetail> resultToDetails(List<ScanResult> scanResults){
        List<WiFiDetail> wiFiDetails = new ArrayList<WiFiDetail>();
        for (int i = 0; i < scanResults.size(); i ++){
            String bssid = scanResults.get(i).BSSID;
            String ssid = scanResults.get(i).SSID;
            String sigStrength = String.valueOf(scanResults.get(i).level);
            WiFiWidth wifiWidth = WifiCalc.getWiFiWidth(scanResults.get(i));
            String capabilities = scanResults.get(i).capabilities;
            int centerFreq = WifiCalc.getCenterFrequency(scanResults.get(i), wifiWidth);
            WiFiSignal wiFiSignal = new WiFiSignal(scanResults.get(i).frequency, centerFreq, wifiWidth, Integer.valueOf(sigStrength));
            WiFiDetail detail = new WiFiDetail(ssid, bssid, capabilities, wiFiSignal);
            wiFiDetails.add(detail);
        }
        return  wiFiDetails;
    }

    public void performWiFiScan() {
        //List<ScanResult> scanResults = Collections.emptyList();
        //WifiInfo wifiInfo = null;
        //List<WifiConfiguration> configuredNetworks = null;
        try {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }

            wifiManager.startScan();
            // Registering Wifi Receiver
            wifiScanReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context c, Intent intent) {
                    retrieveResults();
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);
//            wifiInfo = wifiManager.getConnectionInfo();
//            configuredNetworks = wifiManager.getConfiguredNetworks();
        } catch (Exception e) {
            // critical error: set to no results and do not die
        }
    }

    public void retrieveResults(){
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            //do your check here
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this.getBaseContext().getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permission denied!");
                    return;
                }
            }
        }
        List<ScanResult> scanResults = wifiManager.getScanResults();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
        List<WiFiDetail> wifiDetails = resultToDetails(scanResults);
        final StringBuilder result = new StringBuilder();
        result.append(String.format(Locale.ENGLISH,"Time Stamp|SSID|Strength|Primary Frequency|Distance%n"));
        IterableUtils.forEach(wifiDetails, new WiFiDetailClosure(timestamp, result));
        result.append("===================================================\n");
        result.append(String.format(Locale.ENGLISH,"Time Stamp|SSID|Strength%n"));
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        result.append(timestamp + "|" + wifiInfo.getSSID() + "|" + wifiInfo.getRssi() + "\n");
        String wifiHistData = (result.toString() + "\n");
        // to prevent string overflow, set a max string length of 4MB
        try{
            out.append(wifiHistData);
            out.flush();
            fout.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
        Toast.makeText(this, "wifi info logged into " + fileName, Toast.LENGTH_SHORT);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //if(logTh==null){
        //    this.logTh.interrupt();
        //}
        try {
            getApplicationContext().unregisterReceiver(wifiScanReceiver);
            this.fout.close();
            this.out.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
