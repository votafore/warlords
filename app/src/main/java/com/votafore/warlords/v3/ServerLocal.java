package com.votafore.warlords.v3;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import com.votafore.warlords.v2.Constants;

import org.json.JSONObject;

import java.net.ServerSocket;
import java.net.SocketException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Votafore
 * Created on 26.12.2017.
 *
 * implementation for IServer (for local server)
 */

public class ServerLocal implements IServer {

    /************* IServer *******************/

    /**
     * object that send information
     */
    private PublishProcessor<JSONObject> broadcaster;

    /**
     * object that listen sockets and deliver data to "client"
     */
    private Disposable dsp_sockets; // TODO: 26.12.2017 ???

    /**
     * object accept incoming socket connections
     */
    private Disposable dsp_broadcast;





    @Override
    public void setReceiver(Consumer<JSONObject> receiver) {

    }

    @Override
    public void send(JSONObject data) {
        broadcaster.onNext(data);
    }

    @Override
    public void start(final Context context) {

        broadcaster = PublishProcessor.create();
        // TODO: 26.12.2017 specify thread for broadcaster



        // general observable
        ConnectableObservable<ServerSocket> obs = Observable.create(new ObservableOnSubscribe<ServerSocket>() {
            @Override
            public void subscribe(ObservableEmitter<ServerSocket> e) throws Exception {
                final ServerSocket serverSocket = new ServerSocket(0);
                e.onNext(serverSocket);
            }
        }).publish();


        // appender of sockets to channel
        dsp_sockets = obs.flatMap(new Function<ServerSocket, ObservableSource<ISocket>>() {
            @Override
            public ObservableSource<ISocket> apply(final ServerSocket serverSocket) throws Exception {

                return Observable.create(new ObservableOnSubscribe<ISocket>() {
                    @Override
                    public void subscribe(ObservableEmitter<ISocket> e) throws Exception {
                        while(!serverSocket.isClosed()){
                            try{
                                ISocket s = Socket.create(serverSocket.accept());
                                e.onNext(s);
                            }catch(SocketException ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                }).subscribeOn(Schedulers.newThread());
            }
        })
        .subscribe(getSocketAppender());

        // observable/subscriber that trigger when server created for starting broadcast
        dsp_broadcast = obs.flatMap(new Function<ServerSocket, ObservableSource<?>>() {
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
        .subscribe();

        obs.connect();
    }

    @Override
    public void stop() {

        broadcaster.onComplete();
    }












    /*********** ServerLocal ******************/

    public ServerLocal(){

    }









    /**************** misc ******************/

    /**
     * @return object that specify behaviour when new socket connected
     */
    private Consumer<ISocket> getSocketAppender(){

        return new Consumer<ISocket>() {
            @Override
            public void accept(ISocket iSocket) throws Exception {

                // TODO: 26.12.2017 describe the behaviour
            }
        };
    }
}
