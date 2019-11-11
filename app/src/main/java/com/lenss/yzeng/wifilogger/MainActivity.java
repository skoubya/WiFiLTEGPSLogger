package com.lenss.yzeng.wifilogger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lenss.yzeng.wifilogger.logdata.BatteryData;
import com.lenss.yzeng.wifilogger.logdata.CPUData;
import com.lenss.yzeng.wifilogger.logdata.CtxtData;
import com.lenss.yzeng.wifilogger.logdata.LTERPData;
import com.lenss.yzeng.wifilogger.logdata.ScreenData;
import com.lenss.yzeng.wifilogger.util.Utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    private Button startLoggingBtn, stopLoggingBtn;
    private Button  setFreqBtn;
    private TextView intervalTextView;
    private TextView intervalEditView;
    private TextView mLog = null;

    private static final int PING_LOG_CLEAR = 0;
    private static final int PING_LOG_APPEND = 1;

    private String pingLogName = "ping.log";
    private String loggerFolder = "/distressnet/MStorm/WifiLTEGPSLogger/";

    private CheckboxData[] checkboxes = {
            new CheckboxData(R.id.batBox, "bat", BatteryData.class),
            new CheckboxData(R.id.lteRpBox, "lte_rp", LTERPData.class),
            new CheckboxData(R.id.cpuBox, "cpu", CPUData.class),
            new CheckboxData(R.id.ctxtBox, "ctxt", CtxtData.class),
            new CheckboxData(R.id.scrnBox, "scrn", ScreenData.class)};

    private class CheckboxData{
        private int checkboxId;
        private String logDataName;
        private Class logDataClass;

        CheckboxData(int checkboxId, String logDataName, Class logDataClass){
            this.checkboxId = checkboxId;
            this.logDataName = logDataName;
            if(LogService.LogData.class.isAssignableFrom(logDataClass)) {
                this.logDataClass = logDataClass;
            }
            else{
                this.logDataClass = null;
                System.err.println("Class is not a subclass of LogData");
            }
        }

        int getCheckboxId(){
            return checkboxId;
        }

        String getLogDataName(){
            return logDataName;
        }

        Class getLogDataClass(){
            return logDataClass;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                                    Manifest.permission.INTERNET},1 );

        // Default select all checkboxes
        //TODO: Have select all/ deselect all options
        for(CheckboxData chckData : checkboxes) {
            CheckBox box = findViewById(chckData.getCheckboxId());
            box.setChecked(true);
        }

        startLoggingBtn = (Button)findViewById(R.id.startLoggingBtn);
        stopLoggingBtn = (Button)findViewById(R.id.stopLoggingBtn);
        stopLoggingBtn.setEnabled(false);

        startLoggingBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "Starting logging", Toast.LENGTH_SHORT).show();
                Intent serviceIntent=new Intent(MainActivity.this, LogService.class);
                serviceIntent.putExtra("interval", Integer.valueOf(intervalTextView.getText().toString()));
                LogService.LogDataList logDataList = new LogService.LogDataList();

                //Check the checkboxes
                for(CheckboxData chckData : checkboxes){
                    CheckBox box = findViewById(chckData.getCheckboxId());
                    if(box.isChecked()){
                        try {
                            Class clazz = chckData.getLogDataClass();
                            Constructor constructor = clazz.getConstructor(String.class, Context.class);
                            Object obj = constructor.newInstance(chckData.getLogDataName(), null);
                            logDataList.add((LogService.LogData)obj);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                
                serviceIntent.putExtra("log_data", (Parcelable)logDataList);
                startService(serviceIntent);
                startLoggingBtn.setEnabled(false);
                stopLoggingBtn.setEnabled(true);
            }
        });

        stopLoggingBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "Stopping logging", Toast.LENGTH_SHORT).show();
                stopService(new Intent(MainActivity.this, LogService.class));
                stopLoggingBtn.setEnabled(false);
                startLoggingBtn.setEnabled(true);
            }
        });

        intervalTextView=(TextView)findViewById(R.id.intervalTextView);
        intervalEditView=(EditText)findViewById(R.id.editFreqText);
        setFreqBtn=(Button)findViewById(R.id.setFreqBtn);
        setFreqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intervalTextView.setText(intervalEditView.getText());
                Toast.makeText(MainActivity.this, "logging interval set to "+intervalTextView.getText(), Toast.LENGTH_SHORT);
            }
        });

        mLog = findViewById(R.id.log);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    class Ping implements Runnable {
        String host;
        String count;

        public Ping(String host, String count){
            this.host = host;
            this.count = count;
        }

        public void run() {
            FileOutputStream fout = null;
            OutputStreamWriter writer = null;
            try {
                fout = Utils.setupFile(MainActivity.this, loggerFolder, pingLogName);
                writer = new OutputStreamWriter(fout);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.obtainMessage(PING_LOG_CLEAR,"").sendToTarget();

            try {
                writer.append("\n\n==============" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()) + "==================\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            String inputLine;
            try {
                Process process = Runtime.getRuntime().exec("/system/bin/ping -c " + count + " " + host);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((inputLine = reader.readLine()) != null) {
                    mHandler.obtainMessage(PING_LOG_APPEND,inputLine).sendToTarget();
                    writer.append(inputLine+"\n");
                    writer.flush();
                    fout.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                writer.close();
                fout.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case PING_LOG_CLEAR:
                    mLog.setText("");
                case PING_LOG_APPEND:
                    mLog.append("\n"+msg.obj.toString());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
}
