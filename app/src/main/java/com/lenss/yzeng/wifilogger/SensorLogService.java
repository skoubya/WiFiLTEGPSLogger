package com.lenss.yzeng.wifilogger;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.lenss.yzeng.wifilogger.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yukun on 3/22/2018.
 */

public class SensorLogService extends Service implements SensorEventListener2, LocationListener{
    SensorManager manager = null;
    String fileName = "sensor_log_";
    String filePath = "/distressnet/MStorm/WifiLTEGPSLogger/";
    FileOutputStream fout = null;
    OutputStreamWriter writer = null;
    LocationManager locationManager = null;
    @Override
    public void onCreate(){
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileName = fileName + timestamp + ".log";
        try{
            fout = Utils.setupFile(this,filePath, fileName);
            writer = new OutputStreamWriter(fout);
        }catch (Exception e){
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, SensorLogService.this);

        manager.registerListener(SensorLogService.this,
                manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(SensorLogService.this,
                manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(SensorLogService.this,
                manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED), SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(SensorLogService.this,
                manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(SensorLogService.this,
                manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED), SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(SensorLogService.this,
                manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(SensorLogService.this,
                manager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
        // new sensing capability
        manager.registerListener(SensorLogService.this,
                manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(SensorLogService.this,
                manager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(SensorLogService.this,
                manager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener(SensorLogService.this,
                manager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
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
    public void onSensorChanged(SensorEvent evt) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //long real_stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String timestamp = Utils.convertTime(real_stamp);
        if(writer!=null){
            try {
                switch(evt.sensor.getType()) {
//                    case Sensor.TYPE_ACCELEROMETER:
//                        writer.write(String.format("%s; ACC; %f; %f; %f\n", timestamp, evt.values[0], evt.values[1], evt.values[2]));
//                        break;
//                    case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
//                        writer.write(String.format("%s; GYRO_UN; %f; %f; %f; %f; %f; %f\n", timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], evt.values[4], evt.values[5]));
//                        break;
//                    case Sensor.TYPE_GYROSCOPE:
//                        writer.write(String.format("%s; GYRO; %f; %f; %f; %f; %f; %f\n", timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
//                        break;
//                    case Sensor.TYPE_MAGNETIC_FIELD:
//                        writer.write(String.format("%s; MAG; %f; %f; %f; %f; %f; %f\n", timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
//                        break;
//                    case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
//                        writer.write(String.format("%s; MAG_UN; %f; %f; %f; %f; %f; %f\n", timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f));
//                        break;
//                    case Sensor.TYPE_ROTATION_VECTOR:
//                        writer.write(String.format("%s; ROT; %f; %f; %f; %f; %f; %f\n", timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], 0.f, 0.f));
//                        break;
//                    case Sensor.TYPE_GAME_ROTATION_VECTOR:
//                        writer.write(String.format("%s; GAME_ROT; %f; %f; %f; %f; %f; %f\n", timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], 0.f, 0.f));
//                        break;
//                    case  Sensor.TYPE_AMBIENT_TEMPERATURE:
//                        writer.write(String.format("%s; TEMP; %f; \n", timestamp, evt.values[0]));
//                        break;
                    case  Sensor.TYPE_LIGHT:
                        writer.write(String.format("%s; LIGHT; %f; \n", timestamp, evt.values[0]));
                        break;
//                    case  Sensor.TYPE_PRESSURE:
//                        writer.write(String.format("%s; PRESSURE; %f;\n", timestamp, evt.values[0]));
//                        break;
//                    case  Sensor.TYPE_PROXIMITY:
//                        writer.write(String.format("%s; PROXIMITY; %f;\n", timestamp, evt.values[0]));
//                        break;
                }
                writer.flush();
                fout.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {

    }

    @Override
    public void onLocationChanged(Location location) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if(writer!=null) {
            try {
                writer.write(String.format("%s; LAT; %f; LONG; %f; SPEED; %f; ALT; %f\n", timestamp, location.getLatitude(), location.getLongitude(),
                        location.getSpeed(), location.getAltitude()));
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
