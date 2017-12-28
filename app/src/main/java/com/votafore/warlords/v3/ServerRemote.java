package com.votafore.warlords.v3;

import android.content.Context;

import org.json.JSONObject;

import java.net.InetAddress;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;

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

//    /**
//     *
//     */
//    private Disposable dsp_receiver;

    //private PublishProcessor<JSONObject> mReceiver;

    @Override
    public Disposable setReceiver(Consumer<JSONObject> receiver) {
        return mSocket.setReceiver(receiver);
    }

    @Override
    public void send(JSONObject data) {
        sender.onNext(data);
    }



    private ISocket mSocket;

    @Override
    public void start(Context context) {

        mSocket = Socket.create(mIP, mPort);

        sender.subscribe(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject object) throws Exception {
                mSocket.send(object);
            }
        });

        //socket.setReceiver(mReceiver);
    }

    @Override
    public void stop() {

        sender.onComplete();
        //mReceiver.onComplete();

        // TODO: 26.12.2017 проверить надо ли диспосить dsp_receiver
    }






    /****************** ServerRemote ******************/

    private InetAddress mIP;
    private int mPort;

    public ServerRemote(InetAddress ip, int port){

        sender = PublishProcessor.create();
        //mReceiver = PublishProcessor.create();
        // TODO: 26.12.2017 specify thread for broadcaster

        mIP = ip;
        mPort = port;
    }




    /***************** misc ********************/

    @Override
    public String toString(){
        return mIP.toString();
    }
}
