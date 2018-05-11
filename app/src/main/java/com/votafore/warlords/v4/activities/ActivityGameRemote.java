//package com.votafore.warlords.v4.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.widget.Button;
//
//import com.votafore.warlords.ActivityMain;
//import com.votafore.warlords.R;
//import com.votafore.warlords.v4.App;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//
//
//public class ActivityGameRemote extends AppCompatActivity {
//
//    private App mApp;
//
//    Disposable dsp_receiver;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_start);
//
//        Toolbar mToolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//
//        mApp = (App) getApplication();
//
//        Button ready = findViewById(R.id.ready);
//
//        ready.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                JSONObject query = new JSONObject();
//
//                try {
//                    query.put("type" , "info");
//                    query.put("data" , "state");
//                    query.put("state", "ready");
//                    query.put("ID"   , "11236534");
//
//                    mApp.getServer().send(query);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        final Handler h = new Handler();
//
//        dsp_receiver = mApp.getServer().setReceiver(new Consumer<JSONObject>() {
//            @Override
//            public void accept(JSONObject response) throws Exception {
//
//                if(response.get("type").equals("info")){
//
//                    if(response.get("data").equals("CloseSocket")){
//
//                        dsp_receiver.dispose();
//                        mApp.dismissServer();
//
//                        finish();
//                    }
//
//                }else if(response.get("type").equals("GlobalEvent")){
//
//                    if(response.get("event").equals("StartGame")){
//
//                        h.post(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                mApp.startGame();
//
//                                startActivity(new Intent(ActivityGameRemote.this, ActivityGame.class));
//                                finish();
//                            }
//                        });
//                    }
//                }
//            }
//        });
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        mApp.dismissServer();
//    }
//}