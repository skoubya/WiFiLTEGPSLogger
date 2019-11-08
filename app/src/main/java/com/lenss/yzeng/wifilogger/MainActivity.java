package com.lenss.yzeng.wifilogger;

import android.Manifest;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lenss.yzeng.wifilogger.util.Utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    private Button startWifiBtn, stopWifiBtn, startLteBtn, stopLteBtn, startGpsBtn, stopGpsBtn;
    private Button startLoggingBtn, stopLoggingBtn;
    private Button  pingBtn, setFreqBtn;
    private TextView intervalTextView;
    private TextView intervalEditView;
    private EditText editUrl, editPingNum;
    private TextView mLog = null;

    private static final int PING_LOG_CLEAR = 0;
    private static final int PING_LOG_APPEND = 1;

    private String pingLogName = "ping.log";
    private String loggerFolder = "/distressnet/MStorm/WifiLTEGPSLogger/";

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

        startWifiBtn = (Button)findViewById(R.id.startWifiBtn);
        stopWifiBtn = (Button)findViewById(R.id.stopWifiBtn);
        stopWifiBtn.setEnabled(false);

        startLteBtn = (Button)findViewById(R.id.startLteBtn);
        stopLteBtn = (Button)findViewById(R.id.stopLteBtn);
        stopLteBtn.setEnabled(false);

        startGpsBtn = (Button)findViewById(R.id.startGpsBtn);
        stopGpsBtn = (Button)findViewById(R.id.stopGpsBtn);
        stopGpsBtn.setEnabled(false);

        startLoggingBtn = (Button)findViewById(R.id.startLoggingBtn);
        stopLoggingBtn = (Button)findViewById(R.id.stopLoggingBtn);
        stopLoggingBtn.setEnabled(false);

        startWifiBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "starting wifi logging!", Toast.LENGTH_SHORT).show();
                Intent serviceIntent=new Intent(MainActivity.this, WiFiLogService.class);
                serviceIntent.putExtra("interval", Integer.valueOf(intervalTextView.getText().toString()));
                startService(serviceIntent);
                startWifiBtn.setEnabled(false);
                stopWifiBtn.setEnabled(true);
            }
        });

        stopWifiBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "stopping wifi logging", Toast.LENGTH_SHORT).show();
                stopService(new Intent(MainActivity.this, WiFiLogService.class));
                stopWifiBtn.setEnabled(false);
                startWifiBtn.setEnabled(true);
            }
        });

        startLteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "starting lte logging", Toast.LENGTH_SHORT).show();
                Intent serviceIntent=new Intent(MainActivity.this, LteLogService.class);
                serviceIntent.putExtra("interval", Integer.valueOf(intervalTextView.getText().toString()));
                startService(serviceIntent);
                startLteBtn.setEnabled(false);
                stopLteBtn.setEnabled(true);
            }
        });

        stopLteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "stopping lte logging", Toast.LENGTH_SHORT).show();
                stopService(new Intent(MainActivity.this, LteLogService.class));
                stopLteBtn.setEnabled(false);
                startLteBtn.setEnabled(true);
            }
        });

        startGpsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "starting gps logging", Toast.LENGTH_SHORT).show();
                Intent serviceIntent=new Intent(MainActivity.this, GPSLogService.class);
                serviceIntent.putExtra("interval", Integer.valueOf(intervalTextView.getText().toString()));
                startService(serviceIntent);
                startGpsBtn.setEnabled(false);
                stopGpsBtn.setEnabled(true);
            }
        });

        stopGpsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "stopping gps logging", Toast.LENGTH_SHORT).show();
                stopService(new Intent(MainActivity.this, GPSLogService.class));
                stopGpsBtn.setEnabled(false);
                startGpsBtn.setEnabled(true);
            }
        });

        startLoggingBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "Starting logging", Toast.LENGTH_SHORT).show();
                Intent serviceIntent=new Intent(MainActivity.this, LogService.class);
                serviceIntent.putExtra("interval", Integer.valueOf(intervalTextView.getText().toString()));
                LogService.LogDataList logDataList = new LogService.LogDataList();
                logDataList.add(new LTERPData("lte_rp", null));
                logDataList.add(new BatteryData("bat", null));
                logDataList.add(new ScreenData("scrn", null));
                logDataList.add(new CPUData("cpu", null));
                logDataList.add(new CtxtData("ctxt", null));
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

        editUrl=findViewById(R.id.editUrlText);
        editPingNum=findViewById(R.id.editNumText);
        pingBtn=findViewById(R.id.pingBtn);
        pingBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String pingHost=editUrl.getText().toString();
                String pingNum=editPingNum.getText().toString();
                Toast.makeText(MainActivity.this, "pinging!!!", Toast.LENGTH_SHORT);
                new Thread(new Ping(pingHost, pingNum)).start();
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
