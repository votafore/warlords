package com.votafore.warlords.v3;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import static com.votafore.warlords.v2.Constants.LVL_APP;
import static com.votafore.warlords.v2.Constants.TAG_APP_START;
import static com.votafore.warlords.v2.Constants.TAG_APP_STOP;
import static com.votafore.warlords.v2.Constants.LVL_ADAPTER;
import static com.votafore.warlords.v2.Constants.LVL_LOCAL_SERVER;
import static com.votafore.warlords.v2.Constants.LVL_NW_WATCHER;
import static com.votafore.warlords.v2.Constants.TAG_SRV_CRT;
import static com.votafore.warlords.v2.Constants.TAG_SRV_START;
import static com.votafore.warlords.v2.Constants.TAG_SRV_STOP;
import static com.votafore.warlords.v2.Constants.format1;

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
                Log.d1("", LVL_NW_WATCHER, "network state changed");
                refreshIP();
            }
        };

        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        Log.d1(TAG_APP_START, LVL_NW_WATCHER, "register");
        registerReceiver(mNetReceiver, filter);

        Log.d1(TAG_APP_START, LVL_ADAPTER, "create");
        mServerListAdapter = new AdapterServerList(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // TODO: 28.12.2017  not work.. define where it can be done
        Log.setTAG(TAG_APP_STOP);
        Log.d("stopping...");

        Log.d(String.format(format1, LVL_NW_WATCHER, "unregister"));
        unregisterReceiver(mNetReceiver);
    }



    /***************** page before game *******************/

    /**
     * adapter for list of servers
     */
    private AdapterServerList mServerListAdapter;

    public AdapterServerList getAdapter(){
        Log.d("getAdapter");
        return mServerListAdapter;
    }




    /***************** local server *******************/

    private IServer mServer;

    public void createServer(){

        Log.d1(TAG_SRV_CRT, LVL_APP, "starting...");

//        Log.setLevel(LVL_LOCAL_SERVER);
//        Log.d("creating...");

        Log.d1(TAG_SRV_CRT, LVL_APP, "creating...");
        mServer = new ServerLocal();
        Log.d1(TAG_SRV_CRT, LVL_APP, "created");
        //Log.d("created");

        //Log.reset();


//        Log.setTAG(TAG_SRV_START);
//        Log.setLevel(LVL_LOCAL_SERVER);
//
//        Log.d("starting...");

        Log.d1(TAG_SRV_START, LVL_APP, "starting...");
        mServer.start(this);
        Log.d1(TAG_SRV_START, LVL_APP, "started");
        //Log.d("started");


        //mServerListAdapter.addLocalServer(mServer); // TODO: 29.12.2017 VERNUT

        //Log.reset();
    }

    public void stopServer(){

        Log.d1(TAG_SRV_STOP, LVL_APP, "stopping...");

        //mServerListAdapter.removeLocalServer(); // TODO: 29.12.2017 VERNUT

//        Log.setTAG(TAG_SRV_STOP);
//        Log.d("stopping...");

        //Log.setLevel(LVL_LOCAL_SERVER);

        //Log.d("stopping...");
        mServer.stop();
        //Log.d("stopped");

        Log.d1(TAG_SRV_STOP, LVL_APP, "stopped");

        //Log.reset();
    }




    /***************** selected server *******************/

    private IServer mSelected;

    public void setSelected(IServer server){
        Log.d("setSelected");
        mSelected = server;
    }

    public IServer getSelected(){
        Log.d("getSelected");
        return mSelected;
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

    private Game mGame;

    /**
     * START GAME !!!!!!!!!!
     * finish scanning for services/servers
     * finish broadcasting
     */
    public void startGame(){
        Log.d("starting game...");

        // TODO: 25.12.2017 prepare objects for game

        mGame = new Game(getApplicationContext());
    }



}
