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

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.votafore.warlords.v4.App.EVENT_GAME_START;

public class ActivityGameLocal extends AppCompatActivity {

    private App mApp;

    Disposable dsp_receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mApp = (App) getApplication();
        mApp.getServer().startSearching(this);

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

        dsp_receiver = mApp.getServer().setReceiver(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject object) throws Exception {

                if(object.has("event") && object.getString("event").equals("StartGame")){

                    mApp.startGame();

                    startActivity(new Intent(ActivityGameLocal.this, ActivityMain.class));
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
