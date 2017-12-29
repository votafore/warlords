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

import static com.votafore.warlords.v2.Constants.APP_START;
import static com.votafore.warlords.v2.Constants.APP_STOP;
import static com.votafore.warlords.v2.Constants.LVL_ADAPTER;
import static com.votafore.warlords.v2.Constants.LVL_LOCAL_SERVER;
import static com.votafore.warlords.v2.Constants.LVL_NW_WATCHER;
import static com.votafore.warlords.v2.Constants.SRV_CRT;
import static com.votafore.warlords.v2.Constants.SRV_START;
import static com.votafore.warlords.v2.Constants.SRV_STOP;
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

        Log.setTAG(APP_START);
        Log.d("starting...");

        refreshIP();

        Log.d(String.format(format1, LVL_NW_WATCHER, "create"));
        mNetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(String.format(format1, LVL_NW_WATCHER, "network state changed"));
                refreshIP();
            }
        };

        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        Log.d(String.format(format1, LVL_NW_WATCHER, "register"));
        registerReceiver(mNetReceiver, filter);

        Log.d(String.format(format1, LVL_ADAPTER, "create"));
        mServerListAdapter = new AdapterServerList(this);

        Log.reset();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // TODO: 28.12.2017  not work.. define where it can be done
        Log.setTAG(APP_STOP);
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

        Log.setTAG(SRV_CRT);
        Log.d("starting...");

        Log.setLevel(LVL_LOCAL_SERVER);

        Log.d("creating...");
        mServer = new ServerLocal();
        Log.d("created");

        Log.reset();


        Log.setTAG(SRV_START);
        Log.setLevel(LVL_LOCAL_SERVER);

        Log.d("starting...");
        mServer.start(this);
        Log.d("started");


        mServerListAdapter.addLocalServer(mServer);

        Log.reset();
    }

    public void stopServer(){

        mServerListAdapter.removeLocalServer();

        Log.setTAG(SRV_STOP);
        Log.d("stopping...");

        Log.setLevel(LVL_LOCAL_SERVER);

        Log.d("stopping...");
        mServer.stop();
        Log.d("stopped");

        Log.reset();
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
