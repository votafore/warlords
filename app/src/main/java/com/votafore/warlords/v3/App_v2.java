package com.votafore.warlords.v3;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.votafore.warlords.v2.Constants;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

//import android.util.Log;

/**
 * @author Vorafore
 * Created on 18.12.2017.
 */

public class App_v2 extends Application {

    //String TAG = Constants.TAG;

//    String TAG = String.format(Constants.format, Constants.TAG+"_"+Constants.SRV);
//
//
//    String prefix= Constants.PFX_APP;
//
//    String format1 = Constants.format1;
//    String format2 = Constants.format2;
//    String format3 = Constants.format3;
//    String format4 = Constants.format4;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.setTAG(Constants.TAG_APP_START);
        Log.d("starting...");

        refreshIP();

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(String.format(Constants.format2, "APP", "NETWORK_STATE_WATCHER", "onReceive"));
                refreshIP();
            }
        };

        Log.d(String.format(Constants.format1,"NETWORK_STATE_WATCHER", "created"));

        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter);

        Log.d(String.format(Constants.format1,"NETWORK_STATE_WATCHER", "registered"));

        mServerListAdapter = new AdapterServerList(this);
        Log.d(String.format(Constants.format1,"ADAPTER", "created"));

        Log.reset();
    }








    /***************** page before game *******************/

    /**
     * adapter for list of servers
     */
    private AdapterServerList mServerListAdapter;

    public AdapterServerList getAdapter(){

        Log.d("getAdapter");

        if(mServerListAdapter == null)
            initListPage();

        return mServerListAdapter;
    }







    private void initListPage(){
        //Log.v(TAG, String.format(format1, prefix, "initListPage"));
        mServerListAdapter  = new AdapterServerList(this);
    }

    public void finishListPage(){

        //Log.v(TAG, String.format(format1, prefix, "finishListPage"));

        //Log.v(TAG, String.format(format2, prefix, "finishListPage", "stop scanning"));
        mServerListAdapter.stopScan();
        //Log.v(TAG, String.format(format2, prefix, "finishListPage", "scanning stopped"));

//        mServerManager = null;
        mServerListAdapter = null;
    }


    // TODO: 18.12.2017 make start of broadcasting

    private IServer mServer;

    public void createServer(){

        //Log.v(TAG, String.format(format1, prefix, "createServer"));

        //Log.v(TAG, String.format(format2, prefix, "createServer", "create local server"));
        mServer = new ServerLocal();
        //Log.v(TAG, String.format(format2, prefix, "createServer", "server has been created"));

        //Log.v(TAG, String.format(format2, prefix, "createServer", "start server"));
        mServer.start(this);
        //Log.v(TAG, String.format(format2, prefix, "createServer", "server has been started"));

        //Log.v(TAG, String.format(format2, prefix, "createServer", "add server to list"));
        mServerListAdapter.addLocalServer(mServer);
        //Log.v(TAG, String.format(format2, prefix, "createServer", "server has been added to list"));

    }

    public void TEST_stopServer(){

        //Log.v(TAG, String.format(format1, prefix, "TEST_stopServer"));

        //Log.v(TAG, String.format(format2, prefix, "TEST_stopServer", "remove server form list"));
        mServerListAdapter.removeLocalServer();
        //Log.v(TAG, String.format(format2, prefix, "TEST_stopServer", "server has been removed"));

        //Log.v(TAG, String.format(format2, prefix, "TEST_stopServer", "stop server"));
        mServer.stop();
        //Log.v(TAG, String.format(format2, prefix, "TEST_stopServer", "server has been stopped"));
    }

    public IServer getServer(){
        //Log.v(TAG, String.format(format1, prefix, "getServer"));
        return mServer;
    }










    /***************** UTILS **************/

    private String mDeviceIP = "";

    public String getDeviceIP(){
        //Log.v(TAG, String.format(format1, prefix, "getDeviceIP"));
        return mDeviceIP;
    }

    private void refreshIP(){

        //Log.v(TAG, String.format(format1, prefix, "refreshIP"));
        Log.d("refreshIP");

//        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        int ip = wifiManager.getConnectionInfo().getIpAddress();
//        mDeviceIP = Formatter.formatIpAddress(ip);

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


    /**
     * transition: list of servers -> server settings
     */

//    private com.votafore.warlords.v2.AdapterServerList.ListItem mSelected;
//
//    public void setSelected(com.votafore.warlords.v2.AdapterServerList.ListItem item){
//        mSelected = item;
//    }

//    public AdapterServerList.ListItem getSelected(){
//        return mSelected;
//    }



    /**
     * START GAME !!!!!!!!!!
     * finish scanning for services/servers
     * finish broadcasting
     */
    public void startGame(){

        //Log.v(TAG, String.format(format1, prefix, "startGame"));

        finishListPage();

        // TODO: 25.12.2017 prepare objects for game
    }



}
