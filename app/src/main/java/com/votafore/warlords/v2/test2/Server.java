package com.votafore.warlords.v2.test2;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.votafore.warlords.v2.AdapterServerList;
import com.votafore.warlords.v2.test.Channel;
import com.votafore.warlords.v2.test.Constants;
import com.votafore.warlords.v2.test.IChannel;
import com.votafore.warlords.v2.test.IDataListener;
import com.votafore.warlords.v2.test.ISocket;
import com.votafore.warlords.v2.test.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Votafore
 * Created on 21.12.2017.
 *
 * represents a server
 */

public class Server extends EndPoint {

    public Server(){

        mDisposables = new CompositeDisposable();

        mChannel = new Channel();

        mReceiver = new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {

                Log.v("TESTRX", "SERVER >>>>>>> query has been received");

                if(jsonObject.getString("type").equals("request") && jsonObject.getString("data").equals("ServerInfo")){

                    JSONObject response = new JSONObject();
                    response.put("owner", new Date().toString());
                    response.put("playerCount", 1);
                    mChannel.getSender().onNext(response);
                }
            }
        };
    }



    /**************** 1-st step of starting ***************/

    private Channel mChannel;
    private CompositeDisposable mDisposables;

    private Consumer<JSONObject> mReceiver;

    public void start(final Context context){

        // general observable
        ConnectableObservable<ServerSocket> obs = Observable.create(new ObservableOnSubscribe<ServerSocket>() {
            @Override
            public void subscribe(ObservableEmitter<ServerSocket> e) throws Exception {
                final ServerSocket serverSocket = new ServerSocket(0);
                e.onNext(serverSocket);
            }
        }).publish();

        mChannel.setReceiver(mReceiver);

        // appender of sockets to channel
        mDisposables.add(

                obs.flatMap(new Function<ServerSocket, ObservableSource<Socket>>() {
                    @Override
                    public ObservableSource<Socket> apply(final ServerSocket serverSocket) throws Exception {

                        return Observable.create(new ObservableOnSubscribe<Socket>() {
                            @Override
                            public void subscribe(ObservableEmitter<Socket> e) throws Exception {
                                while(!serverSocket.isClosed()){
                                    try{
                                        Socket s = new Socket(serverSocket.accept());
                                        e.onNext(s);
                                    }catch(SocketException ex){
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }).subscribeOn(Schedulers.newThread());
                    }
                })
                        .subscribe(mChannel.getSubscriber())
        );

        // observable/subscriber that trigger when server created for starting broadcast
        mDisposables.add(

                obs.flatMap(new Function<ServerSocket, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final ServerSocket serverSocket) throws Exception {

                        return Observable.create(new ObservableOnSubscribe<Void>() {
                            @Override
                            public void subscribe(ObservableEmitter<Void> e) throws Exception {

                                final NsdManager manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
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
                                regInfo.setServiceName(Constants.SERVICENAME);
                                regInfo.setServiceType(Constants.SERVICETYPE);
                                regInfo.setPort(serverSocket.getLocalPort());

                                manager.registerService(regInfo, NsdManager.PROTOCOL_DNS_SD, listener);

                                e.setCancellable(new Cancellable() {
                                    @Override
                                    public void cancel() throws Exception {
                                        manager.unregisterService(listener);
                                        serverSocket.close();
                                    }
                                });

                            }
                        });
                    }
                })
                .subscribe()
        );

        obs.connect();

        Log.v("TESTRX", "SERVER >>>>>>>    started");
    }

    public void stop(){

        mDisposables.dispose();

        mChannel.close();
    }





    /*************** settings for game that user may adjust *****************/

    private String userName = "<...>";






    /******** JSON handler *********/





    /****** TESTS **********/

    public AdapterServerList.ListItem getLocalItem(){

        Log.v("TESTRX", "SERVER >>>>>>>    create local item");

        LocalItem item = new LocalItem();

        return item;

    }


    public class LocalItem extends AdapterServerList.ListItem{

        public LocalItem(){

            mChannel = new IChannel() {
                @Override
                public PublishProcessor<JSONObject> getSender() {
                    PublishProcessor<JSONObject> localSender = PublishProcessor.create();
                    localSender.subscribe(mReceiver);

                    return localSender;
                }

                @Override
                public void setReceiver(Consumer<JSONObject> c) {
                    Server.this.mChannel.getSender().subscribe(c);
                }
            };

            mChannel.setReceiver(new Consumer<JSONObject>() {
                @Override
                public void accept(JSONObject jsonObject) throws Exception {

                    Log.v("TESTRX", "SERVER >>>>>>> LIST ITEM >>>>>>>   receiver is called");

                    try {
                        ownerName = jsonObject.getString("owner");
                        playerCount = jsonObject.getString("playerCount");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mListener.onItemChanged();
                }
            });
        }
    }
}
