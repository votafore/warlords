package com.votafore.warlords;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.v3.App;
import com.votafore.warlords.v3.IServer;
import com.votafore.warlords.v3.Log;
import com.votafore.warlords.v3.fragments.FragmentLocalServerSettings;
import com.votafore.warlords.v3.fragments.FragmentRemoteServerSettings;

import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ActivityGame extends AppCompatActivity {

    private App mApp;

    //private IServer mServer;

    private Disposable dsp_receiver;

    Button startGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mApp = (App) getApplication();

        startGame = findViewById(R.id.start_game);

        mApp.createServer();

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.setSelected(mApp.getServer());
                mApp.startGame();
                startActivity(new Intent(ActivityGame.this, ActivityMain.class));

                // TODO: 11.01.2018 пока нельзя закрыть активити т.к. эта команда должна прийти с сервера
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        dsp_receiver = mServer.setReceiver(new Consumer<JSONObject>() {
//            @Override
//            public void accept(JSONObject object) throws Exception {
//
//                // TODO: 28.12.2017 implement reaction
//
//                Log.d("Activity: some data received");
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //dsp_receiver.dispose();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mApp.stopServer();
    }
}
