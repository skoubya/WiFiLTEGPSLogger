package com.lenss.yzeng.wifilogger;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.lenss.yzeng.wifilogger.util.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.sleep;

public class LteLogService extends Service {
    TelephonyManager tm=null;
    String fileName = "lte.log";
    String filePath = "/distressnet/MStorm/WifiLTEGPSLogger/";
    FileOutputStream fout=null;
    OutputStreamWriter out=null;
    int interval=2000;
    Thread logTh=null;

    public class LteLogger extends Thread{
        @Override
        public void run() {
            while (!this.isInterrupted()){
                int dbm=collectLteLog();
                String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
                try{
                    out.append(timestamp+"|"+dbm+"\n");
                    out.flush();
                    fout.flush();
                }catch (IOException e){
                    e.printStackTrace();
                }
                try{
                    sleep(interval);
                }catch (Exception e){
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        this.tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        this.fout = Utils.setupFile(this,filePath, fileName);
        this.out = new OutputStreamWriter(fout);
        try {
            out.append("\n\n\n==============" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()) + "==================\n");
            out.flush();
            fout.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Notification notification = new Notification();
        //startForeground(101, notification);
        Bundle extras=intent.getExtras();
        interval=Integer.valueOf(extras.get("interval").toString());
        logTh = new LteLogger();
        logTh.start();

        // Start this service as foreground service
        Notification.Builder builder = new Notification.Builder (this.getApplicationContext());
        Intent nfIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_lte_logger))
                .setContentTitle("LTE logger Is Running")
                .setSmallIcon(R.drawable.ic_lte_logger)
                .setContentText("LTE logger Is Running")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        startForeground(104, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    public int collectLteLog(){
        int dbm=Integer.MIN_VALUE;
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
            //do your check here
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this.getBaseContext().getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permission denied!");
                    return dbm;
                }
            }
        }
        if(tm!=null){
            List<CellInfo> cellInfoList= tm.getAllCellInfo();
            if(cellInfoList!=null) {
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoLte) {
                        // cast to CellInfoLte and call all the CellInfoLte methods you need
                        dbm = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();
                    }
                }
            }
        }

        return dbm;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){
        if(this.logTh!=null){
            this.logTh.interrupt();
        }
        try {
            fout.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
