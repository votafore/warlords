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
        mServer.start();
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


    public static final String EVENT_GAME_START  = "com.votafore.warlords.game_start";


    private void testFunc(){


    }





    // для автоподключения устройств опробовать возможность использования WIFI_AWARE_SERVICE из getSystemService
    // https://developer.android.com/guide/topics/connectivity/wifi-aware.html

}
