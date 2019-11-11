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

public class LTERPData extends LogService.LogData {
    private TelephonyManager tm = null;

    public LTERPData(String name, Context context){
        super(name, context);
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
                    if (cellInfo instanceof CellInfoLte) {
                        // cast to CellInfoLte and call all the CellInfoLte methods you need
                        dbm = Integer.toString(((CellInfoLte) cellInfo).getCellSignalStrength().getDbm());
                    }
                }
            }
        }

        return dbm;
    }
}
