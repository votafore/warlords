package com.votafore.warlords;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import java.io.IOException;
import java.net.ServerSocket;

public class ActivityStart extends AppCompatActivity {

    GameManager manager;

    Toolbar mToolbar;


//    NsdManager mNsdManager;
//
//    @Override
//    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
//        Log.v("GAMESERVICE", "onRegistrationFailed");
//    }
//
//    @Override
//    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
//        Log.v("GAMESERVICE", "onUnregistrationFailed");
//    }
//
//    @Override
//    public void onServiceRegistered(NsdServiceInfo serviceInfo) {
//        Log.v("GAMESERVICE", "onServiceRegistered. Имя сервиса: " + serviceInfo.getServiceName());
//    }
//
//    @Override
//    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
//        Log.v("GAMESERVICE", "onServiceUnregistered");
//    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);



        manager = GameManager.getInstance(this);

        RecyclerView serverList = (RecyclerView) findViewById(R.id.list_servers);

        serverList.setHasFixedSize(true);
        serverList.setItemAnimator(new DefaultItemAnimator());
        serverList.setLayoutManager(new LinearLayoutManager(this));

        serverList.setAdapter(manager.getAdapter());

        //mNsdManager = (NsdManager) getSystemService(NSD_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gamelist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.refresh:

                manager.discoverServers(this);
                break;

            case R.id.new_game:

                manager.createServer();

//                NsdServiceInfo info;
//
//                info = new NsdServiceInfo();
//                info.setServiceName(manager.mServiceName);
//                info.setServiceType(manager.mServiceType);
//
//                try {
//                    ServerSocket sock = new ServerSocket(0);
//
//                    info.setPort(sock.getLocalPort());
//
//                    sock.close();
//
//                    mNsdManager = (NsdManager) getSystemService(NSD_SERVICE);
//
//                    Log.v("GAMESERVICE", "включаем транслящию сервиса. port: " + String.valueOf(info.getPort()));
//                    mNsdManager.registerService(info, NsdManager.PROTOCOL_DNS_SD, this);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                break;

            case R.id.stop_server:

                manager.stopBroadcastService();
                manager.stopServer();

                //mNsdManager.unregisterService(this);

                break;

            case R.id.call_someFunc:

                manager.someFunc();

                break;
            case R.id.stop_client:

                manager.stopClient();
                break;

            case R.id.stop_discovery:

                manager.stopServiceDiscovery();

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
