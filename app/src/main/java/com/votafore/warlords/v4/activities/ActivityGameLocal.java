package com.votafore.warlords.v4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.ActivityMain;
import com.votafore.warlords.R;
import com.votafore.warlords.v4.App;
import com.votafore.warlords.v4.network.ServerLocal;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.functions.Consumer;

public class ActivityGameLocal extends AppCompatActivity {

    private App mApp;

    private ServerLocal mServerLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mApp = (App) getApplication();

        mServerLocal = new ServerLocal();

        mApp.setServer(mServerLocal);

        mServerLocal.startBroadcast(this);

        Button startGame = findViewById(R.id.start_game);

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject query = new JSONObject();

                try {
                    query.put("ID"   , "112365312");
                    query.put("state", "start");

                    mServerLocal.send(query);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        mServerLocal.setReceiver(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject object) throws Exception {

                if(!object.has("event") || !object.getString("event").equals("StartGame"))
                    return;

                mServerLocal.stopBroadcast();

                mApp.startGame();
                startActivity(new Intent(ActivityGameLocal.this, ActivityMain.class));

                // TODO: 11.01.2018 пока нельзя закрыть активити т.к. эта команда должна прийти с сервера
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mServerLocal.stopBroadcast();
        mServerLocal.stop();
    }

}
