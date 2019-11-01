package com.lenss.yzeng.wifilogger;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.lenss.yzeng.wifilogger.util.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GPSLogService extends Service implements LocationListener {
    LocationManager locationManager = null;
    String fileName = "gps.log";
    String filePath = "/distressnet/MStorm/WifiLTEGPSLogger/";
    FileOutputStream fout = null;
    OutputStreamWriter writer = null;
    int interval=2000;

    @Override
    public void onCreate(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try{
            fout = Utils.setupFile(this,filePath, fileName);
            writer = new OutputStreamWriter(fout);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            writer.append("\n\n\n==============" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()) + "==================\n");
            writer.flush();
            fout.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            //do your check here
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this.getBaseContext().getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permission denied!");
                    return super.onStartCommand(intent, flags, startId);
                }
            }
        }

        Bundle extras=intent.getExtras();
        interval=Integer.valueOf(extras.get("interval").toString());
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, 1, GPSLogService.this);

        // Start this service as foreground service
        Notification.Builder builder = new Notification.Builder (this.getApplicationContext());
        Intent nfIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_gps_logger))
                .setContentTitle("GPS logger Is Running")
                .setSmallIcon(R.drawable.ic_gps_logger)
                .setContentText("GPS logger Is Running")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        startForeground(105, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            this.writer.close();
            writer = null;
            this.fout.close();
            fout = null;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
        if(writer!=null) {
            try {
                writer.append(String.format("%s; LAT; %f; LONG; %f; SPEED; %f; ALT; %f\n", timestamp,
                        location.getLatitude(), location.getLongitude(), location.getSpeed(), location.getAltitude()));
                writer.flush();
                fout.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
