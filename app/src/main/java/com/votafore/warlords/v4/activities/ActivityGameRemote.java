package com.votafore.warlords.v4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.ActivityMain;
import com.votafore.warlords.R;
import com.votafore.warlords.v4.App;

import org.json.JSONException;
import org.json.JSONObject;


import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class ActivityGameRemote extends AppCompatActivity {

    private App mApp;

    Disposable dsp_receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

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

        dsp_receiver = mApp.getServer().setReceiver(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject object) throws Exception {

                if(object.has("event") && object.getString("event").equals("socket is closing")){

                    dsp_receiver.dispose();
                    mApp.dismissServer();

                    finish();
                }

                if(object.has("event") && object.getString("event").equals("StartGame")){

                    mApp.startGame();

                    startActivity(new Intent(ActivityGameRemote.this, ActivityMain.class));
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mApp.dismissServer();
    }
}