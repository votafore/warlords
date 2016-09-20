package com.votafore.warlords.net;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author Votafore
 * Created on 19.09.2016.
 * Connection Manager WI-FI
 * provide connectivity via wi-fi
 */

public class CMWifi implements IConnectivity, NsdManager.RegistrationListener{

    private String mServiceName = "WarlordsService";

    private Context mContext;

    private boolean mValid = false;

    public CMWifi(Context context) {

        mContext = context;

        ///////////////////////////////
        // variant 2
        ServerSocket socket;

        try {
            socket = new ServerSocket();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int port = socket.getLocalPort();
    }


    @Override
    public boolean init() {

        final boolean result = false;

        ///////////////////////////////
        // https://developer.android.com/training/connect-devices-wirelessly/nsd.html
        // variant 1
        NsdServiceInfo serviceInfo;

        serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType("_http._tcp.");
        serviceInfo.setPort(1225);

        NsdManager manager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);

        manager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, this);

        return result;
    }

    @Override
    public void release(){

        NsdManager manager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
        manager.unregisterService(this);
    }




    @Override
    public void onServiceRegistered(NsdServiceInfo serviceInfo) {

        mServiceName = serviceInfo.getServiceName();

        mValid = true;
    }

    @Override
    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        mValid = false;
    }

    @Override
    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

    }

    @Override
    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

    }
}
