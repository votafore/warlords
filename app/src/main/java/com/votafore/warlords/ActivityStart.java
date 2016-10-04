package com.votafore.warlords;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityStart extends AppCompatActivity {

    GameManager manager;

    Toolbar mToolbar;


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

                break;

            case R.id.stop_server:

                manager.stopBroadcastService();
                manager.stopServer();

                break;

            case R.id.call_someFunc:

                manager.someFunc();

                break;
            case R.id.stop_client:

                manager.stopClient();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
