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

import static com.votafore.warlords.v2.Constants.LVL_APP;
import static com.votafore.warlords.v2.Constants.LVL_LOCAL_SERVER;
import static com.votafore.warlords.v2.Constants.TAG_DATA_RECEIVE;
import static com.votafore.warlords.v2.Constants.TAG_DATA_SEND;
import static com.votafore.warlords.v2.Constants.TAG_SOCKET;
import static com.votafore.warlords.v2.Constants.TAG_SOCKET_CLOSE;
import static com.votafore.warlords.v2.Constants.TAG_SRV_CRT;
import static com.votafore.warlords.v2.Constants.TAG_SRV_START;
import static com.votafore.warlords.v2.Constants.TAG_SRV_STOP;

/**
 * @author Votafore
 * Created on 26.12.2017.
 *
 * implementation for IServer (for local server)
 */

public class ServerLocal implements IServer {

//    String TAG = Constants.TAG;
//    String prefix= Constants.PFX_LOCAL_SERVER;
//
//    String format1 = Constants.format1;
//    String format2 = Constants.format2;
//    String format3 = Constants.format3;
//    String format4 = Constants.format4;


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
        Log.d1("", LVL_LOCAL_SERVER, "set client");
        return sender.subscribe(receiver);
    }

    @Override
    public void send(JSONObject data) {
        Log.d1(TAG_DATA_SEND, LVL_LOCAL_SERVER, "send data");
        try {
            receiver.accept(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(final Context context) {

        Log.d1(TAG_SRV_START, LVL_LOCAL_SERVER, "create ServerSocket");

        final ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d1(TAG_SRV_START, LVL_LOCAL_SERVER, "ServerSocket not created");
            return;
        }

        //Log.d1(TAG_SRV_START, LVL_LOCAL_SERVER, "create appender");
        Log.d2(TAG_SRV_START, LVL_LOCAL_SERVER, "APPENDER", "create");

        // appender of sockets to channel
        dsp_sockets = Observable.create(new ObservableOnSubscribe<ISocket>() {
            @Override
            public void subscribe(ObservableEmitter<ISocket> e) throws Exception {

                while(!serverSocket.isClosed()){
                    try{
                        //Log.v(TAG, String.format(format3, prefix, "OBSERVER", "ServerSocket", "waiting for connection"));
                        Log.d2(TAG_SRV_START, LVL_LOCAL_SERVER, "APPENDER", "waiting for connection");
                        ISocket s = Socket.create(serverSocket.accept());
                        //Log.v(TAG, String.format(format3, prefix, "OBSERVER", "ServerSocket", "new socket created"));
                        e.onNext(s);
                    }catch(SocketException ex){
                        ex.printStackTrace();
                    }
                }

                Log.d2("", LVL_LOCAL_SERVER, "APPENDER", "serverSocket closed");
                // TODO: 29.12.2017 check... may be cancellable for closing serverSocket should be here
            }
        })
        .subscribeOn(Schedulers.newThread())
        .subscribe(getSocketAppender());

        //Log.d1(TAG_SRV_START, LVL_LOCAL_SERVER, "create broadcaster of service and start broadcasting");
        Log.d2(TAG_SRV_START, LVL_LOCAL_SERVER, "BROADCASTER", "create and start");

        // observable/subscriber that trigger when server created for starting broadcast
        dsp_broadcast = Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> e) throws Exception {

                final NsdManager manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
                final NsdManager.RegistrationListener listener = new NsdManager.RegistrationListener() {
                    @Override
                    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                        //Log.d(String.format(Constants.format1, Constants.LVL_LOCAL_SERVER, "onRegistrationFailed"));
                        //Log.d("onRegistrationFailed");
                        Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "onRegistrationFailed");
                    }

                    @Override
                    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                        //Log.d(String.format(Constants.format1, Constants.LVL_LOCAL_SERVER, "onUnregistrationFailed"));
                        //Log.d("onUnregistrationFailed");
                        Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "onUnregistrationFailed");
                    }

                    @Override
                    public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                        //Log.d(String.format(Constants.format1, Constants.LVL_LOCAL_SERVER, "onServiceRegistered"));
                        //Log.d("onServiceRegistered");
                        Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "onServiceRegistered");
                    }

                    @Override
                    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                        //Log.d(String.format(Constants.format1, Constants.LVL_LOCAL_SERVER, "onServiceUnregistered"));
                        //Log.d("onServiceUnregistered");
                        Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "onServiceUnregistered");
                    }
                };

                NsdServiceInfo regInfo = new NsdServiceInfo();
                regInfo.setServiceName(Constants.SERVICENAME);
                regInfo.setServiceType(Constants.SERVICETYPE);
                regInfo.setPort(serverSocket.getLocalPort());

                Log.d2(TAG_SRV_START, LVL_LOCAL_SERVER, "BROADCASTER", "register service");
                manager.registerService(regInfo, NsdManager.PROTOCOL_DNS_SD, listener);

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "unregister service");
                        manager.unregisterService(listener);
                        Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "close server socket");
                        serverSocket.close();
                    }
                });
            }
        }).subscribe();
    }

    @Override
    public void stop() {

        //Log.d("sender.onComplete()");
        Log.d1(TAG_SRV_STOP, LVL_LOCAL_SERVER, "sender.onComplete()");
        sender.onComplete();

        // TODO: 26.12.2017 define moments when these objects have to be disposed
        //Log.d("dispose socket observer");
        //Log.d1(TAG_SRV_STOP, LVL_LOCAL_SERVER, "dispose socket observer");
        Log.d2(TAG_SRV_STOP, LVL_LOCAL_SERVER, "APPENDER", "stop");
        dsp_sockets.dispose();

        // for example broadcasting could be finished before server's Stop method called
        if(!dsp_broadcast.isDisposed())
        {
            //Log.d("dispose broadcasting observer");
            //Log.d1(TAG_SRV_STOP, LVL_LOCAL_SERVER, "dispose broadcasting observer");
            Log.d2(TAG_SRV_STOP, LVL_LOCAL_SERVER, "BROADCASTER", "stop");
            dsp_broadcast.dispose();
        }

        // TODO: 26.12.2017 check if dispose in map_dsp is necessary

        // TODO: 27.12.2017 may be it is worth to place all observers in CompositeDisposable
    }



    /****************** ServerLocal ******************/

    private Consumer<JSONObject> receiver;

    public ServerLocal(){

        Log.d1(TAG_SRV_CRT, LVL_LOCAL_SERVER, "create sender");
        sender = PublishProcessor.create();
        // TODO: 26.12.2017 specify thread for broadcaster

        Log.d1(TAG_SRV_CRT, LVL_LOCAL_SERVER, "create maps");
        map_dsp_sender = new HashMap<>();
        map_dsp_receiver = new HashMap<>();

        Log.d1(TAG_SRV_CRT, LVL_LOCAL_SERVER, "create server receiver");
        receiver = new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject object) throws Exception {
                handleRequest(object);
            }
        };
    }


    synchronized private void handleRequest(JSONObject data){

        Log.d1(TAG_DATA_RECEIVE, LVL_LOCAL_SERVER, "handleRequest");

        try{

            if(data.getString("type").equals("request") && data.getString("data").equals("ServerInfo")){

                Log.d1(TAG_DATA_RECEIVE, LVL_LOCAL_SERVER, "this is request about service info");

                JSONObject response = new JSONObject();
                response.put("owner", new Date().toString());
                response.put("count", "1");

                Log.d1(TAG_DATA_SEND, LVL_LOCAL_SERVER, "send response");
                sender.onNext(response);
            }

        }catch(JSONException je){
            je.printStackTrace();
            Log.d1(TAG_DATA_RECEIVE, LVL_LOCAL_SERVER, "handleRequest - error");
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

                Log.d1(TAG_SOCKET, LVL_LOCAL_SERVER, "set up socket in server");


                Log.d1(TAG_SOCKET, LVL_LOCAL_SERVER, "set socket as subscriber for sender");
                iSocket.subscribeSocket(sender);

                Log.d1(TAG_SOCKET, LVL_LOCAL_SERVER, "subscribe server to incoming data");
                map_dsp_receiver.put(iSocket, iSocket.setReceiver(receiver));

//                map_dsp_sender.put(iSocket, sender.subscribe(new Consumer<JSONObject>() {
//                    @Override
//                    public void accept(JSONObject jsonObject) throws Exception {
//                        //Log.v("TESTRX", ">>>>>>>>> Channel - socket subscriber. send request for server info into output: " + jsonObject.toString());
//                        //Log.v(TAG, String.format(format4, prefix, "OBSERVER", "ServerSocket", "append socket", "socket set as subscriber of sender"));
//                        iSocket.send(jsonObject);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//
//                    }
//                }, new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        Log.d1(TAG_SOCKET_CLOSE, LVL_LOCAL_SERVER, "close");
//                        iSocket.close();
//                        // method "dispose" will be called when receiver unsubscribe
//                        // or, may be, it is not required
//                    }
//                }));

                // TODO: 28.12.2017 check closing of sockets
            }
        };
    }

    @Override
    public String toString(){
        return "0.0.0.0";
    }

}
