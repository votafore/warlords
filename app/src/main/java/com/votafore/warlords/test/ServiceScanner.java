package com.votafore.warlords.test;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.votafore.warlords.GameManager;
import com.votafore.warlords.net.ConnectionChanel;
import com.votafore.warlords.net.IConnection;
import com.votafore.warlords.net.ISocketListener;
import com.votafore.warlords.net.SocketConnection;
import com.votafore.warlords.support.Queries;

import org.json.JSONException;
import org.json.JSONObject;



public class ServiceScanner implements NsdManager.DiscoveryListener{

    private NsdManager                  mNsdManager;
    private ConnectionChanel            mChanel;
    private ConnectionChanel.IObserver  mObserver;

    private GameFactory                 mFactory;

    public ServiceScanner(final Context context){

        //Log.v(GameManager.TAG + "_1", "ServiceScanner: конструктор");

        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        mChanel     = new ConnectionChanel(ConnectionChanel.TYPE_FOR_CLIENT);

        mChanel.setCustomListener(new ISocketListener() {
            @Override
            public void onCommandReceived(IConnection connection, String message) {

            }

            @Override
            public void onSocketConnected(IConnection connection) {

                Log.v(GameManager.TAG, "ServiceScanner - CustomListener - onSocketConnected. Отправляем запрос.");

                connection.put(Queries.getQuery(Queries.QUERY_INSTANCE));
                connection.send();
            }

            @Override
            public void onSocketDisconnected(IConnection connection) {

            }
        });

        mFactory = GameFactory.getInstance(context);

        mObserver = new ConnectionChanel.IObserver() {
            @Override
            public void notifyObserver(IConnection connection, String message) {

                // TODO: этот ответ получают даже те, кто не отсылал его
                // переделать этот момент

                Log.v(GameManager.TAG, "ServiceScanner - ConnectionChanel.IObserver: notifyObserver(). есть ответ от сервера");

                JSONObject response;

                try {
                    response = new JSONObject(message);

                    switch(response.getString("type")){
                        case "InstanceInfo":

                            Log.v(GameManager.TAG, "ServiceScanner - ConnectionChanel.IObserver: notifyObserver(). это инфа о созданном инстансе");

                            ListAdapter.ListItem item = new ListAdapter.ListItem();

                            item.mResMap       = response.getInt("map");
                            item.mCreator      = response.getInt("creatorID");
                            item.mCreatorName  = response.getString("creatorName");

                            item.mConnection   = connection;

                            //Log.v(GameManager.TAG, "ServiceScanner - ConnectionChanel.IObserver: notifyObserver(). добавляем элемент списка в адаптер");

                            String host = "/" + mFactory.getLocalIpAddress(context);

                            if(item.mConnection instanceof SocketConnection){
                                host = ((SocketConnection)item.mConnection).getHost();
                            }

                            item.mHost = host;

                            mAdapter.addItem(item);

                            break;
                    }

                } catch (JSONException e) {
                    //Log.v(GameManager.TAG, "ServiceScanner - ConnectionChanel.IObserver: notifyObserver(). обработка ответа сервера. Ошибка: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }




    /**************************************************************************************/
    /******************************** Основные возможности ********************************/
    /**************************************************************************************/

    public void startScan(){

        Log.v(GameManager.TAG, "ServiceScanner - startScan.");

        mChanel.registerObserver(mObserver);

        mNsdManager.discoverServices(ServiceBroadcaster.mServiceType, NsdManager.PROTOCOL_DNS_SD, this);
    }

    public void stopScan(){

        Log.v(GameManager.TAG, "ServiceScanner - stopScan.");

        try {
            mNsdManager.stopServiceDiscovery(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        mChanel.unregisterObserver(mObserver);
    }

    public void close(){

        Log.v(GameManager.TAG, "ServiceScanner - close.");

        mChanel.clearObservers();
        mChanel.close();
    }



    /******************************** дополнительные возможности ********************************/


    /**************************************************************************************/
    /****************************** работа с адаптером списка *****************************/
    /**************************************************************************************/

    private ListAdapter mAdapter;

    public void setAdapter(ListAdapter adapter){

        //Log.v(GameManager.TAG + "_1", "ServiceScanner - setAdapter.");

        mAdapter = adapter;
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
    }

    @Override
    public void onServiceFound(NsdServiceInfo serviceInfo) {

        if(!serviceInfo.getServiceName().contains(ServiceBroadcaster.mServiceName))
            return;

        Log.v(GameManager.TAG, "ServiceScanner - onServiceFound. пытаемся подключиться к сервису");
        mNsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                //Log.v(GameManager.TAG, "ServiceScanner - NsdManager.ResolveListener - onResolveFailed. не удалось подключиться к сервису");
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {

                Log.v(GameManager.TAG, "ServiceScanner - NsdManager.ResolveListener - onServiceResolved. добавляем соединение");
                Log.v(GameManager.TAG, "хост: " + serviceInfo.getHost().toString());
                Log.v(GameManager.TAG, "порт: " + String.valueOf(serviceInfo.getPort()));

                // если сокет к этому хосту уже есть, то новый добавлять не надо
                if(mAdapter.isExist(serviceInfo.getHost().toString()))
                    return;

                Log.v(GameManager.TAG, "ServiceScanner - NsdManager.ResolveListener - onServiceResolved. таки добавляем");
                mChanel.getConnectionAppend().addConnection(serviceInfo.getHost().toString(), serviceInfo.getPort());
            }
        });
    }

    @Override
    public void onServiceLost(NsdServiceInfo serviceInfo) {
        Log.v(GameManager.TAG, "ServiceScanner - onServiceLost.");

        // TODO: нужен механизм слежения за отвалившимися сервисами
        // хотя, может и не понадобится т.к. во время игры нет поиска сервисов
    }



    // fails
    @Override
    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
        //Log.v(GameManager.TAG + "_1", "ServiceScanner - onStartDiscoveryFailed");
    }

    @Override
    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
        //Log.v(GameManager.TAG + "_1", "ServiceScanner - onStopDiscoveryFailed");
    }
}
