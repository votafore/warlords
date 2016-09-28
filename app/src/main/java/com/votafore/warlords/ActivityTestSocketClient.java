package com.votafore.warlords;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.votafore.warlords.game.Instance;

public class ActivityTestSocketClient extends AppCompatActivity implements View.OnClickListener{

    private TextView text;

    public static final int SERVERPORT = 6000;

    Button btn_close;
    Button btn_response;

    private Instance mInstance;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_socket);
        text = (TextView) findViewById(R.id.text2);

        btn_close               = (Button) findViewById(R.id.btn_close);
        btn_response            = (Button) findViewById(R.id.response);
        Button btn_startClient  = (Button) findViewById(R.id.btn_start_client);
        Button btn_startServer  = (Button) findViewById(R.id.btn_start_server);


        btn_close.setOnClickListener(this);
        btn_response.setOnClickListener(this);
        btn_startServer.setOnClickListener(this);
        btn_startClient.setOnClickListener(this);

        btn_close.setEnabled(false);
        btn_response.setEnabled(false);

//        mInstance = new Instance(this, 0);
//        mInstance.setConnectionType(Instance.TYPE_WIFI);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btn_start_server:

                btn_close.setEnabled(true);
                btn_response.setEnabled(true);

//                mInstance.setOwnerID(0);
//                mInstance.setPlayerID(0);
//
//                mInstance.startInstance();

                break;
            case R.id.btn_start_client:

                btn_close.setEnabled(true);
                btn_response.setEnabled(true);

//                mInstance.setOwnerID(0);
//                mInstance.setPlayerID(1);
//
//                mInstance.startInstance();

                break;
            case R.id.btn_close:

                btn_close.setEnabled(false);
                btn_response.setEnabled(false);

//                if(mInstance != null)
//                    mInstance.stopInstance();

                break;

            case R.id.response:

//                if(mInstance.mIsServer){
//                    mInstance.someServerFunc();
//                    mInstance.someLocalClientFunc();
//                }else {
//                    mInstance.someClientFunc();
//                }
        }
    }
}