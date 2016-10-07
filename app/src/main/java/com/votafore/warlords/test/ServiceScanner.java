package com.votafore.warlords.test;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.votafore.warlords.GameManager;
import com.votafore.warlords.net.ConnectionChanel;

import org.json.JSONException;
import org.json.JSONObject;


public class ServiceScanner implements NsdManager.DiscoveryListener{

    private NsdManager          mNsdManager;
    private ConnectionChanel2   mChanel;
    private ConnectionChanel.IObserver mObserver;


    public ServiceScanner(Context context){

        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        mChanel     = new ConnectionChanel2();

        mChanel.setupAppend(ConnectionChanel.TYPE_FOR_CLIENT);

        mObserver = new ConnectionChanel.IObserver() {
            @Override
            public void notifyObserver(int connectionId, String message) {

                // TODO: этот ответ получают даже те, кто не отсылал его
                // переделать этот момент

                Log.v(GameManager.TAG, "ServiceScanner - ConnectionChanel.IObserver: notifyObserver(). есть ответ от сервера");

                JSONObject response;

                try {
                    response = new JSONObject(message);

                    switch(response.getString("type")){
                        case "InstanceInfo":

                            Log.v(GameManager.TAG, "ServiceScanner - ConnectionChanel.IObserver: notifyObserver(). это инфа о созданном инстансе");

                            break;
                    }

                } catch (JSONException e) {
                    Log.v(GameManager.TAG, "ServiceScanner - ConnectionChanel.IObserver: notifyObserver(). обработка ответа сервера. Ошибка: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }




    /**************************************************************************************/
    /******************************** Основные возможности ********************************/
    /**************************************************************************************/

    public void startScan(){

        Log.v(GameManager.TAG, "ServiceScanner - startScan().");

        mNsdManager.discoverServices(GameManager.mServiceType, NsdManager.PROTOCOL_DNS_SD, this);

        mChanel.registerObserver(mObserver);
    }

    public void stopScan(){

        Log.v(GameManager.TAG, "ServiceScanner - stopScan().");

        try {
            mNsdManager.stopServiceDiscovery(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        mChanel.clearObservers();
        mChanel.close();
    }

    /**************************************************************************************/
    /**************************** NsdManager.DiscoveryListener ****************************/
    /**************************************************************************************/

    @Override
    public void onDiscoveryStarted(String serviceType) {
        Log.v(GameManager.TAG, "ServiceScanner - onDiscoveryStarted");
    }

    @Override
    public void onDiscoveryStopped(String serviceType) {
        Log.v(GameManager.TAG, "ServiceScanner - onDiscoveryStopped");

        // как только прекратили искать (5 сек. ожидания)
        // посылаем запрос на информацию о созданных инстансах

        //Log.v(GameManager.TAG, "ServiceScanner - рассылаем запросы");

//        // при отправке запроса:
//        new Thread(new Runnable() {
//            @Override
//            public void run() {

                //Log.v(GameManager.TAG, "ServiceScanner: поток рассылки запросов - запущен");

                // отправляем запрос на информацию об инстансе
//                try {
//
//                    JSONObject query = new JSONObject();
//
//                    query.put("clientID" , 0);
//                    query.put("type"     , "InstanceInfo");
//                    query.put("command"  , "get");
//
//                    Log.v(GameManager.TAG, "ServiceScanner: поток рассылки запросов - отправляем запрос");
//
//                    mChanel.sendCommand(query.toString());
//
//                } catch (JSONException e) {
//
//                    Log.v(GameManager.TAG, "ServiceScanner: поток рассылки запросов, создание запроса: " + e.getMessage());
//
//                    e.printStackTrace();
//                }

//                Log.v(GameManager.TAG, "ServiceScanner: поток рассылки запросов - ждем ответы (5 сек)");
//
//                // ждем ответы 5 сек
//                long end = System.currentTimeMillis() + 5 * 1000;
//                while(System.currentTimeMillis() < end){
//
//                    try {
//                        Thread.sleep(end - System.currentTimeMillis());
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }

                //Log.v(GameManager.TAG, "ServiceScanner: поток рассылки запросов - закрываем все соединения");

                // закрываем соединение(я)
                //mConnectionManager.close();

                //clientChanel.close();

//            }
//        }).start();
    }

    @Override
    public void onServiceFound(NsdServiceInfo serviceInfo) {

        Log.v(GameManager.TAG, "ServiceScanner - onServiceFound. нашли сервис. пытаемся получить инфу для подключения");

        if(!serviceInfo.getServiceName().contains(GameManager.mServiceName))
            return;

        Log.v(GameManager.TAG, "ServiceScanner - onServiceFound. пытаемся подключиться к сервису");
        mNsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.v(GameManager.TAG, "ServiceScanner - NsdManager.ResolveListener - onResolveFailed. не удалось подключиться к сервису");
            }

            @Override
            public void onServiceResolved(final NsdServiceInfo serviceInfo) {

                Log.v(GameManager.TAG, "ServiceScanner - NsdManager.ResolveListener - onServiceResolved. добавляем соединение");
                Log.v(GameManager.TAG, "хост: " + serviceInfo.getHost().toString());
                Log.v(GameManager.TAG, "порт: " + String.valueOf(serviceInfo.getPort()));

                new Runnable(){
                    @Override
                    public void run() {

                        Log.v(GameManager.TAG, "ServiceScanner - NsdManager.ResolveListener - поток запроса - запущен. Добавляем подключение");

                        mChanel.getConnectionAppend().addConnection(serviceInfo.getHost().toString(), serviceInfo.getPort());

                        Log.v(GameManager.TAG, "ServiceScanner - NsdManager.ResolveListener - поток запроса - запущен. ждем 1 сек что бы подключение создалось");

                        try {
                            // дадим время на создание соединения
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.v(GameManager.TAG, "ServiceScanner - NsdManager.ResolveListener - поток запроса - запущен. помещаем запрос в стек для всех подключений");

                        // помещаем запрос для конкретного подключения в стек
                        int indexLast = mChanel.getConnections().size() - 1;

                        if(indexLast > -1){

                            IConnection2  connection = mChanel.getConnections().get(indexLast);
                            connection.put(Queries.getQuery(Queries.QUERY_INSTANCE));

                            Log.v(GameManager.TAG, "ServiceScanner - NsdManager.ResolveListener - поток запроса - запрос отправлен.");
                        }

                    }
                }.run();
            }
        });
    }

    @Override
    public void onServiceLost(NsdServiceInfo serviceInfo) {
        Log.v(GameManager.TAG, "ServiceScanner - onServiceLost.");
    }



    // fails
    @Override
    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
        Log.v(GameManager.TAG, "ServiceScanner - onStartDiscoveryFailed");
    }

    @Override
    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
        Log.v(GameManager.TAG, "ServiceScanner - onStopDiscoveryFailed");
    }
}
