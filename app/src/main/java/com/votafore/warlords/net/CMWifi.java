package com.votafore.warlords.net;

import android.net.nsd.NsdServiceInfo;

/**
 * @author Votafore
 * Created on 19.09.2016.
 * Connection Manager WI-FI
 * provide connectivity via wi-fi
 */

public class CMWifi implements IConnectivity{

    public CMWifi(){

        NsdServiceInfo serviceInfo;

        serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName("WarlordsService");
        serviceInfo.setServiceType("_http._tcp.");


    }


    @Override
    public boolean init() {
        return false;
    }
}
