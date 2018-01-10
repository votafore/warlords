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

    private IServer mServer;

    private Disposable dsp_receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mApp = (App) getApplication();

        mServer = mApp.getSelected();

        Button startGame = findViewById(R.id.start_game);

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mApp.startGame();
//                startActivity(new Intent(ActivityGame.this, ActivityMain.class));
                finish();
            }
        });

        FragmentManager manager = getSupportFragmentManager();

        if(manager.findFragmentById(R.id.settings) == null){

            Fragment f;

            boolean isLocalServer = true;

            if(isLocalServer){
                f = new FragmentLocalServerSettings();
            }else{
                f = new FragmentRemoteServerSettings();
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.settings ,f)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        dsp_receiver = mServer.setReceiver(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject object) throws Exception {

                // TODO: 28.12.2017 implement reaction

                Log.d("Activity: some data received");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        dsp_receiver.dispose();
    }


}
