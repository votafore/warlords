package com.votafore.warlords.v4;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.opengl.GLSurfaceView;


import com.votafore.warlords.ActivityMain;
import com.votafore.warlords.v2.ServiceInfo;
import com.votafore.warlords.v4.constant.Constants;
import com.votafore.warlords.v4.constant.Log;
import com.votafore.warlords.v4.network.IServer;
import com.votafore.warlords.v4.network.ServerLocal;
import com.votafore.warlords.v4.network.ServerRemote;
import com.votafore.warlords.v4.test.DiscoveryListener;
import com.votafore.warlords.v4.test.IState;
import com.votafore.warlords.v4.test.ResolveListener;


import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Stack;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.votafore.warlords.v4.constant.Constants.LVL_ADAPTER;
import static com.votafore.warlords.v4.constant.Constants.LVL_APP;
import static com.votafore.warlords.v4.constant.Constants.LVL_NW_WATCHER;
import static com.votafore.warlords.v4.constant.Constants.TAG_APP_START;
import static com.votafore.warlords.v4.constant.Constants.TAG_APP_STOP;
import static com.votafore.warlords.v4.constant.Constants.TAG_SCAN;
import static com.votafore.warlords.v4.constant.Constants.TAG_SCAN_START;
import static com.votafore.warlords.v4.constant.Constants.TAG_SRV_CRT;
import static com.votafore.warlords.v4.constant.Constants.TAG_SRV_START;

/**
 * @author Vorafore
 * Created on 18.12.2017.
 */

public class App extends Application {

