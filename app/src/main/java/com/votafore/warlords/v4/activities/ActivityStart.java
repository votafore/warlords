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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        Button create_btn = findViewById(R.id.create_btn);
        Button connect_btn = findViewById(R.id.connect_btn);

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityStart.this, ActivityGameLocal.class));
            }
        });

        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findServer();
            }
        });
    }

    Disposable dsp_findServer;

    private void findServer(){

        dsp_findServer = Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<ServiceInfo> e){

                final NsdManager manager = (NsdManager) getSystemService(Context.NSD_SERVICE);
                final NsdManager.DiscoveryListener listener = new NsdManager.DiscoveryListener() {
                    @Override
                    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onStartDiscoveryFailed");
                    }

                    @Override
                    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onStopDiscoveryFailed");
                    }

                    @Override
                    public void onDiscoveryStarted(String serviceType) {

                        // TODO: 22.12.2017 check if in is necessary
                        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onDiscoveryStarted");

//                        Log.v("TESTRX", "onDiscoveryStarted");

//                        ServiceInfo svc_info = new ServiceInfo();
//                        svc_info.messageType = ServiceInfo.SUCCESS_DISCOVERYSTART;
//                        svc_info.serviceType = serviceType;
//
//                        e.onNext(svc_info);
                    }

                    @Override
                    public void onDiscoveryStopped(String serviceType) {

                        // TODO: 22.12.2017 check if in is necessary
                        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onDiscoveryStopped");

//                        Log.v("TESTRX", "onDiscoveryStopped");
//
//                        ServiceInfo svc_info = new ServiceInfo();
//                        svc_info.messageType = ServiceInfo.SUCCESS_DISCOVERYSTOP;
//                        svc_info.serviceType = serviceType;
//
//                        e.onNext(svc_info);
                    }

                    @Override
                    public void onServiceFound(NsdServiceInfo serviceInfo) {

                        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onServiceFound");

                        ServiceInfo svc_info = new ServiceInfo();
                        svc_info.messageType = ServiceInfo.SERVICE_FOUND;
                        svc_info.info = serviceInfo;

                        e.onNext(svc_info);
                    }

                    @Override
                    public void onServiceLost(NsdServiceInfo serviceInfo) {

                        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onServiceLost");

                        ServiceInfo svc_info = new ServiceInfo();
                        svc_info.messageType = ServiceInfo.SERVICE_LOST;
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
                return serviceInfo.info.getServiceName().contains(Constants.SERVICENAME) && serviceInfo.messageType.equals(ServiceInfo.SERVICE_FOUND);
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
                                manager.resolveService(serviceInfo.info, new NsdManager.ResolveListener() {
                                    @Override
                                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                                        Log.d3(TAG_SCAN_START, "CONN OBSERVABLE", "FOUND", "RESOLVE", "onResolveFailed");
                                    }

                                    @Override
                                    public void onServiceResolved(NsdServiceInfo serviceInfo) {

                                        Log.d3(TAG_SCAN_START, "CONN OBSERVABLE", "FOUND", "RESOLVE", "onServiceResolved");
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
                        Intent i = new Intent(ActivityStart.this, ActivityGameRemote.class);

                        i.putExtra("IP", serviceInfo.getHost().toString());
                        i.putExtra("port", serviceInfo.getPort());

                        start(i);
                    }
                });

    }

    private void start(Intent i){
        startActivity(i);
        dsp_findServer.dispose();
        finish();
    }
}
