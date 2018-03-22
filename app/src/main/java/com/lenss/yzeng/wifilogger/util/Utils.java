package com.lenss.yzeng.wifilogger.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by yukun on 3/21/2018.
 */

public class Utils {
    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }

    public static FileOutputStream setupFile(Context context, String filePath, String fileName){
//        String state = Environment.getExternalStorageState();
//        if (!Environment.MEDIA_MOUNTED.equals(state)) {
//            Toast.makeText(this, "no external storage", Toast.LENGTH_SHORT);
//            return null;
//        }
        if (Build.VERSION.SDK_INT >= 23) {
            //do your check here

            int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                System.out.println("no permission for writing files");
                //ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1 );
                return  null;
            }
        }
        // Get the directory for the user's public pictures directory.
        final File path = Environment.getExternalStoragePublicDirectory
                (
                        //Environment.DIRECTORY_PICTURES
                        filePath
                );

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        File file = new File(path, fileName);
        FileOutputStream fOut = null;

        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);

            System.out.println("created file out put stream");
        }catch(Exception e){
            e.printStackTrace();
        }
        return fOut;
    }

//    public void wifiScan(Context context){
//
//        WifiManager wifiManager = c;
//        List<ScanResult> scanResults = Collections.emptyList();
//        WifiInfo wifiInfo = null;
//        List<WifiConfiguration> configuredNetworks = null;
//        try {
//            if (!wifiManager.isWifiEnabled()) {
//                wifiManager.setWifiEnabled(true);
//            }
//            if (wifiManager.startScan()) {
//                scanResults = wifiManager.getScanResults();
//            }
//            wifiInfo = wifiManager.getConnectionInfo();
//            configuredNetworks = wifiManager.getConfiguredNetworks();
//        } catch (Exception e) {
//            // critical error: set to no results and do not die
//        }
//        cache.add(scanResults);
//        wiFiData = transformer.transformToWiFiData(cache.getScanResults(), wifiInfo, configuredNetworks);
//
//        List<WiFiDetail> wifiDetails = wiFiData.getWiFiDetails();
//        String timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
//
//        final StringBuilder result = new StringBuilder();
//        result.append(
//                String.format(Locale.ENGLISH,
//                        "Time Stamp|SSID|BSSID|Strength|Primary Channel|Primary Frequency|Center Channel|Center Frequency|Width (Range)|Distance|Security%n"));
//        IterableUtils.forEach(wifiDetails, new WiFiDetailClosure(timestamp, result));
//
//        wifiHistData += (result.toString() + "\n");
//        // to prevent string overflow, set a max string length of 4MB
//        if (out == null)
//            out = new OutputStreamWriter(fOut);
//        try{
//            out.append(wifiHistData);
//            out.flush();
//
//            fOut.flush();
//            wifiHistData = "";
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        Toast.makeText(this.context, "wifi info logged into " + fileName, Toast.LENGTH_SHORT);
//    }
}
