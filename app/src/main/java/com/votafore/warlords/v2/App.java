package com.votafore.warlords.v2;

import android.app.Application;
import android.net.nsd.NsdServiceInfo;

import com.votafore.warlords.v2.test2.Server;

import io.reactivex.functions.Consumer;

/**
 * @author Vorafore
 * Created on 18.12.2017.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
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
        mServerListAdapter  = new AdapterServerList();
    }

    public void finishListPage(){

//        mServerManager.stopScanning();
//
//        mServerManager = null;
//        mServerListAdapter = null;
    }


    // TODO: 18.12.2017 make start of broadcasting

    private Server mServer;

    public void createServer(){

        mServer = new Server();
        mServer.start(getApplicationContext());

    }

    public void TEST_stopServer(){
        mServer.stop();
    }

    public Server getServer(){
        return mServer;
    }
}
