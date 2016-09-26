package com.votafore.warlords;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.game.Instance;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ActivityStart extends AppCompatActivity implements View.OnClickListener {

    GameManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button btn = (Button) findViewById(R.id.btn_start);
        Button btn_test = (Button) findViewById(R.id.btn_testwifi);

        btn.setOnClickListener(this);
        btn_test.setOnClickListener(this);



//        try {
//            //Loop through all the network interface devices
//            for (Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces(); enumeration.hasMoreElements();) {
//                NetworkInterface networkInterface = enumeration.nextElement();
//                //Loop through all the ip addresses of the network interface devices
//                for (Enumeration<InetAddress> enumerationIpAddr = networkInterface.getInetAddresses(); enumerationIpAddr.hasMoreElements();) {
//                    InetAddress inetAddress = enumerationIpAddr.nextElement();
//                    //Filter out loopback address and other irrelevant ip addresses
//                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
//                        //Print the device ip address in to the text view
//                        btn_test.setText(inetAddress.getHostAddress());
//                    }
//                }
//            }
//        } catch (SocketException e) {
//            Log.e("ERROR:", e.toString());
//        }


//        manager = GameManager.getInstance();
//
//        // предполагается что будет возможность либо создать игру, либо присоединиться к созданной
//        // пока что создаем игру
//
//        Instance inst = new Instance(getApplicationContext());
//
//        // типа мы выбрали карту
//        //inst.setMap(new MeshMapTest(getApplicationContext()));
//
//        // установили ИД игрока, создавшего инстанс
//        inst.setPlayerID(0);
//
//        // установили ИД игрока, подключившегося к инстансу
//        inst.setOwnerID(2);
//
//        // еще типа указали вид подключения и создели его
//        // и запустили его (подключились)
//
//        // сказали что "... вот такие параметры боя..."
//        manager.setInstance(inst);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_start:

                manager.startGame(this);

                // наступил момент, когда статус "Готов к бою" установили все
                Intent i = new Intent(this, ActivityMain.class);
                startActivity(i);

                break;

            case R.id.btn_testwifi:

                //manager.getInstance().stopGame();
        }
    }
}
