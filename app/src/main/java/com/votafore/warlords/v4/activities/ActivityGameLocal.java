package com.votafore.warlords.v4.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.ActivityMain;
import com.votafore.warlords.R;
import com.votafore.warlords.v4.App;

import org.json.JSONException;
import org.json.JSONObject;

import static com.votafore.warlords.v4.App.EVENT_GAME_START;

public class ActivityGameLocal extends AppCompatActivity {

    private App mApp;

    BroadcastReceiver mStartGameReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sendBroadcast(new Intent(App.EVENT_LOCALSERVER_CREATE));

        mApp = (App) getApplication();

        Button startGame = findViewById(R.id.start_game);

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject query = new JSONObject();

                try {
                    query.put("ID"   , "112365312");
                    query.put("state", "start");

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendBroadcast(new Intent(App.EVENT_LOCALSERVER_DISMISS));
    }

}
