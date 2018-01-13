package com.votafore.warlords.v4.network;

import android.content.Context;

import com.votafore.warlords.v3.IServer;
import com.votafore.warlords.v3.ISocket;
import com.votafore.warlords.v3.Log;
import com.votafore.warlords.v3.Socket;

import org.json.JSONObject;

import java.net.InetAddress;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

import static com.votafore.warlords.v2.Constants.LVL_REMOTE_SERVER;
import static com.votafore.warlords.v2.Constants.TAG_DATA_SEND;
import static com.votafore.warlords.v2.Constants.TAG_SOCKET;
import static com.votafore.warlords.v2.Constants.TAG_SRV_CRT;
import static com.votafore.warlords.v2.Constants.TAG_SRV_START;
import static com.votafore.warlords.v2.Constants.TAG_SRV_STOP;

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
    public void start(Context context) {

        Log.d1(TAG_SRV_START, LVL_REMOTE_SERVER, "create socket");
        mSocket = Socket.create(mIP, mPort);

        Log.d1(TAG_SOCKET, LVL_REMOTE_SERVER, "set up socket in server");
        Log.d1(TAG_SOCKET, LVL_REMOTE_SERVER, "set socket as subscriber for sender");
        mSocket.subscribeSocket(sender);
    }

    @Override
    public void stop() {
        Log.d1(TAG_SRV_STOP, LVL_REMOTE_SERVER, "sender.onComplete()");
        sender.onComplete();
    }






    /****************** ServerRemote ******************/

    private InetAddress mIP;
    private int mPort;

    public ServerRemote(InetAddress ip, int port){

        Log.d1(TAG_SRV_CRT, LVL_REMOTE_SERVER, "create sender");

        sender = PublishProcessor.create();
        sender.subscribeOn(Schedulers.io());

        mIP = ip;
        mPort = port;
    }




    /***************** misc ********************/

    @Override
    public String toString(){
        return mIP.toString();
    }
}
