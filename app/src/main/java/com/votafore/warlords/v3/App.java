package com.votafore.warlords.v3;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.opengl.GLSurfaceView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.ConnectableObservable;

import static com.votafore.warlords.v2.Constants.LVL_APP;
import static com.votafore.warlords.v2.Constants.TAG_APP_START;
import static com.votafore.warlords.v2.Constants.TAG_APP_STOP;
import static com.votafore.warlords.v2.Constants.LVL_ADAPTER;
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

    Disposable dsp_netWatcherEmitter;
    Disposable dsp_netWatcherSubscriber;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG_APP_START, "starting...");

        refreshIP();

        ConnectableObservable<Intent> mNetWatcher = Observable.create(new ObservableOnSubscribe<Intent>() {
            @Override
            public void subscribe(final ObservableEmitter<Intent> e) throws Exception {

                Log.d1(TAG_APP_START, LVL_NW_WATCHER, "create");
                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        Log.d1("", LVL_NW_WATCHER, "onReceive");

                        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                            Log.d1("", LVL_NW_WATCHER, "network state changed");
                            e.onNext(intent);
                        }
                    }
                };

                IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);

                Log.d1(TAG_APP_START, LVL_NW_WATCHER, "register");
                registerReceiver(receiver, filter);

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        Log.d1(TAG_APP_START, LVL_NW_WATCHER, "unregister watcher");
                        unregisterReceiver(receiver);
                    }
                });
            }
        })
        .publish();

        dsp_netWatcherEmitter = mNetWatcher.connect();

        dsp_netWatcherSubscriber = mNetWatcher.subscribe(new Consumer<Intent>() {
            @Override
            public void accept(Intent intent) throws Exception {

                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                mDeviceIP = "0.0.0.0";

                if(info.isConnected())
                    refreshIP();
            }
        });


        Log.d1(TAG_APP_START, LVL_ADAPTER, "create");
        mServerListAdapter = new AdapterServerList(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // TODO: 28.12.2017  not work.. define where it can be done
        Log.setTAG(TAG_APP_STOP);
        Log.d("stopping...");

//        Log.d(String.format(format1, LVL_NW_WATCHER, "unregister"));
//        unregisterReceiver(mNetReceiver);
    }


    public void stopApp(){

        // stop emitting new network state
        dsp_netWatcherEmitter.dispose();

        // stop observing network state
        dsp_netWatcherSubscriber.dispose();
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

        Log.d1(TAG_SRV_CRT, LVL_APP, "creating...");
        mServer = new ServerLocal();
        Log.d1(TAG_SRV_CRT, LVL_APP, "created");

        Log.d1(TAG_SRV_START, LVL_APP, "starting...");
        mServer.start(this);
        Log.d1(TAG_SRV_START, LVL_APP, "started");
    }

    public void stopServer(){

        Log.d1(TAG_SRV_STOP, LVL_APP, "stopping...");

        if(mServer != null)
            mServer.stop();

        mServer = null;

        Log.d1(TAG_SRV_STOP, LVL_APP, "stopped");
    }

    public IServer getServer(){
        return mServer;
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
        mGame.setServer(getSelected());
        mGame.start();

    }

    public GLSurfaceView getView(){
        return mGame.getSurfaceView();
    }















    // для автоподключения устройств опробовать возможность использования WIFI_AWARE_SERVICE из getSystemService
    // https://developer.android.com/guide/topics/connectivity/wifi-aware.html
}
