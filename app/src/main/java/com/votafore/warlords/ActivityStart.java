package com.votafore.warlords;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.votafore.warlords.v3.AdapterServerList;
import com.votafore.warlords.v3.App;


public class ActivityStart extends AppCompatActivity {

    private GameFactory   mFactory;

    private App mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mApp = (App) getApplication();


        RecyclerView serverList = (RecyclerView) findViewById(R.id.list_servers);

        serverList.setHasFixedSize(true);
        serverList.setItemAnimator(new DefaultItemAnimator());
        serverList.setLayoutManager(new LinearLayoutManager(this));

        //mFactory = GameFactory.getInstance();

        //mFactory.onActivityCreate(this);

        AdapterServerList adapter = mApp.getAdapter();

        serverList.setAdapter(adapter);

        //adapter.startScan(this);

//        mFactory.getAdapter().setListener(new GameFactory.ClickListener() {
//            @Override
//            public void onClick(int position) {
//
//                mFactory.startGame(position, ActivityStart.this);
//
//                finish();
//
//                Intent i = new Intent(ActivityStart.this, ActivityMain.class);
//                startActivity(i);
//            }
//        });
    }

    @Override
    public void onResume(){
        super.onResume();

        //Log.v(GameManager.TAG, "ActivityStart: onResume().");
        //mFactory.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Log.v(GameManager.TAG, "ActivityStart: onPause().");
        //mFactory.onActivityPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gamelist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.new_game:

                //mFactory.createServer(this);
                mApp.createServer();

                break;
//            case R.id.start_game:
//
//                mFactory.startGame(0, this);
//
//                break;
            case R.id.call_someFunc:

                //mFactory.someFunc();

                break;
            case R.id.exit:

                //mFactory.exit();
                mApp.stopServer();
                break;

            case R.id.start_scan:

                mApp.getAdapter().startScan(this);
                break;

            case R.id.stop_scanning:

                mApp.getAdapter().stopScan();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}