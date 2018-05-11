package com.votafore.warlords.network;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;


import com.votafore.warlords.utils.ServiceInfo;
import com.votafore.warlords.constant.Constants;
import com.votafore.warlords.constant.Log;
import com.votafore.warlords.utils.DiscoveryListener;
import com.votafore.warlords.utils.ResolveListener;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

import static com.votafore.warlords.constant.Constants.LVL_REMOTE_SERVER;
import static com.votafore.warlords.constant.Constants.TAG_DATA_SEND;
import static com.votafore.warlords.constant.Constants.TAG_SOCKET;
import static com.votafore.warlords.constant.Constants.TAG_SRV_CRT;
import static com.votafore.warlords.constant.Constants.TAG_SRV_START;
import static com.votafore.warlords.constant.Constants.TAG_SRV_STOP;
import static com.votafore.warlords.constant.Constants.LVL_ADAPTER;
import static com.votafore.warlords.constant.Constants.TAG_SCAN;
import static com.votafore.warlords.constant.Constants.TAG_SCAN_START;

/**
 * @author Vorafore
 * Created on 26.12.2017.
 *
 * implementation for IServer (for remote server)
 */

public class ServerRemote implements IServer {

    /********************* IServer ********************/

    /**
     * object that send information
     */
    private PublishProcessor<JSONObject> sender;

    @Override
    public Disposable setReceiver(Consumer<JSONObject> receiver) {
        Log.d1("", LVL_REMOTE_SERVER, "subscribe client to incoming data");
        return mSocket.setReceiver(receiver);
    }

    @Override
    public void send(JSONObject data) {
        Log.d1(TAG_DATA_SEND, LVL_REMOTE_SERVER, "send data");
        sender.onNext(data);
    }

    private ISocket mSocket;

    @Override
    public void start() {

//        Log.d1(TAG_SRV_START, LVL_REMOTE_SERVER, "create socket");
//        mSocket = Socket.create(mIP, mPort);
//
//        Log.d1(TAG_SOCKET, LVL_REMOTE_SERVER, "set up socket in server");
//        Log.d1(TAG_SOCKET, LVL_REMOTE_SERVER, "set socket as subscriber for sender");
//        mSocket.subscribeSocket(sender);
    }

    @Override
    public void stop() {
        Log.d1(TAG_SRV_STOP, LVL_REMOTE_SERVER, "sender.onComplete()");
        sender.onComplete();

        mSearchingListener = null;
    }



    @Override
    public void startSearching(final Context context) {

        if(mSearchingListener != null)
            mSearchingListener.onSearchingStart();

        dsp_findServer = Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<ServiceInfo> e){

                final NsdManager manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
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
                        && serviceInfo.messageType.equals(ServiceInfo.SERVICE_FOUND);
                        //&& (!serviceInfo.info.getHost().toString().replace("/", "").equals(App.getInstance().getDeviceIP()));
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

                        NsdManager manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
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

                Log.d1(TAG_SRV_START, LVL_REMOTE_SERVER, "create socket");
                mSocket = Socket.create(serviceInfo.getHost(), serviceInfo.getPort());

                Log.d1(TAG_SOCKET, LVL_REMOTE_SERVER, "set up socket in server");
                Log.d1(TAG_SOCKET, LVL_REMOTE_SERVER, "set socket as subscriber for sender");
                mSocket.subscribeSocket(sender);

                dsp_waiterForRegistration = mSocket.setReceiver(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject response) throws Exception {

                        if(response.get("type").equals("response")
                                && response.get("data").equals("registration")){

                            dsp_waiterForRegistration.dispose();
                            stopSearching();
                        }
                    }
                });

                JSONObject query = new JSONObject();
                query.put("type", "info");
                query.put("data", "registration");

                mSocket.send(query);

            }
        });
    }

    Disposable dsp_waiterForRegistration;

    @Override
    public void stopSearching() {

        dsp_findServer.dispose();

        if(mSearchingListener != null)
            mSearchingListener.onSearchingEnd();

        mSearchingListener = null;
    }

    /****************** ServerRemote ******************/

//    private InetAddress mIP;
//    private int mPort;

    public ServerRemote(){

        Log.d1(TAG_SRV_CRT, LVL_REMOTE_SERVER, "create sender");

        sender = PublishProcessor.create();
        sender.observeOn(Schedulers.io());

//        mIP = ip;
//        mPort = port;
    }




    /***************** misc ********************/

//    @Override
//    public String toString(){
//        return mIP.toString();
//    }





    /******* test **********/

    Disposable dsp_findServer;

    private ISearchingListener mSearchingListener;

    public void setSearchingListener(ISearchingListener listener){
        mSearchingListener = listener;
    }

}
