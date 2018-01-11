package com.votafore.warlords.v2;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.votafore.warlords.v2.versions.ServerManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;

/**
 * @author Vorafore
 * Created on 18.12.2017.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Log.v("TESTRX", "refresh IP");
        refreshIP();

        // start watching network start
        Observable.create(new ObservableOnSubscribe<Intent>() {
            @Override
            public void subscribe(final ObservableEmitter<Intent> e) throws Exception {

                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        e.onNext(intent);
                    }
                };

                IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                getApplicationContext().registerReceiver(receiver, filter);

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        getApplicationContext().unregisterReceiver(receiver);
                    }
                });
            }
        }).subscribe(new Consumer<Intent>() {
            @Override
            public void accept(Intent intent) throws Exception {
                Log.v("TESTRX", "refresh IP - subscriber");
                refreshIP();
            }
        });
    }








    /***************** page before game *******************/

    /**
     * adapter for list of servers
     */
    private AdapterServerList mServerListAdapter;

    public AdapterServerList getAdapter(){

        if(mServerListAdapter == null)
            initListPage();

        return mServerListAdapter;
    }

    /**
     * ServerManager
     */
    private ServerManager mServerManager;







    private void initListPage(){
        mServerListAdapter  = new AdapterServerList(this);
    }

    public void finishListPage(){

        mServerListAdapter.stopScan();

//        mServerManager = null;
        mServerListAdapter = null;
    }


    // T-ODO: 18.12.2017 make start of broadcasting

    private Server mServer;

    public void createServer(){

        mServer = new Server();
        mServer.start(getApplicationContext());

        mServerListAdapter.addLocalItem(mServer.getLocalItem());

    }

    public void TEST_stopServer(){
        mServerListAdapter.removeLocalItem();
        mServer.stop();
    }

    public Server getServer(){
        return mServer;
    }










    /***************** UTILS **************/

    private String mDeviceIP = "";

    public String getDeviceIP(){
        return mDeviceIP;
    }

    private void refreshIP(){

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        int ip = wifiManager.getConnectionInfo().getIpAddress();
        mDeviceIP = Formatter.formatIpAddress(ip);
        //String ip = "/" + Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

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
            Log.e("IP Address", ex.toString());
        }
    }








    /*************** TESTS *********************/


    /**
     * transition: list of servers -> server settings
     */

    private AdapterServerList.ListItem mSelected;

    public void setSelected(AdapterServerList.ListItem item){
        mSelected = item;
    }

    public AdapterServerList.ListItem getSelected(){
        return mSelected;
    }



    /**
     * START GAME !!!!!!!!!!
     * finish scanning for services/servers
     * finish broadcasting
     */
    public void startGame(){
        finishListPage();

        // T-ODO: 25.12.2017 prepare objects for game
    }



}
