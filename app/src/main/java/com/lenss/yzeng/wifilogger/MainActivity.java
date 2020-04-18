package com.lenss.yzeng.wifilogger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.lenss.yzeng.wifilogger.logdata.LTERBData;
import com.lenss.yzeng.wifilogger.logdata.LTERData;
import com.lenss.yzeng.wifilogger.logdata.LTERPData;
import com.lenss.yzeng.wifilogger.logdata.LTETBData;
import com.lenss.yzeng.wifilogger.logdata.LTETData;
import com.lenss.yzeng.wifilogger.logdata.MemData;
import com.lenss.yzeng.wifilogger.logdata.ScreenData;
import com.lenss.yzeng.wifilogger.logdata.ScreenOnData;
import com.lenss.yzeng.wifilogger.logdata.WifiFData;
import com.lenss.yzeng.wifilogger.logdata.WifiRBData;
import com.lenss.yzeng.wifilogger.logdata.WifiRData;
import com.lenss.yzeng.wifilogger.logdata.WifiRPData;
import com.lenss.yzeng.wifilogger.logdata.WifiTBData;
import com.lenss.yzeng.wifilogger.logdata.WifiTData;

import java.lang.reflect.Constructor;

public class MainActivity extends AppCompatActivity {

    private Button startLoggingBtn, stopLoggingBtn;
    private Button setFreqBtn;
    private TextView intervalTextView;
    private TextView intervalEditView;

    private String loggerFolder = "/distressnet/MStorm/WifiLTEGPSLogger/";

    private CheckboxData[] checkboxes = {
            new CheckboxData(R.id.batBox, "bat", BatteryData.class),
            new CheckboxData(R.id.lteRpBox, "lte_rp", LTERPData.class),
            new CheckboxData(R.id.cpuBox, "cpu", CPUData.class),
            new CheckboxData(R.id.ctxtBox, "ctxt", CtxtData.class),
            new CheckboxData(R.id.scrnBox, "scrn", ScreenData.class),
            new CheckboxData(R.id.wifiRBox, "wifi_r", WifiRData.class),
            new CheckboxData(R.id.wifiTBox, "wifi_t", WifiTData.class),
            new CheckboxData(R.id.memBox, "mem", MemData.class),
            new CheckboxData(R.id.wifiRPBox, "wifi_rp", WifiRPData.class),
            new CheckboxData(R.id.wifiFBox, "wifi_f", WifiFData.class),
            new CheckboxData(R.id.lteRBox, "lte_r", LTERData.class),
            new CheckboxData(R.id.lteTBox, "lte_t", LTETData.class),
            new CheckboxData(R.id.wifiRBBox, "wifi_rb", WifiRBData.class),
            new CheckboxData(R.id.wifiTBBox, "wifi_tb", WifiTBData.class),
            new CheckboxData(R.id.lteRBBox, "lte_rb", LTERBData.class),
            new CheckboxData(R.id.lteTBBox, "lte_tb", LTETBData.class),
            new CheckboxData(R.id.scrnOnBox, "scrn_on", ScreenOnData.class)};

    private class CheckboxData {
        private int checkboxId;
        private String logDataName;
        private Class logDataClass;

        CheckboxData(int checkboxId, String logDataName, Class logDataClass) {
            this.checkboxId = checkboxId;
            this.logDataName = logDataName;
            if (LogService.LogData.class.isAssignableFrom(logDataClass)) {
                this.logDataClass = logDataClass;
            } else {
                this.logDataClass = null;
                System.err.println("Class is not a subclass of LogData");
            }
        }

        int getCheckboxId() {
            return checkboxId;
        }

        String getLogDataName() {
            return logDataName;
        }

        Class getLogDataClass() {
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
                Manifest.permission.INTERNET}, 1);

        // Default select all checkboxes
        //TODO: Have select all/ deselect all options
        for (CheckboxData chckData : checkboxes) {
            CheckBox box = findViewById(chckData.getCheckboxId());
            box.setChecked(true);
        }

        startLoggingBtn = (Button) findViewById(R.id.startLoggingBtn);
        stopLoggingBtn = (Button) findViewById(R.id.stopLoggingBtn);
        stopLoggingBtn.setEnabled(false);

        startLoggingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Starting logging", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(MainActivity.this, LogService.class);
                serviceIntent.putExtra("interval", Integer.valueOf(intervalTextView.getText().toString()));
                LogService.LogDataList logDataList = new LogService.LogDataList();

                //Check the checkboxes
                for (CheckboxData chckData : checkboxes) {
                    CheckBox box = findViewById(chckData.getCheckboxId());
                    if (box.isChecked()) {
                        try {
                            Class clazz = chckData.getLogDataClass();
                            Constructor constructor = clazz.getConstructor(String.class, Context.class, Process.class);
                            Object obj = constructor.newInstance(chckData.getLogDataName(), null, null);
                            logDataList.add((LogService.LogData) obj);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                serviceIntent.putExtra("log_data", (Parcelable) logDataList);
                startService(serviceIntent);
                startLoggingBtn.setEnabled(false);
                stopLoggingBtn.setEnabled(true);
            }
        });

        stopLoggingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Stopping logging", Toast.LENGTH_SHORT).show();
                stopService(new Intent(MainActivity.this, LogService.class));
                stopLoggingBtn.setEnabled(false);
                startLoggingBtn.setEnabled(true);
            }
        });

        intervalTextView = (TextView) findViewById(R.id.intervalTextView);
        intervalEditView = (EditText) findViewById(R.id.editFreqText);
        setFreqBtn = (Button) findViewById(R.id.setFreqBtn);
        setFreqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intervalTextView.setText(intervalEditView.getText());
                Toast.makeText(MainActivity.this, "logging interval set to " + intervalTextView.getText(), Toast.LENGTH_SHORT);
            }
        });

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
}
