package com.votafore.warlords.v3;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
//import android.util.Log;

import com.votafore.warlords.v2.Constants;
import com.votafore.warlords.v2.IDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
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

    String TAG = Constants.TAG;
    String prefix= Constants.PFX_LOCAL_SERVER;

    String format1 = Constants.format1;
    String format2 = Constants.format2;
    String format3 = Constants.format3;
    String format4 = Constants.format4;


    /************* IServer *******************/

    /**
     * object that send information
     */
    private PublishProcessor<JSONObject> sender;

    /**
     * object that accept socket connections
     */
    private Disposable dsp_sockets;

    /**
     * object for handling broadcasting
     */
    private Disposable dsp_broadcast;


    @Override
    public Disposable setReceiver(Consumer<JSONObject> receiver) {
        //Log.v(TAG, String.format(format1, prefix, "client's receiver set"));
        return sender.subscribe(receiver);
    }

    @Override
    public void send(JSONObject data) {
        //Log.v(TAG, String.format(format1, prefix, "send"));
        handleRequest(data);
    }

    @Override
    public void start(final Context context) {

        Log.d("create ServerSocket");
        final ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Log.d("create appender");

        // appender of sockets to channel
        dsp_sockets = Observable.create(new ObservableOnSubscribe<ISocket>() {
            @Override
            public void subscribe(ObservableEmitter<ISocket> e) throws Exception {

                while(!serverSocket.isClosed()){
                    try{
                        //Log.v(TAG, String.format(format3, prefix, "OBSERVER", "ServerSocket", "waiting for connection"));
                        Log.d("waiting for connection");
                        ISocket s = Socket.create(serverSocket.accept());
                        //Log.v(TAG, String.format(format3, prefix, "OBSERVER", "ServerSocket", "new socket created"));
                        e.onNext(s);
                    }catch(SocketException ex){
                        ex.printStackTrace();
                    }
                }
            }
        })
        .subscribeOn(Schedulers.io())
        .subscribe(getSocketAppender());


        Log.d("create broadcaster of service");

        // observable/subscriber that trigger when server created for starting broadcast
        dsp_broadcast = Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> e) throws Exception {

                final NsdManager manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
                final NsdManager.RegistrationListener listener = new NsdManager.RegistrationListener() {
                    @Override
                    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                        //Log.d(String.format(Constants.format1, Constants.LVL_LOCAL_SERVER, "onRegistrationFailed"));
                        Log.d("onRegistrationFailed");
                    }

                    @Override
                    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                        //Log.d(String.format(Constants.format1, Constants.LVL_LOCAL_SERVER, "onUnregistrationFailed"));
                        Log.d("onUnregistrationFailed");
                    }

                    @Override
                    public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                        //Log.d(String.format(Constants.format1, Constants.LVL_LOCAL_SERVER, "onServiceRegistered"));
                        Log.d("onServiceRegistered");
                    }

                    @Override
                    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                        //Log.d(String.format(Constants.format1, Constants.LVL_LOCAL_SERVER, "onServiceUnregistered"));
                        Log.d("onServiceUnregistered");
                    }
                };

                NsdServiceInfo regInfo = new NsdServiceInfo();
                regInfo.setServiceName(Constants.SERVICENAME);
                regInfo.setServiceType(Constants.SERVICETYPE);
                regInfo.setPort(serverSocket.getLocalPort());

                Log.d("register service");
                manager.registerService(regInfo, NsdManager.PROTOCOL_DNS_SD, listener);

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        Log.d("unregister service");
                        manager.unregisterService(listener);
                        //Log.v(TAG, String.format(format4, prefix, "OBSERVER", "Broadcasting", "subscribtion", "cancel: close server socket"));
                        Log.d("close server socket");
                        serverSocket.close();
                    }
                });
            }
        }).subscribe();
    }

    @Override
    public void stop() {

        Log.d("sender.onComplete()");
        sender.onComplete();

        // TODO: 26.12.2017 define moments when these objects have to be disposed
        Log.d("dispose socket observer");
        dsp_sockets.dispose();

        // for example broadcasting could be finished before server's Stop method called
        if(!dsp_broadcast.isDisposed())
        {
            Log.d("dispose broadcasting observer");
            dsp_broadcast.dispose();
        }

        // TODO: 26.12.2017 check if dispose in map_dsp is necessary

        // TODO: 27.12.2017 may be it is worth to place all observers in CompositeDisposable
    }



    /****************** ServerLocal ******************/

    public ServerLocal(){

        Log.d("create sender");
        sender = PublishProcessor.create();
        // TODO: 26.12.2017 specify thread for broadcaster

        Log.d("create maps");
        map_dsp_sender = new HashMap<>();
        map_dsp_receiver = new HashMap<>();
    }


    private void handleRequest(JSONObject data){

        //Log.v(TAG, String.format(format1, prefix, "handleRequest"));

        try{

            if(data.getString("type").equals("request") && data.getString("data").equals("ServerInfo")){

                //Log.v(TAG, String.format(format2, prefix, "handleRequest", "service info request received"));

                JSONObject response = new JSONObject();
                response.put("owner", new Date().toString());
                response.put("count", "");

                //Log.v(TAG, String.format(format2, prefix, "handleRequest", "send response"));
                sender.onNext(response);
            }

        }catch(JSONException je){
            je.printStackTrace();
            //Log.v(TAG, String.format(format2, prefix, "handleRequest", "error"));
        }
    }






    /**************** misc ******************/

    private Map<ISocket, Disposable> map_dsp_sender;
    private Map<ISocket, Disposable> map_dsp_receiver;

    /**
     * @return object that specify behaviour when new socket connected
     */
    private Consumer<ISocket> getSocketAppender(){

        return new Consumer<ISocket>() {
            @Override
            public void accept(final ISocket iSocket) throws Exception {

                //Log.v(TAG, String.format(format3, prefix, "OBSERVER", "ServerSocket", "append socket"));

                map_dsp_sender.put(iSocket, sender.subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        //Log.v("TESTRX", ">>>>>>>>> Channel - socket subscriber. send request for server info into output: " + jsonObject.toString());
                        //Log.v(TAG, String.format(format4, prefix, "OBSERVER", "ServerSocket", "append socket", "socket set as subscriber of sender"));
                        iSocket.send(jsonObject);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        iSocket.close();
                        // method "dispose" will be called when receiver unsubscribe
                        // or, may be, it is not required
                    }
                }));

                map_dsp_receiver.put(iSocket, Observable.create(new ObservableOnSubscribe<JSONObject>() {
                    @Override
                    public void subscribe(final ObservableEmitter<JSONObject> e) throws Exception {

                        //Log.v(TAG, String.format(format4, prefix, "OBSERVER", "ServerSocket", "append socket", "set listener for incoming data"));

                        iSocket.setListener(new IDataReceiver<String>() {
                            @Override
                            public void onDataReceived(String data) {

                                if(data == null){
                                    //Log.v(TAG, String.format(format4, prefix, "OBSERVER", "SOCKET", "incoming data", "null, close socket"));
                                    e.onComplete();
                                    // TODO: 21.12.2017 close socket
                                }else{
                                    //Log.v("TESTRX", ">>>>>>>>> Channel - socket input. got data!!!! yahooooo");
                                    //Log.v(TAG, String.format(format4, prefix, "OBSERVER", "SOCKET", "incoming data", "data received"));
                                    try {
                                        e.onNext(new JSONObject(data));
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                })
                        //.subscribeOn(Schedulers.newThread())
                        .subscribe(new Consumer<JSONObject>() {
                            @Override
                            public void accept(JSONObject object) throws Exception {
                                //Log.v(TAG, String.format(format4, prefix, "OBSERVER", "SOCKET", "incoming data", "send for handling"));
                                handleRequest(object);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {

                                //Log.v("TESTRX", ">>>>>>>>> Channel - socket . FINISH ?????");
                                //Log.v(TAG, String.format(format3, prefix, "OBSERVER", "SOCKET", "FINISH"));

                                map_dsp_receiver.get(iSocket).dispose();
                                map_dsp_sender.remove(iSocket);

                                // TODO: 21.12.2017 check if current disposable is disposed
                                map_dsp_sender.remove(iSocket);
                            }
                        }));
            }
        };
    }

    @Override
    public String toString(){
        return "0.0.0.0";
    }

}