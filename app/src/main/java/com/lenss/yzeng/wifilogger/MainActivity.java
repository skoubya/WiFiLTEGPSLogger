package com.lenss.yzeng.wifilogger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener2 {

    private Button startSenseBtn, startWifiBtn, stopWifiBtn, stopSenseBtn, startLteBtn;
    private Button stopLteBtn, setPingBtn, pingBtn, setFreqBtn;
    private TextView intervalTextView, hostText, pingNumText;
    private TextView intervalEditView;
    private EditText editUrl, editPingNum;
    private static int count = 0;

    private TextView mag_data;
    private TextView acc_data;

    private static boolean isRunning = false;
    private static String acc, gyro_uncal, gyro, mag, mag_uncal, rot, game_rot;

    SensorManager manager;

    public String ping(String url, String count) {
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec(
                    "/system/bin/ping -c "+count+" "+ url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((i = reader.read(buffer)) > 0)
                output.append(buffer, 0, i);
            reader.close();

            // body.append(output.toString()+"\n");
            str = output.toString();
            // Log.d(TAG, str);
        } catch (IOException e) {
            // body.append("Error\n");
            e.printStackTrace();
        }
        return str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        hostText=findViewById(R.id.hostText);
        pingNumText=findViewById(R.id.pingNumText);
        editUrl=findViewById(R.id.editUrlText);
        editPingNum=findViewById(R.id.editNumText);
        setPingBtn=findViewById(R.id.setPingBtn);
        pingBtn=findViewById(R.id.pingBtn);
        setPingBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String pingHost=editUrl.getText().toString(),
                pingNum=editPingNum.getText().toString();
                Toast.makeText(MainActivity.this, "ping host: "+pingHost+" for "+pingNum+" times", Toast.LENGTH_SHORT);
                hostText.setText(pingHost);
                pingNumText.setText(pingNum);
            }
        });
        pingBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(MainActivity.this, "pinging!!!", Toast.LENGTH_SHORT);
                ping(hostText.getText().toString(), pingNumText.getText().toString());
            }
        });

        startWifiBtn = (Button)findViewById(R.id.startWifiBtn);
        stopWifiBtn = (Button)findViewById(R.id.stopWifiBtn);
        stopWifiBtn.setEnabled(false);

        startSenseBtn = (Button)findViewById(R.id.startSenseBtn);
        stopSenseBtn = (Button)findViewById(R.id.stopSenseBtn);
        stopSenseBtn.setEnabled(false);

        startLteBtn = (Button)findViewById(R.id.startLteBtn);
        stopLteBtn = (Button)findViewById(R.id.stopLteBtn);
        stopLteBtn.setEnabled(false);

        setFreqBtn=(Button)findViewById(R.id.setFreqBtn);
        setFreqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intervalTextView.setText(intervalEditView.getText());
                Toast.makeText(MainActivity.this, "logging interval set to "+intervalTextView.getText(), Toast.LENGTH_SHORT);
            }
        });

        intervalTextView=(TextView)findViewById(R.id.intervalTextView);
        intervalEditView=(EditText)findViewById(R.id.editFreqText);

        startWifiBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "entering wifi logging thread!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "stopping wifi logging thread", Toast.LENGTH_SHORT).show();
                stopService(new Intent(MainActivity.this, WiFiLogService.class));
                stopWifiBtn.setEnabled(false);
                startWifiBtn.setEnabled(true);
            }
        });

        startSenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start a service
                Toast.makeText(MainActivity.this, "entering sensor logging thread!", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, SensorLogService.class));
                startSenseBtn.setEnabled(false);
                stopSenseBtn.setEnabled(true);
            }
        });

        stopSenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "stopping the sensor service thread", Toast.LENGTH_SHORT).show();
                stopService(new Intent(MainActivity.this, SensorLogService.class));
                stopSenseBtn.setEnabled(false);
                startSenseBtn.setEnabled(true);
            }
        });

        startLteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "starting lte logging service thread", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "stopping lte logging service", Toast.LENGTH_SHORT).show();
                stopService(new Intent(MainActivity.this, LteLogService.class));
                stopLteBtn.setEnabled(false);
                startLteBtn.setEnabled(true);
            }
        });
    }

    public void updateText(TextView textView, String newText){
        textView.setText(newText);
    }

    @Override
    public void onSensorChanged(SensorEvent evt) {

        if(isRunning) {
            try {
                switch(evt.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        acc = String.format("%d; ACC; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f);
                        updateText(acc_data, acc);
                        break;
                    case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                        gyro_uncal = String.format("%d; GYRO_UN; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], evt.values[4], evt.values[5]);
                        //((TextView)findViewById(R.id.gyro_uncal_data)).setText(gyro_uncal);
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        gyro = String.format("%d; GYRO; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f);
                        //((TextView)findViewById(R.id.gyro_data)).setText(gyro);
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        mag = String.format("%d; MAG; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f);
                        updateText(mag_data, mag);
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                        mag_uncal = String.format("%d; MAG_UN; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], 0.f, 0.f, 0.f);
                        //((TextView)findViewById(R.id.mag_uncal_data)).setText(mag_uncal);
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        rot = String.format("%d; ROT; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], 0.f, 0.f);
                        //((TextView)findViewById(R.id.rot_data)).setText(rot);
                        break;
                    case Sensor.TYPE_GAME_ROTATION_VECTOR:
                        game_rot = String.format("%d; GAME_ROT; %f; %f; %f; %f; %f; %f\n", evt.timestamp, evt.values[0], evt.values[1], evt.values[2], evt.values[3], 0.f, 0.f);
                        //((TextView)findViewById(R.id.rot_game_data)).setText(game_rot);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
    public void onFlushCompleted(Sensor sensor) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
