package com.votafore.warlords.v4.activities;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.R;
import com.votafore.warlords.v2.Constants;
import com.votafore.warlords.v2.ServiceInfo;
import com.votafore.warlords.v3.Log;
import com.votafore.warlords.v4.App;
import com.votafore.warlords.v4.network.ServerRemote;
import com.votafore.warlords.v4.test.DiscoveryListener;
import com.votafore.warlords.v4.test.ResolveListener;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.votafore.warlords.v2.Constants.LVL_ADAPTER;
import static com.votafore.warlords.v2.Constants.TAG_SCAN;
import static com.votafore.warlords.v2.Constants.TAG_SCAN_START;

public class ActivityStart extends AppCompatActivity {

    private App mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        mApp = (App) getApplication();

        Button create_btn = findViewById(R.id.create_btn);
        Button connect_btn = findViewById(R.id.connect_btn);

        final Intent local = new Intent(this, ActivityGameLocal.class);
        final Intent remote = new Intent(this, ActivityGameRemote.class);

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(local);
            }
        });

        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dsp_findServer = Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
                    @Override
                    public void subscribe(final ObservableEmitter<ServiceInfo> e){

                        final NsdManager manager = (NsdManager) getSystemService(Context.NSD_SERVICE);
                        final DiscoveryListener listener = new DiscoveryListener(){
                            @Override
                            public void onServiceFound(NsdServiceInfo serviceInfo) {

                                Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onServiceFound");

                                ServiceInfo svc_info = new ServiceInfo();
                                svc_info.messageType = ServiceInfo.SERVICE_FOUND;
                                svc_info.info = serviceInfo;

                                e.onNext(svc_info);
                            }
                        };

                        Log.d2(TAG_SCAN, LVL_ADAPTER, "CONN OBSERVABLE", "create disc. listener, start discover");
                        manager.discoverServices(Constants.SERVICETYPE, NsdManager.PROTOCOL_DNS_SD, listener);


                        e.setCancellable(new Cancellable() {
                            @Override
                            public void cancel() throws Exception {
                                Log.d2(TAG_SCAN, LVL_ADAPTER, "CONN OBSERVABLE", "cancel. stop discovery");
                                manager.stopServiceDiscovery(listener);
                            }
                        });
                    }
                })
                        // check if it is one of game services
                        .filter(new Predicate<ServiceInfo>() {
                            @Override
                            public boolean test(ServiceInfo serviceInfo) throws Exception {
                                return serviceInfo.info.getServiceName().contains(Constants.SERVICENAME)
                                        && serviceInfo.messageType.equals(ServiceInfo.SERVICE_FOUND)
                                        && (!serviceInfo.info.getHost().toString().replace("/", "").equals(mApp.getDeviceIP()));
                            }
                        })
                        // convert unresolved NsdInfo into resolved NsdInfo
                        .flatMap(new Function<ServiceInfo, Observable<NsdServiceInfo>>() {
                            @Override
                            public Observable<NsdServiceInfo> apply(final ServiceInfo serviceInfo) throws Exception {

                                Log.d3(TAG_SCAN, LVL_ADAPTER, "FOUND", "RESOLVE", "create observable");

                                return Observable.create(new ObservableOnSubscribe<NsdServiceInfo>() {
                                    @Override
                                    public void subscribe(final ObservableEmitter<NsdServiceInfo> e) {

                                        Log.d3(TAG_SCAN, LVL_ADAPTER, "FOUND", "RESOLVE", "start");

                                        NsdManager manager = (NsdManager) getSystemService(Context.NSD_SERVICE);
                                        manager.resolveService(serviceInfo.info, new ResolveListener() {

                                            @Override
                                            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                                                super.onServiceResolved(serviceInfo);
                                                e.onNext(serviceInfo);
                                            }
                                        });

                                    }
                                });
                            }
                        })
                        .subscribe(new Consumer<NsdServiceInfo>() {
                            @Override
                            public void accept(NsdServiceInfo serviceInfo) throws Exception {

                                ServerRemote serverRemote = new ServerRemote(serviceInfo.getHost(), serviceInfo.getPort());
                                mApp.setServer(serverRemote);

                                startActivity(remote);

                            }
                        });

            }
        });
    }

    Disposable dsp_findServer;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dsp_findServer.dispose();
    }
}
