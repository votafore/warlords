package com.votafore.warlords.v2;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author Votafore
 * Created on 16.12.2017.
 *
 * responsibilities of class
 * - creating objects and processes for:
 *      - initialization
 *      - broadcasting connection service
 *      - discovering another connection services
 *      - making list of available games(or servers)
 */

public class ServerManager {

    String ServiceName = "Warlords";

    private Context mContext;
    private IAdapter mAdapter;

    public ServerManager(Context ctx, IAdapter adapter){

        mContext = ctx;
        mAdapter = adapter;

        isScanningStarted = false;

        init();
    }



    /*************************** broadcast service **********************/

    /**
     * discovering
     *
     * object that is subscribed on discovering
     */
    private Disposable dsp_discoveryFind;
    private Disposable dsp_discoveryLost;

    /**
     * discovering
     *
     * observable
     */
    private Observable<ServiceInfo> obs_discovery;

    /**
     * flag if scanning has already started
     */
    private boolean isScanningStarted;

    public void startScanning(){

        // TODO: 18.12.2017 may be it is redundant
        if(isScanningStarted)
            return;

        dsp_discoveryFind = obs_discovery
                .filter(new Predicate<ServiceInfo>() {
                    @Override
                    public boolean test(ServiceInfo serviceInfo) throws Exception {
                        // only game server that was found is interesting
                        return serviceInfo.messageType.equals(ServiceInfo.SERVICE_FOUND) && serviceInfo.info.getServiceName().contains(ServiceName);
                    }
                })
                .flatMap(new Function<ServiceInfo, Observable<ServiceInfo>>() {
                    @Override
                    public Observable<ServiceInfo> apply(final ServiceInfo serviceInfo) throws Exception {
                        //Log.v("TESTRX", "flatMap - apply");
                        return Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
                            @Override
                            public void subscribe(final ObservableEmitter<ServiceInfo> e) {
                                //Log.v("TESTRX", "2. subscribe");
                                NsdManager manager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
                                manager.resolveService(serviceInfo.info, new NsdManager.ResolveListener() {
                                    @Override
                                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                                        //Log.v("TESTRX", "2. onResolveFailed");
                                        e.onError(new Throwable(String.valueOf(errorCode)));
                                    }

                                    @Override
                                    public void onServiceResolved(NsdServiceInfo serviceInfo) {
                                        //Log.v("TESTRX", "2. onServiceResolved");

                                        ServiceInfo svc_info = new ServiceInfo();
                                        svc_info.messageType = ServiceInfo.SERVICE_FOUND;
                                        svc_info.info = serviceInfo;

                                        e.onNext(svc_info);
                                    }
                                });

                            }
                        });
                    }
                })
                .subscribe(new Consumer<ServiceInfo>() {
            @Override
            public void accept(ServiceInfo serviceInfo) throws Exception {
                mAdapter.addServer(serviceInfo);
                //Log.v("TESTRX", "server is added to list");
            }
        });

        dsp_discoveryLost = obs_discovery
                .filter(new Predicate<ServiceInfo>() {
                    @Override
                    public boolean test(ServiceInfo serviceInfo) throws Exception {
                        // only game server that was lost is interesting
                        return serviceInfo.messageType.equals(ServiceInfo.SERVICE_LOST) && serviceInfo.info.getServiceName().contains(ServiceName);
                    }
                })
                .flatMap(new Function<ServiceInfo, Observable<ServiceInfo>>() {
                    @Override
                    public Observable<ServiceInfo> apply(final ServiceInfo serviceInfo) throws Exception {
                        //Log.v("TESTRX", "flatMap - apply");
                        return Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
                            @Override
                            public void subscribe(final ObservableEmitter<ServiceInfo> e) {
                                //Log.v("TESTRX", "2. subscribe");
                                NsdManager manager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
                                manager.resolveService(serviceInfo.info, new NsdManager.ResolveListener() {
                                    @Override
                                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                                        //Log.v("TESTRX", "2. onResolveFailed");
                                        e.onError(new Throwable(String.valueOf(errorCode)));
                                    }

                                    @Override
                                    public void onServiceResolved(NsdServiceInfo serviceInfo) {
                                        //Log.v("TESTRX", "2. onServiceResolved");

                                        ServiceInfo svc_info = new ServiceInfo();
                                        svc_info.messageType = ServiceInfo.SERVICE_LOST;
                                        svc_info.info = serviceInfo;

                                        e.onNext(svc_info);
                                    }
                                });

                            }
                        });
                    }
                })
                .subscribe(new Consumer<ServiceInfo>() {
                    @Override
                    public void accept(ServiceInfo serviceInfo) throws Exception {
                        mAdapter.removeServer(serviceInfo);
                        //Log.v("TESTRX", "server is removed from list");
                    }
                });
    }

    public void stopScanning(){

        // TODO: 18.12.2017 mey be it is redundant
        if(!isScanningStarted)
            return;

        dsp_discoveryFind.dispose();
        dsp_discoveryLost.dispose();
    }


    /**
     * broadcasting
     *
     * object that is subscribed on broadcasting
     */
    private Disposable dsp_broadcasting;

    /**
     * broadcasting
     *
     * observer for state of broadcasting
     */
    private Observable<ServiceInfo> obs_broadcasting;


    public void startBroadcasting(final int port){

        dsp_broadcasting = Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> e) throws Exception {

                final NsdManager manager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
                final NsdManager.RegistrationListener listener = new NsdManager.RegistrationListener() {
                    @Override
                    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

                    }

                    @Override
                    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

                    }

                    @Override
                    public void onServiceRegistered(NsdServiceInfo serviceInfo) {

                    }

                    @Override
                    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

                    }
                };

                NsdServiceInfo regInfo = new NsdServiceInfo();
                regInfo.setServiceName(ServiceName);
                regInfo.setServiceType("_http._tcp.");
                regInfo.setPort(port);

                manager.registerService(regInfo, NsdManager.PROTOCOL_DNS_SD, listener);

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        manager.unregisterService(listener);
                    }
                });
            }
        }).subscribe();
    }

    public void stopBroadcasting(){
        dsp_broadcasting.dispose();
    }



    private void init(){

        obs_discovery = Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<ServiceInfo> e){
                NsdManager manager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
                manager.discoverServices("_http._tcp", NsdManager.PROTOCOL_DNS_SD, new NsdManager.DiscoveryListener() {
                    @Override
                    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                        //Log.v("TESTRX", "onStartDiscoveryFailed");
                        e.onError(new Throwable(serviceType));
                    }

                    @Override
                    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                        //Log.v("TESTRX", "onStopDiscoveryFailed");
                        e.onError(new Throwable(serviceType));
                    }

                    @Override
                    public void onDiscoveryStarted(String serviceType) {

                        //Log.v("TESTRX", "onDiscoveryStarted");

                        ServiceInfo svc_info = new ServiceInfo();
                        svc_info.messageType = ServiceInfo.SUCCESS_DISCOVERYSTART;
                        svc_info.serviceType = serviceType;

                        e.onNext(svc_info);
                    }

                    @Override
                    public void onDiscoveryStopped(String serviceType) {

                        //Log.v("TESTRX", "onDiscoveryStopped");

                        ServiceInfo svc_info = new ServiceInfo();
                        svc_info.messageType = ServiceInfo.SUCCESS_DISCOVERYSTOP;
                        svc_info.serviceType = serviceType;

                        e.onNext(svc_info);
                    }

                    @Override
                    public void onServiceFound(NsdServiceInfo serviceInfo) {

                        //Log.v("TESTRX", "onServiceFound");

                        ServiceInfo svc_info = new ServiceInfo();
                        svc_info.messageType = ServiceInfo.SERVICE_FOUND;
                        svc_info.info = serviceInfo;

                        e.onNext(svc_info);
                    }

                    @Override
                    public void onServiceLost(NsdServiceInfo serviceInfo) {

                        //Log.v("TESTRX", "onServiceLost");

                        ServiceInfo svc_info = new ServiceInfo();
                        svc_info.messageType = ServiceInfo.SERVICE_LOST;
                        svc_info.info = serviceInfo;

                        e.onNext(svc_info);
                    }
                });
            }
        });
    }




}