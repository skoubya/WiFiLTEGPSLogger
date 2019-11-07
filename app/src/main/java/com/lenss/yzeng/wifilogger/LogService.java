package com.lenss.yzeng.wifilogger;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import com.lenss.yzeng.wifilogger.util.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class LogService extends Service {
    String fileName = "energy.csv"; //TODO: name based off of time?
    String filePath = "/distressnet/MStorm/WifiLTEGPSLogger/"; //TODO: Take as input
    FileOutputStream fout=null;
    OutputStreamWriter out=null;
    int interval=2000;
    Thread logTh = null;
    LogDataList logData = null; // map from value name to its retriever

    public static abstract class LogData{
        public String name;
        protected Context cntxt;

        public LogData(String name, Context context){
            this.name = name;
            this.cntxt = context;
        }

        public void setCntxt(Context context){
            this.cntxt = context;
        }

        public abstract String retrieve(); //Gets the current value for the data (NA if not available)
    }

    //TODO: Using this is probably slow
    public static class LogDataList
            extends ArrayList<LogData>
            implements Parcelable{
        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public LogDataList createFromParcel(Parcel in) {
                return new LogDataList(in);
            }

            public LogDataList[] newArray(int size) {
                return new LogDataList[size];
            }
        };
        private Context cntxt = null; //TODO: is not passed with parcel

        public LogDataList(){
            super();
        }

        public LogDataList(Parcel in){
            int size = in.readInt();

            for(int i = 0; i < size; i++){
                LogData obj;

                try {
                    String class_name = in.readString();
                    Class<?> clazz = Class.forName(class_name);
                    Constructor<?> ctor = clazz.getConstructor(String.class,Context.class);
                    obj = (LogData)ctor.newInstance(new Object[]{in.readString(),cntxt});
                }
                catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

                add(obj);
            }
        }

        public void setContext(Context context){
            this.cntxt = context;

            for(int i = 0; i < size(); i++){
                get(i).setCntxt(this.cntxt);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(size());
            for(int i = 0; i < size(); i++){
                dest.writeString(get(i).getClass().getName());
                dest.writeString(get(i).name);
            }
        }

    }

    public class EnergyLogger extends Thread{
        @Override
        public void run() {
            try {
                // Add column names
                out.append("time");
                for(int i = 0; i < logData.size(); i++) {
                    out.append(",");
                    out.append(logData.get(i).name);
                }

                out.append("\n");
                out.flush();
                fout.flush();
            } catch (IOException e){
                e.printStackTrace();
            }

            while (!this.isInterrupted()){
                try{
                    out.append(Long.toString(System.currentTimeMillis()));

                    for(int i = 0; i < logData.size(); i++) {
                        //Read and append all desired value
                        out.append(",");
                        out.append(logData.get(i).retrieve());
                    }

                    out.append("\n");
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
        System.out.println("Log service created");
        this.fout = Utils.setupFile(this,filePath, fileName);
        this.out = new OutputStreamWriter(fout);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Notification notification = new Notification();
        //startForeground(101, notification);
        Bundle extras=intent.getExtras();
        interval=Integer.valueOf(extras.get("interval").toString());
        logData = extras.getParcelable("log_data");
        logData.setContext(this.getBaseContext().getApplicationContext());

        logTh = new LogService.EnergyLogger();
        logTh.start();

        // Start this service as foreground service
        Notification.Builder builder = new Notification.Builder (this.getApplicationContext());
        Intent nfIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_file_logger))
                .setContentTitle("Logger Is Running")
                .setSmallIcon(R.drawable.ic_file_logger)
                .setContentText("Logger Is Running")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        startForeground(104, notification);

        return super.onStartCommand(intent, flags, startId);
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
