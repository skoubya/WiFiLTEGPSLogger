package com.lenss.yzeng.wifilogger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Thread logger;
    Button startbtn;
    Button stopbtn;
    FileOutputStream fOut = null;
    OutputStreamWriter myOutWriter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        startbtn = findViewById(R.id.startBtn);

        stopbtn = findViewById(R.id.stopBtn);
        stopbtn.setVisibility(View.GONE);

        startbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "entering the service thread!", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, WiFiLogService.class));
//                fOut = setupFile();
//                if (fOut == null){
//                    Toast.makeText(MainActivity.this, "get a null fOut!", Toast.LENGTH_SHORT);
//                    System.out.println("get a null fOut!");
//                }
//                myOutWriter = new OutputStreamWriter(fOut);
//
//                logger = new WifiLogger(myOutWriter);
//                Toast.makeText(MainActivity.this, "starting the service thread", Toast.LENGTH_SHORT).show();
//                logger.start();
                startbtn.setVisibility(View.GONE);
                stopbtn.setVisibility(View.VISIBLE);
            }
        });

        stopbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "stopping the service thread", Toast.LENGTH_SHORT).show();
                stopService(new Intent(MainActivity.this, WiFiLogService.class));
//                logger.interrupt();
//
//                try {
//                    myOutWriter.flush();
//                    myOutWriter.close();
//                    fOut.flush();
//                    fOut.close();
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//
                startbtn.setVisibility(View.VISIBLE);
                stopbtn.setVisibility(View.GONE);
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
}
