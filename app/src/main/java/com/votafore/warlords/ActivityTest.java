package com.votafore.warlords;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.v2.Constants;
import com.votafore.warlords.v3.App;
import com.votafore.warlords.v3.Log;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.votafore.warlords.v2.Constants.LVL_LOCAL_SERVER;

public class ActivityTest extends AppCompatActivity {

    Disposable dsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        App app = (App) getApplication();

        final NsdManager manager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        final NsdManager.RegistrationListener listener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "onRegistrationFailed");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "onUnregistrationFailed");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "onServiceRegistered");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "onServiceUnregistered");
            }
        };

        final NsdServiceInfo regInfo = new NsdServiceInfo();
        regInfo.setServiceName(Constants.SERVICENAME);
        regInfo.setServiceType(Constants.SERVICETYPE);
        regInfo.setPort(25001);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            regInfo.setAttribute("ownerName" , "developer");
            regInfo.setAttribute("message"   , "welcome !!! )");
        }

//        dsp = app.getNetWatcher().subscribe(new Consumer<Boolean>() {
//            @Override
//            public void accept(Boolean isNetAvailable) throws Exception {
//
//                if
//                manager.registerService(regInfo, NsdManager.PROTOCOL_DNS_SD, listener);
//            }
//        });


        Button start = findViewById(R.id.start);
        Button stop = findViewById(R.id.stop);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.registerService(regInfo, NsdManager.PROTOCOL_DNS_SD, listener);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.unregisterService(listener);
            }
        });
    }
}
