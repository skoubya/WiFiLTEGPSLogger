package com.lenss.yzeng.wifilogger.logdata;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

/* Abstract class for accessing transmitted and received data statistics for LTE */
public abstract class LTEPacketData extends SingleDiffFileData {
    private static String[] COMMAND = {LogConstants.CAT_PATH, LogConstants.NETWORK_FILE};

    private String prevSearch;

    public LTEPacketData(String name, Context context, Process rootProc){
        super(name, context, rootProc);;
        command = COMMAND;
        prevSearch = "";
        search = "";

        if(Build.VERSION.SDK_INT >= 28){//Android 9 and later
            needRoot = true;
        }
        else {
            needRoot = false;
        }
    }

    @Override
    public String retrieve(){
        String result = "NA";
        String intfName = "";

        ConnectivityManager connMgr = (ConnectivityManager) cntxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connMgr.getAllNetworks();
        for(Network net : networks) {
            LinkProperties linkProp = connMgr.getLinkProperties(net);
            NetworkCapabilities netCap = connMgr.getNetworkCapabilities(net);

            if(netCap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                if(netCap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){
                    intfName = linkProp.getInterfaceName();
                    break;
                }
            }
        }

        search = intfName;

        //If switched interfaces, there is no previous value
        if(!prevSearch.equals(search)){
            prevVal = -1;
        }
        prevSearch = search;

        //If search string is blank, no interface was found
        if(!search.equals("")){
            result = super.retrieve();
        }

        return result;
    }
}
