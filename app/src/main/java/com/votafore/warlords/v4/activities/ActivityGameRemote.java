package com.votafore.warlords.v4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.ActivityMain;
import com.votafore.warlords.R;
import com.votafore.warlords.v4.App;
import com.votafore.warlords.v4.network.ServerRemote;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;


public class ActivityGameRemote extends AppCompatActivity {

    private App mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mApp = (App) getApplication();

        String ip = getIntent().getStringExtra("IP");
        int port = getIntent().getIntExtra("port", 0);

        final ServerRemote server;

        try {
            server = new ServerRemote(InetAddress.getByName(ip.replace("/", "")), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }

        mApp.setServer(server);

        Button ready = findViewById(R.id.ready);

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject query = new JSONObject();

                try {
                    query.put("ID"   , "11236534");
                    query.put("state", "ready");

                    server.send(query);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        server.setReceiver(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject object) throws Exception {

                if(!object.has("event") || !object.getString("event").equals("StartGame"))
                    return;

                mApp.startGame();
                startActivity(new Intent(ActivityGameRemote.this, ActivityMain.class));

                // TODO: 11.01.2018 пока нельзя закрыть активити т.к. эта команда должна прийти с сервера
                finish();
            }
        });
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
                //mApp.createServer();

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
//                mApp.stopServer();
                break;

            case R.id.start_scan:

                //mApp.getAdapter().startScan(this);
                break;

            case R.id.stop_scanning:

                //mApp.getAdapter().stopScan();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}