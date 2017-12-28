package com.votafore.warlords.v3;

import android.content.Context;
import android.util.Log;

import com.votafore.warlords.v2.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.observable.ObservableCreate;
import io.reactivex.processors.PublishProcessor;

/**
 * @author Vorafore
 * Created on 26.12.2017.
 *
 * implementation for IServer (for remote server)
 */

public class ServerRemote implements IServer {

//    String TAG = Constants.TAG;
//    String prefix= Constants.PFX_REMOTE_SERVER;
//
//    String format1 = Constants.format1;
//    String format2 = Constants.format2;
//    String format3 = Constants.format3;
//    String format4 = Constants.format4;


    /********************* IServer ********************/

    /**
     * object that send information
     */
    private PublishProcessor<JSONObject> sender;

    /**
     *
     */
    private Disposable dsp_receiver;

    private PublishProcessor<JSONObject> mReceiver;

    @Override
    public Disposable setReceiver(Consumer<JSONObject> receiver) {
        //Log.v(TAG, String.format(format1, prefix, "client's receiver set"));
        return mReceiver.subscribe(receiver); // TODO: 28.12.2017
    }

    @Override
    public void send(JSONObject data) {
        //Log.v(TAG, String.format(format1, prefix, "send"));
        sender.onNext(data);
    }


    @Override
    public void start(Context context) {

        //Log.v(TAG, String.format(format1, prefix, "start"));

        final ISocket socket = Socket.create(mIP, mPort);
        //Log.v(TAG, String.format(format2, prefix, "start", "socket created"));

        sender.subscribe(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject object) throws Exception {
                //Log.v(TAG, String.format(format2, prefix, "SOCKET", "send data"));
                socket.send(object);
            }
        });

        //Log.v(TAG, String.format(format2, prefix, "start", "socket set as subscriber for sender"));

        socket.setListener(new IDataReceiver<String>() {
            @Override
            public void onDataReceived(String data) {

                if(data == null) {
                    mReceiver.onComplete();
                }else{

                    try {
                        mReceiver.onNext(new JSONObject(data));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//        dsp_receiver = Observable.create(new ObservableOnSubscribe<JSONObject>() {
//            @Override
//            public void subscribe(final ObservableEmitter<JSONObject> e) throws Exception {
//
//                Log.v(TAG, String.format(format4, prefix, "OBSERVER", "SOCKET", "incoming data", "set listener for incoming data"));
//
//                socket.setListener(new IDataReceiver<String>() {
//                    @Override
//                    public void onDataReceived(String data) {
//
//                        if(data == null) {
//                            Log.v(TAG, String.format(format4, prefix, "OBSERVER", "SOCKET", "incoming data", "null, close socket"));
//                            e.onComplete();
//                            // TODO: 21.12.2017 close socket
//                        }else{
//                            Log.v(TAG, String.format(format4, prefix, "OBSERVER", "SOCKET", "incoming data", "data received"));
//                            try {
//                                e.onNext(new JSONObject(data));
//                            } catch (JSONException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    }
//                });
//            }
//        }).subscribe(mReceiver, new Consumer<Throwable>() {
//            @Override
//            public void accept(Throwable throwable) throws Exception {
//
//            }
//        }, new Action() {
//            @Override
//            public void run() throws Exception {
//                // TODO: 21.12.2017 check if current disposable is disposed
//                //dsp_receiver.dispose();
//            }
//        });
    }

    @Override
    public void stop() {

        //Log.v(TAG, String.format(format1, prefix, "stop"));

        sender.onComplete();
        //Log.v(TAG, String.format(format2, prefix, "stop", "sender.onComplete"));

        mReceiver.onComplete();

        // TODO: 26.12.2017 проверить надо ли диспосить dsp_receiver
    }






    /****************** ServerRemote ******************/

    private InetAddress mIP;
    private int mPort;

    public ServerRemote(InetAddress ip, int port){

        //Log.v(TAG, String.format(format1, prefix, "ServerRemote"));

        sender = PublishProcessor.create();
        mReceiver = PublishProcessor.create();
        // TODO: 26.12.2017 specify thread for broadcaster

        //Log.v(TAG, String.format(format2, prefix, "ServerRemote", "sender created"));

        mIP = ip;
        mPort = port;
    }




    /***************** misc ********************/

    @Override
    public String toString(){
        return mIP.toString();
    }
}
