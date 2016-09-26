package com.votafore.warlords;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.JsonWriter;
import android.util.Log;
import android.view.MotionEvent;

import com.votafore.warlords.game.Instance;
import com.votafore.warlords.glsupport.GLRenderer;
import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLView;
import com.votafore.warlords.glsupport.GLWorld;
import com.votafore.warlords.net.IClient;
import com.votafore.warlords.net.IServer;
import com.votafore.warlords.net.ISocketListener;
import com.votafore.warlords.net.wifi.CMWifiClient;
import com.votafore.warlords.net.wifi.SocketConnection;
import com.votafore.warlords.test.TestConnectionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Votafore
 * Created on 17.09.2016.
 */
public class GameManager {//extends BroadcastReceiver{

    private static volatile GameManager mThis;

    static GameManager getInstance(Context context){

        if(mThis == null)
            mThis = new GameManager(context);

        return mThis;
    }

    private GameManager(Context context){

        mWorld = new GLWorld(this);
        mWorld.camMove(GLWorld.AXIS_Y, 3f);

        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);

        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.v(TAG, "onStartDiscoveryFailed");
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.v(TAG, "onStopDiscoveryFailed");
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.v(TAG, "onDiscoveryStarted");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.v(TAG, "onDiscoveryStopped");
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {

                if(!serviceInfo.getServiceName().contains(mServiceName))
                    return;

                mNsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {
                    @Override
                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                        Log.v(TAG, "onResolveFailed");
                    }

                    @Override
                    public void onServiceResolved(NsdServiceInfo serviceInfo) {
                        createConnection(serviceInfo);
                    }
                });
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.v(TAG, "onServiceLost");
            }
        };

        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {

                // актуализация имени сервиса
                mServiceName = serviceInfo.getServiceName();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

            }
        };
    }



    private String TAG = "GAMESERVICE";


    /*************************************************************************************************/
    /*********************************** ОСНОВНЫЕ ОБЪЕКТЫ СИСТЕМЫ ************************************/
    /*************************************************************************************************/

    /**
     * должен быть объект, определяющий параметры игры
     */
    private Instance mInstance;

    /**
     * объект для отрисовки 3D мира
     * создаем заранее
     */
    private GLView mSurfaceView;

    /**
     * 3D мир
     * управление камерой
     */
    private GLWorld     mWorld;


    void setInstance(Instance instance){
        mInstance = instance;
    }


    GLSurfaceView getSurfaceView(){
        return mSurfaceView;
    }




    /*************************************************************************************************/
    /*********************************** УПРАВЛЕНИЕ ИГРОВЫМ ПРОЦЕССОМ ********************************/
    /*************************************************************************************************/


    /**
     * запускаем игру
     */

    void startGame(Context context){

        GLShader   mShader     = new GLShader(context, R.raw.shader_vertex, R.raw.shader_fragment);
        GLRenderer mRenderer   = new GLRenderer(mWorld, mInstance, mShader);

        mSurfaceView = new GLView(context, mWorld, mRenderer) {
            @Override
            protected void init() {

                mHandler = new MotionHandlerJoystick(mContext, mCamera);
            }

            private MotionHandlerJoystick mHandler;

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return mHandler.onHandleEvent(event);
            }
        };

        mInstance.startInstance();
    }


    /**
     * прекращаем игру
     */

    public void stopGame(){

        mInstance.stopInstance();
    }


    /*************************************************************************************************/
    /*********************************** РАБОТА ПО СЕТИ В ЭТОМ КЛАССЕ ********************************/
    /*************************************************************************************************/


    /**
     * объект для поиска\создания сервиса, позволяющего
     * автоматически создать сеть для игры между игроками
     */
    private NsdManager mNsdManager;




    /******************************* вспомогательный объекты и переменные ****************************/

    /**
     * слушатель для начала и остановки поиска сервиса, а так же
     * для реакции на событие нахождения сервиса.
     */
    private NsdManager.DiscoveryListener mDiscoveryListener;


    /**
     * слушатель для регистрации сервиса и отмены его регистрации
     */
    private NsdManager.RegistrationListener mRegistrationListener;


    /**
     * имя сервиса (или хотя бы как оно должно выглядеть)
     */
    private String mServiceName = "Warlords";


    /**
     * протокол - транспорт сервиса
     */
    private String mServiceType = "_http._tcp.";



    /******************************* создание собственного сервиса (и сервера) ***********************/


    /**
     * что бы сервер мог отправлять ответ (сообщения) клиенту
     * ему нужен соответствющий объект (имитатор)
     */

    private IClient mClient;


    /**
     * запускаем наш сервис в мир
     * что бы его увидели и могли подключиться
     */
    public void startBroadcastService(){

        // создаем сервер подключений (сокетов)
        CMWifiClient client = new CMWifiClient(null);
        mClient = client;


        // создаем сервис для автоматического подключения пользователей
        NsdServiceInfo info;

        info = new NsdServiceInfo();
        info.setServiceName(mServiceName);
        info.setServiceType(mServiceType);
        info.setPort(client.mServerSocket.getLocalPort());

        mNsdManager.registerService(info, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }


    /**
     * останавливаем сервис
     */
    public void stopBroadcastService(){

        // отменяем регистрацию (трансляцию) сервиса в сети
        mNsdManager.unregisterService(mRegistrationListener);
    }







    /********************************** поиск созданных сервисов *************************************/


    /**
     * запускаем поиск игр (сервисов для подключения)
     * созданных другими игроками
     */
    public void discoverServers(Context context){

        mNsdManager.discoverServices(mServiceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }


    /**
     * прекращаем поиск сервисов
     */
    public void stopDiscoverServers(Context context){

        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }



    /**
     * имитатор сервера для Instance создаем уже сдесь
     * т.к. роль (клиент или сервер) определяется уже сейчас
     */
    private IServer mServer;










    /****************************** раздел еще в разработке ************************/

    private void createConnection(final NsdServiceInfo serviceInfo){

        // TODO: вообщето еще не факт что объект mInstance к этому моменту будет создан
        // надо определить порядок событий

        mConnectionManager.addConnection(serviceInfo.getHost(), serviceInfo.getPort());

        try {

            JSONObject response = new JSONObject();

            response.put("clientID" , 0);
            response.put("type"     , "InstanceInfo");
            response.put("command"  , "get");

            mConnectionManager.sendMessage(response.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * список созданных игр (найденных)
     */
    List<InstanceContainer> mInstances;


    /**
     * контейнер, для хранения информации об инстансе
     */
    private class InstanceContainer{

        public InetAddress mAddress;
        public int mResMap;
        public int mCreator;
    }


    /**
     * контейнер подключений
     */
    TestConnectionManager mConnectionManager = new TestConnectionManager();














//    //////////////////////////////////////
//    // РАЗДЕЛ РАБОТЫ С WI-FI
//    // реакция на изменение состояния
//    //////////////////////////////////////
//
//    private WifiP2pManager          mWifiP2pManager;
//    private WifiP2pManager.Channel  mWifiChannel;
//
//    /**
//     * подписываемся на событие изменения статуса Wi-Fi
//     */
//    private void subscribeWifiStateChange(){
//
//        IntentFilter intentFilter;
//
//        // Indicates a change in the Wi-Fi P2P status.
//        // Indicates a change in the list of available peers.
//        // Indicates the state of Wi-Fi P2P connectivity has changed.
//        // Indicates this device's details have changed.
//
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
//        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
//
//        mContext.registerReceiver(this, intentFilter);
//
//
//        mWifiP2pManager     = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
//        mWifiChannel        = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), new WifiP2pManager.ChannelListener() {
//            @Override
//            public void onChannelDisconnected() {
//                Log.v(TAG, "канал отключен");
//            }
//        });
//    }
//
//
//    private String TAG = "TEST_WIFI";
//
//    /**
//     * обработка изменения состояния wi-fi
//     * @param context
//     * @param intent
//     */
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        switch (intent.getAction()){
//            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
//
//                Log.v(TAG, "изменилось состояние");
//
//                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
//                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
//
//                    mWifiP2pManager.discoverPeers(mWifiChannel, new WifiP2pManager.ActionListener() {
//                        @Override
//                        public void onSuccess() {
//
//                            Log.v(TAG, "Запуск поиска пиров: успешно");
//                        }
//
//                        @Override
//                        public void onFailure(int reason) {
//
//                            Log.v(TAG, "Запуск поиска пиров: ошибка");
//                        }
//                    });
//                }
//
//                break;
//            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
//
//                Log.v(TAG, "изменились пиры");
//
//                mWifiP2pManager.requestPeers(mWifiChannel, new WifiP2pManager.PeerListListener() {
//                    @Override
//                    public void onPeersAvailable(WifiP2pDeviceList peers) {
//
//                        int count = peers.getDeviceList().size();
//
//                        if(count == 0)
//                            return;
//
//                        Log.v(TAG, String.format("найдены пиры: %s", count));
//
//                        List<WifiP2pDevice> list_peers = new ArrayList();
//                        list_peers.clear();
//                        list_peers.addAll(peers.getDeviceList());
//
//                        WifiP2pDevice  device = list_peers.get(0);
//
//                        WifiP2pConfig config;
//
//                        config = new WifiP2pConfig();
//                        config.deviceAddress = device.deviceAddress;
//                        config.wps.setup = WpsInfo.PBC;
//
//                        Log.v(TAG, "попытка подключения к пиру");
//                        mWifiP2pManager.connect(mWifiChannel, config, new WifiP2pManager.ActionListener() {
//                            @Override
//                            public void onSuccess() {
//
//                                Log.v(TAG, "подключение к пиру: успешно");
//                            }
//
//                            @Override
//                            public void onFailure(int reason) {
//
//                                Log.v(TAG, "подключение к пиру: fail");
//                            }
//                        });
//
//                    }
//                });
//
//                break;
//            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
//
//                Log.v(TAG, "изменилось подключение");
//
//                mWifiP2pManager.requestConnectionInfo(mWifiChannel, new WifiP2pManager.ConnectionInfoListener() {
//                    @Override
//                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
//
//                        InetAddress address = info.groupOwnerAddress;
//                    }
//                });
//
//                break;
//            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
//
//                Log.v(TAG, "изменились параметры уст-ва");
//                //Toast.makeText(mContext, "изменились параметры уст-ва", Toast.LENGTH_SHORT).show();
//
//                WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
//        }
//    }
}
