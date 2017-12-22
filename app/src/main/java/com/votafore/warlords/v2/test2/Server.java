package com.votafore.warlords.v2.test2;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.view.ContextMenu;

import com.votafore.warlords.v2.ServerManager;
import com.votafore.warlords.v2.test.Channel_v2;
import com.votafore.warlords.v2.test.Channel_v3;
import com.votafore.warlords.v2.test.Socket;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Votafore
 * Created on 21.12.2017.
 *
 * represents a server
 */

public class Server extends EndPoint {









    private Disposable dsp_broadcaster;

    public void waitForReadyWith(Consumer<NsdServiceInfo> info){


    }














    /****** under tests *********/

    Channel_v3 v3;
    Disposable d1;
    Disposable d2;

    public void stop(){

        d1.dispose();
        d2.dispose();

        v3.close();
    }



    public void test(final ServerManager manager){

        v3 = new Channel_v3();

        // general observable
        ConnectableObservable<ServerSocket> obs = Observable.create(new ObservableOnSubscribe<ServerSocket>() {
            @Override
            public void subscribe(ObservableEmitter<ServerSocket> e) throws Exception {

                ServerSocket serverSocket = new ServerSocket(0);
                Log.v("TESTRX", ">>>>>>>>> server created !!!!!!!! <<<<<<<<<<<");
                e.onNext(serverSocket);
            }
        })
                .publish();

        v3.setReceiver(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                Log.v("TESTRX", ">>>>>>>>> query has been received <<<<<<<<<<");
            }
        });

        // appender of sockets to channel
        d2 = obs.flatMap(new Function<ServerSocket, ObservableSource<Socket>>() {
            @Override
            public ObservableSource<Socket> apply(final ServerSocket serverSocket) throws Exception {

                return Observable.create(new ObservableOnSubscribe<Socket>() {
                    @Override
                    public void subscribe(ObservableEmitter<Socket> e) throws Exception {

                        while(!serverSocket.isClosed()){

                            Log.v("TESTRX", ">>>>>>>>> waiting for connection <<<<<<<<<<");
                            java.net.Socket s = serverSocket.accept();
                            Log.v("TESTRX", ">>>>>>>>> ACCEPTED !!!!! <<<<<<<<<<");
                            e.onNext(new Socket(s));
                        }
                    }
                }).subscribeOn(Schedulers.newThread());
            }
        })
                .observeOn(Schedulers.io())
                .subscribe(v3.getSubscriber());


        // observable/subscriber that trigger when server created for starting broadcas
        d1 = obs
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<ServerSocket>() {
                    @Override
                    public void accept(ServerSocket serverSocket) throws Exception {
                        Log.v("TESTRX", ">>>>>>>>> start broadcasting <<<<<<<<<<");
                        manager.startBroadcasting(serverSocket.getLocalPort());
                    }
                });

        obs.connect();
    }



}