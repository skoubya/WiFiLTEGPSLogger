package com.lenss.yzeng.wifilogger.logdata;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;

import com.lenss.yzeng.wifilogger.LogService;

import java.util.List;

/* Retrieves the received LTE signal power */
public class LTERPData extends LogService.LogData {
    private TelephonyManager tm = null;

    public LTERPData(String name, Context context, Process rootProc){
        super(name, context, rootProc);
        if (cntxt != null) {
            tm = (TelephonyManager)cntxt.getSystemService(Context.TELEPHONY_SERVICE);
        }
    }

    @Override
    public void setCntxt(Context context){
        super.setCntxt(context);
        if (cntxt != null) {
            tm = (TelephonyManager)cntxt.getSystemService(Context.TELEPHONY_SERVICE);
        }
    }

    @Override
    public String retrieve(){
        String dbm= "NA";
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
            //do your check here
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(cntxt, permission) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permission denied!");
                    return dbm;
                }
            }
        }
        if(tm!=null){
            List<CellInfo> cellInfoList= tm.getAllCellInfo();
            if(cellInfoList!=null) {
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoLte && cellInfo.isRegistered()) {
                        // cast to CellInfoLte and call all the CellInfoLte methods you need
                        dbm = Integer.toString(((CellInfoLte) cellInfo).getCellSignalStrength().getDbm());
                        //TODO: May be incorrect if multiple devices can be registered
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= 28){
            int invalid = 0x7FFFFFFF; //means invalid readings
            if (dbm.equals(Integer.toString(invalid))){
                dbm = "NA";
            }
        }

        return dbm;
    }
}
