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

import com.votafore.warlords.test.ServiceScanner;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

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


    ServiceScanner mScanner;


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

        mScanner = new ServiceScanner(this);
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

                //manager.discoverServers(this);
                mScanner.startScan();
                break;

            case R.id.new_game:

                manager.createServer();

                break;

            case R.id.stop_server:

                manager.stopBroadcastService();
                manager.stopServer();

                break;

            case R.id.call_someFunc:

                manager.someFunc();

                break;
            case R.id.stop_client:

                //manager.stopClient();
                mScanner.stopScan();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
