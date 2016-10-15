package com.votafore.warlords.support;


import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.votafore.warlords.GameFactory;


public class ServiceBroadcaster implements NsdManager.RegistrationListener {

    private NsdManager mNsdManager;

    public static String mServiceName = "Warlords";
    public static String mServiceType = "_http._tcp.";

    private NsdServiceInfo mServiceInfo;

    public ServiceBroadcaster(Context context){

        //Log.v(GameFactory.TAG + "_1", "ServiceBroadcaster: конструктор");

        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void setServiceInfo(NsdServiceInfo serviceInfo) {
        mServiceInfo = serviceInfo;
    }




    /*************************************************************************************************/
    /*************************************** основной функционал *************************************/
    /*************************************************************************************************/

    public void startBroadcast(){

        //Log.v(GameFactory.TAG, "ServiceBroadcaster: startBroadcast(). Включаем транслящию сервиса. port: " + String.valueOf(mServiceInfo.getPort()));

        mNsdManager.registerService(mServiceInfo, NsdManager.PROTOCOL_DNS_SD, this);
    }

    public void stopBroadcast(){

        //Log.v(GameFactory.TAG, "ServiceBroadcaster: stopBroadcast()");

        // отменяем регистрацию (трансляцию) сервиса в сети
        try {
            mNsdManager.unregisterService(this);
        } catch (IllegalArgumentException e) {
            //Log.v(GameFactory.TAG, "ServiceBroadcaster: stopBroadcast(). Ошибка - " + e.getMessage());
            e.printStackTrace();
        }
    }



    /*************************************************************************************************/
    /********************************* NsdManager.RegistrationListener *******************************/
    /*************************************************************************************************/

    @Override
    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        //Log.v(GameFactory.TAG, "ServiceBroadcaster - NsdManager.RegistrationListener: onRegistrationFailed");
    }

    @Override
    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        //Log.v(GameFactory.TAG, "ServiceBroadcaster - NsdManager.RegistrationListener: onUnregistrationFailed");
    }

    @Override
    public void onServiceRegistered(NsdServiceInfo serviceInfo) {

        // актуализация имени сервиса
        mServiceName = serviceInfo.getServiceName();

        Log.v(GameFactory.TAG, "ServiceBroadcaster - NsdManager.RegistrationListener: onServiceRegistered!!! Service name - " + mServiceName);
    }

    @Override
    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
        Log.v(GameFactory.TAG, "ServiceBroadcaster - NsdManager.RegistrationListener: onServiceUnregistered");
    }
}