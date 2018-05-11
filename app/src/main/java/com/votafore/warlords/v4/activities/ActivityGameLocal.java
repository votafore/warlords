package com.votafore.warlords.v4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.R;
import com.votafore.warlords.v4.App;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


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

        final Handler h = new Handler();

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject query = new JSONObject();

                try {

                    query.put("type", "GlobalEvent");
                    query.put("event", "StartGame");

                    mApp.getServer().send(query);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        dsp_receiver = mApp.getServer().setReceiver(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject response) throws Exception {

                if(response.get("type").equals("GlobalEvent")
                        && response.get("event").equals("StartGame")){

                    mApp.getServer().stopSearching();
                    mApp.startGame();

                    // старт игры запускаем в основном потоке
                    h.post(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(ActivityGameLocal.this, ActivityGame.class));
                            finish();
                        }
                    });
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
