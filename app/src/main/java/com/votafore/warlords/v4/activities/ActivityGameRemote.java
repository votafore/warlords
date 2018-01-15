package com.votafore.warlords.v4.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.ActivityMain;
import com.votafore.warlords.R;
import com.votafore.warlords.v4.App;
import com.votafore.warlords.v4.constant.Log;

import org.json.JSONException;
import org.json.JSONObject;


import static com.votafore.warlords.v4.App.EVENT_GAME_START;
import static com.votafore.warlords.v4.constant.Constants.TAG_ACTIVITY_CREATE;


public class ActivityGameRemote extends AppCompatActivity {

    private App mApp;

    BroadcastReceiver mStartGameReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Log.d1(TAG_ACTIVITY_CREATE, "ACTIVITY", "onCreate");

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mApp = (App) getApplication();

        Button ready = findViewById(R.id.ready);

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject query = new JSONObject();

                try {
                    query.put("ID"   , "11236534");
                    query.put("state", "ready");

                    mApp.getServer().send(query);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mStartGameReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startActivity(new Intent(context, ActivityMain.class));
                finish();
            }
        };

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //mApp.dismissServer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(EVENT_GAME_START);

        registerReceiver(mStartGameReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mStartGameReceiver);
    }
}