    private BroadcastReceiver mNetReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG_APP_START, "starting...");

        refreshIP();

        Log.d1(TAG_APP_START, LVL_NW_WATCHER, "create");
        mNetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(!intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
                    return;

                Log.d1("", LVL_NW_WATCHER, "network state changed");

                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                mDeviceIP = "0.0.0.0";

                if(info.isConnected())
                    refreshIP();
            }
        };

        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        Log.d1(TAG_APP_START, LVL_NW_WATCHER, "register");
        registerReceiver(mNetReceiver, filter);


        Log.d1(TAG_APP_START, LVL_ADAPTER, "create");
        //mServerListAdapter = new AdapterServerList(this);



        testFunc();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // TODO: 28.12.2017  not work.. define where it can be done
        Log.setTAG(TAG_APP_STOP);
        Log.d("stopping...");

        //Log.d(String.format(format1, LVL_NW_WATCHER, "unregister"));
        unregisterReceiver(mNetReceiver);
    }




    /***************** server *******************/

    private IServer mServer;

    public IServer getServer(){
        Log.d("getServer");
        return mServer;
    }

    public void setServer(IServer server){
        Log.d("setServer");

        Log.d1(TAG_SRV_CRT, LVL_APP, "setting server in the app...");
        mServer = server;

        Log.d1(TAG_SRV_START, LVL_APP, "starting...");
        mServer.start(getApplicationContext());
        Log.d1(TAG_SRV_START, LVL_APP, "started");
    }

    public void dismissServer(){

        mServer.stopSearching();
        mServer.stop();
        mServer = null;
    }




    /***************** game *******************/

    private Game mGame;


    public void startGame(){
        Log.d("starting game...");

        // TODO: 25.12.2017 prepare objects for game

        mGame = new Game(getApplicationContext());
        mGame.setServer(mServer);
        mGame.start();

    }

    public GLSurfaceView getView(){
        return mGame.getSurfaceView();
    }




    /***************** UTILS **************/

    private String mDeviceIP = "";

    public String getDeviceIP(){
        Log.d("getDeviceIP");
        return mDeviceIP;
    }

    private void refreshIP(){
        Log.d("refreshIP");

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){

                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {

                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        mDeviceIP = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }




    /*************** TESTS *********************/

//    public static final String EVENT_LOCALSERVER_CREATE         = "com.votafore.warlords.local_server_create";
//    public static final String EVENT_LOCALSERVER_DISMISS        = "com.votafore.warlords.local_server_dismiss";
//    //public static final String EVENT_LOCALSERVER_STARTBROADCAST = "com.votafore.warlords.local_server_start_broadcast";
//    public static final String EVENT_LOCALSERVER_STOPBROADCAST  = "com.votafore.warlords.local_server_stop_broadcast";

    public static final String EVENT_GAME_START  = "com.votafore.warlords.game_start";






//    public static final String STATE_LS_CREATE    = "local server created";
//    public static final String STATE_LS_WAIT      = "local server is waiting for incoming connections";
//    public static final String STATE_RS_FIND      = "searching for remote server";
//    public static final String STATE_RS_CREATE    = "remote server created";
//    public static final String STATE_GAME_STARTED = "game started";
//
//    private Stack<IState> mStates;
//
//    private IState LS_create;
//    private IState LS_wait;
//
//    private IState RS_find;
//    private IState RS_create;
//
//    private IState startGame;

    private void testFunc(){

//        BroadcastReceiver eventReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                switch(intent.getAction()){
//                    case EVENT_LOCALSERVER_CREATE:
//
//                        localServerCreate();
//                        break;
//                    case EVENT_LOCALSERVER_DISMISS:
//
//                        localServerDismiss();
//                        break;
//
////                    case EVENT_LOCALSERVER_STARTBROADCAST:
////
////                        mServerLocal.startBroadcast(getApplicationContext());
////                        break;
//
//                    case EVENT_LOCALSERVER_STOPBROADCAST:
//
//                        mServerLocal.stopBroadcast();
//                        break;
//
//                    case EVENT_GAME_START:
//
//                        startGame();
//                        break;
//                }
//            }
//        };
//
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(EVENT_LOCALSERVER_CREATE);
//        filter.addAction(EVENT_LOCALSERVER_DISMISS);
//        //filter.addAction(EVENT_LOCALSERVER_STARTBROADCAST);
//        filter.addAction(EVENT_LOCALSERVER_STOPBROADCAST);
//
//        filter.addAction(EVENT_GAME_START);
//
//        registerReceiver(eventReceiver, filter);


//        mStates = new Stack<>();
//
//        LS_create = new IState() {
//            @Override
//            public void onStateStart() {
//                localServerCreate();
//            }
//
//            @Override
//            public void onStateEnd() {
//                localServerDismiss();
//            }
//        };
//        LS_wait   = new IState() {
//            @Override
//            public void onStateStart() {
//                mServerLocal.startBroadcast(getApplicationContext());
//            }
//
//            @Override
//            public void onStateEnd() {
//                mServerLocal.stopBroadcast();
//            }
//        };
//
//        RS_find   = new IState() {
//            @Override
//            public void onStateStart() {
//                remoteServerFind();
//            }
//
//            @Override
//            public void onStateEnd() {
//                dsp_findServer.dispose();
//            }
//        };
//        RS_create = new IState() {
//            @Override
//            public void onStateStart() {
//
//            }
//
//            @Override
//            public void onStateEnd() {
//                remoteServerDismiss();
//            }
//        };
//
//        startGame = new IState() {
//            @Override
//            public void onStateStart() {
//                startGame();
//            }
//
//            @Override
//            public void onStateEnd() {
//
//            }
//        };
    }



//    public String getState(){
//
//        String result = "";
//
//        if(mStates.peek() == LS_create)
//            result = STATE_LS_CREATE;
//
//        if(mStates.peek() == LS_wait)
//            result = STATE_LS_WAIT;
//
//        if(mStates.peek() == RS_find)
//            result = STATE_RS_FIND;
//
//        if(mStates.peek() == RS_create)
//            result = STATE_RS_CREATE;
//
//        return result;
//    }

//    public void nextState(String state){
//
//        IState next;
//
//        switch(state){
//            case STATE_LS_CREATE:
//                next = LS_create;
//                break;
//            case STATE_LS_WAIT:
//                next = LS_wait;
//                break;
//            case STATE_RS_FIND:
//                next = RS_find;
//                break;
//            case STATE_RS_CREATE:
//                next = RS_create;
//                break;
//            case STATE_GAME_STARTED:
//                next = startGame;
//                break;
//            default:
//                next = LS_create;
//        }
//
//        mStates.push(next);
//
//        next.onStateStart();
//    }

//    public void changeState(String state){
//
//        mStates.pop().onStateEnd();
//
//        nextState(state);
//    }





//    private boolean isLocal = false;
//
//    /******* local *******/
//
//    private ServerLocal mServerLocal;
//
//
//    public void localServerCreate(){
//
//        mServerLocal = new ServerLocal();
//
//        setServer(mServerLocal);
//
//        //mServerLocal.startBroadcast(getApplicationContext());
//
//        mServerLocal.setReceiver(new Consumer<JSONObject>() {
//            @Override
//            public void accept(JSONObject object) throws Exception {
//
//                if(!object.has("event") || !object.getString("event").equals("StartGame"))
//                    return;
//
//                startGame();
//
//                sendBroadcast(new Intent(EVENT_GAME_START));
//            }
//        });
//
//        isLocal = true;
//    }
//
//    public void localServerDismiss(){
//
//        //mServerLocal.stopBroadcast();
//        mServerLocal.stop();
//
//        mServerLocal = null;
//    }



    /******* remote *******/

    //Disposable dsp_findServer;

    //public void remoteServerFind(){

//        dsp_findServer = Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
//            @Override
//            public void subscribe(final ObservableEmitter<ServiceInfo> e){
//
//                final NsdManager manager = (NsdManager) getSystemService(Context.NSD_SERVICE);
//                final DiscoveryListener listener = new DiscoveryListener(){
//                    @Override
//                    public void onServiceFound(NsdServiceInfo serviceInfo) {
//
//                        com.votafore.warlords.v3.Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onServiceFound");
//
//                        ServiceInfo svc_info = new ServiceInfo();
//                        svc_info.messageType = ServiceInfo.SERVICE_FOUND;
//                        svc_info.info = serviceInfo;
//
//                        e.onNext(svc_info);
//                    }
//                };
//
//                com.votafore.warlords.v3.Log.d2(TAG_SCAN, LVL_ADAPTER, "CONN OBSERVABLE", "create disc. listener, start discover");
//                manager.discoverServices(Constants.SERVICETYPE, NsdManager.PROTOCOL_DNS_SD, listener);
//
//
//                e.setCancellable(new Cancellable() {
//                    @Override
//                    public void cancel() throws Exception {
//                        com.votafore.warlords.v3.Log.d2(TAG_SCAN, LVL_ADAPTER, "CONN OBSERVABLE", "cancel. stop discovery");
//                        manager.stopServiceDiscovery(listener);
//                    }
//                });
//            }
//        })
//                // check if it is one of game services
//                .filter(new Predicate<ServiceInfo>() {
//                    @Override
//                    public boolean test(ServiceInfo serviceInfo) throws Exception {
//                        return serviceInfo.info.getServiceName().contains(Constants.SERVICENAME)
//                                && serviceInfo.messageType.equals(ServiceInfo.SERVICE_FOUND)
//                                && (!serviceInfo.info.getHost().toString().replace("/", "").equals(mDeviceIP));
//                    }
//                })
//                // convert unresolved NsdInfo into resolved NsdInfo
//                .flatMap(new Function<ServiceInfo, Observable<NsdServiceInfo>>() {
//                    @Override
//                    public Observable<NsdServiceInfo> apply(final ServiceInfo serviceInfo) throws Exception {
//
//                        com.votafore.warlords.v3.Log.d3(TAG_SCAN, LVL_ADAPTER, "FOUND", "RESOLVE", "create observable");
//
//                        return Observable.create(new ObservableOnSubscribe<NsdServiceInfo>() {
//                            @Override
//                            public void subscribe(final ObservableEmitter<NsdServiceInfo> e) {
//
//                                com.votafore.warlords.v3.Log.d3(TAG_SCAN, LVL_ADAPTER, "FOUND", "RESOLVE", "start");
//
//                                NsdManager manager = (NsdManager) getSystemService(Context.NSD_SERVICE);
//                                manager.resolveService(serviceInfo.info, new ResolveListener() {
//
//                                    @Override
//                                    public void onServiceResolved(NsdServiceInfo serviceInfo) {
//                                        super.onServiceResolved(serviceInfo);
//                                        e.onNext(serviceInfo);
//                                    }
//                                });
//
//                            }
//                        });
//                    }
//                })
//                .subscribe(new Consumer<NsdServiceInfo>() {
//                    @Override
//                    public void accept(NsdServiceInfo serviceInfo) throws Exception {
//
//                        remoteServerCreate();
//
//                        mStates.pop().onStateEnd();
//                        mStates.push(RS_create);
//
//                        // TODO: 14.01.2018 notify activity that server was created
//
//                    }
//                });
    //}


//    private ServerRemote mServerRemote;
//
//    public void remoteServerCreate(){
//
//        mServerRemote = new ServerRemote();
//        mServerRemote.start(getApplicationContext());
//    }
//
//    public void remoteServerDismiss(){
//
//        mServerRemote.stop();
//        mServerRemote = null;
//    }





    // для автоподключения устройств опробовать возможность использования WIFI_AWARE_SERVICE из getSystemService
    // https://developer.android.com/guide/topics/connectivity/wifi-aware.html










    /************ states of application *******************/

    // choise: create or connect

    // create
    //  - create server
    //      start server
    //      start broadcasting

    // start game
    //  - stop broadcasting
    //      start game

}
