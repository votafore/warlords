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

    Button btn_put;
    Button btn_get;

    private Instance mInstance;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_socket);
        text = (TextView) findViewById(R.id.text2);

        btn_put               = (Button) findViewById(R.id.btn_put);
        btn_get               = (Button) findViewById(R.id.btn_get);

        btn_put.setOnClickListener(this);
        btn_get.setOnClickListener(this);


//        mInstance = new Instance(this, 0);
//        mInstance.setConnectionType(Instance.TYPE_WIFI);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btn_put:



                break;
            case R.id.btn_get:


                break;
        }
    }
}