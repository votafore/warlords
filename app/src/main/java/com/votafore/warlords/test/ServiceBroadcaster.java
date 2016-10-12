package com.votafore.warlords.test;


import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.votafore.warlords.GameManager;

public class ServiceBroadcaster implements NsdManager.RegistrationListener {

    private NsdManager mNsdManager;

    public static String mServiceName = "Warlords";
    public static String mServiceType = "_http._tcp.";

    private NsdServiceInfo mServiceInfo;

    public ServiceBroadcaster(Context context){

        Log.v(GameManager.TAG + "_1", "ServiceBroadcaster: конструктор");

        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void setServiceInfo(NsdServiceInfo serviceInfo) {
        mServiceInfo = serviceInfo;
    }




    /*************************************************************************************************/
    /*************************************** основной функционал *************************************/
    /*************************************************************************************************/

    public void startBroadcast(){

        Log.v(GameManager.TAG + "_1", "ServiceBroadcaster: startBroadcast(). Включаем транслящию сервиса. port: " + String.valueOf(mServiceInfo.getPort()));

        mNsdManager.registerService(mServiceInfo, NsdManager.PROTOCOL_DNS_SD, this);
    }

    public void stopBroadcast(){

        Log.v(GameManager.TAG + "_1", "ServiceBroadcaster: stopBroadcast()");

        // отменяем регистрацию (трансляцию) сервиса в сети
        try {
            mNsdManager.unregisterService(this);
        } catch (IllegalArgumentException e) {
            Log.v(GameManager.TAG, "ServiceBroadcaster: stopBroadcast(). Ошибка - " + e.getMessage());
            e.printStackTrace();
        }
    }



    /*************************************************************************************************/
    /********************************* NsdManager.RegistrationListener *******************************/
    /*************************************************************************************************/

    @Override
    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.v(GameManager.TAG + "_1", "ServiceBroadcaster - NsdManager.RegistrationListener: onRegistrationFailed");
    }

    @Override
    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.v(GameManager.TAG + "_1", "ServiceBroadcaster - NsdManager.RegistrationListener: onUnregistrationFailed");
    }

    @Override
    public void onServiceRegistered(NsdServiceInfo serviceInfo) {

        // актуализация имени сервиса
        mServiceName = serviceInfo.getServiceName();

        Log.v(GameManager.TAG + "_1", "ServiceBroadcaster - NsdManager.RegistrationListener: onServiceRegistered!!! Service name - " + mServiceName);
    }

    @Override
    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
        Log.v(GameManager.TAG + "_1", "ServiceBroadcaster - NsdManager.RegistrationListener: onServiceUnregistered");
    }
}