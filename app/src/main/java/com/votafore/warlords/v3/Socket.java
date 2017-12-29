package com.votafore.warlords.v3;

//import android.util.Log;

import com.votafore.warlords.v2.Constants;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Votafore
 * Created on 26.12.2017.
 *
 * implementation for ISocket
 */

public class Socket implements ISocket {

    /**
     * current connection
     */
    private java.net.Socket mSocket;

    /**
     * object that send data to net
     */
    private DataOutputStream output;

    /**
     * object that read data from net
     */
    private DataInputStream input;



    /**
     * emitter for incoming data
     */
    private ConnectableObservable<JSONObject> emitter;

    /**
     * disposable for emitter
     */
    private Disposable dsp_emitter;



    private Socket(InetAddress ip, int port){

        try {
            mSocket = new java.net.Socket(ip, port);
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Socket(java.net.Socket socket){

        mSocket = socket;
        init();
    }

    private void init(){

        Log.d1(Constants.TAG_SOCKET_CRT, Constants.LVL_SOCKET, "init");

        try {
            Log.d1(Constants.TAG_SOCKET_CRT, Constants.LVL_SOCKET, "create input and output");
            input = new DataInputStream(mSocket.getInputStream());
            output = new DataOutputStream(mSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d1(Constants.TAG_SOCKET_CRT, Constants.LVL_SOCKET, "create emitter for incoming data");

        emitter = Observable.create(new ObservableOnSubscribe<JSONObject>() {
            @Override
            public void subscribe(ObservableEmitter<JSONObject> e) throws Exception {

                Log.d(Constants.TAG_SOCKET, "thread for incoming data started");

                String data;

                while(mSocket.isConnected()){

                    try {
                        data = input.readUTF();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        data = null;
                    }

                    if(data == null) {
                        e.onComplete();

                        // TODO: 29.12.2017 test it for correct closing
                        //mSocket.close();
                        close();

                        break;

                    }else{
                        Log.d1(Constants.TAG_DATA_RECEIVE, Constants.LVL_SOCKET, "new data received");
                        e.onNext(new JSONObject(data));
                    }
                }
            }
        })
        .subscribeOn(Schedulers.newThread())
        .publish();

        Log.d1(Constants.TAG_SOCKET_CRT, Constants.LVL_SOCKET, "start emitting");
        dsp_emitter = emitter.connect();
    }




    /**************** ISocket *****************/

    @Override
    public void send(JSONObject data) {

        Log.d1(Constants.TAG_DATA_SEND, Constants.LVL_SOCKET, "send data");

        try {
            output.writeUTF(data.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d1(Constants.TAG_DATA_SEND, Constants.LVL_SOCKET, "data are not sent");
        }
    }

    @Override
    public Disposable setReceiver(Consumer<JSONObject> consumer) {
        Log.d(Constants.TAG_SOCKET, "add new receiver");
        return emitter.subscribe(consumer);
    }

    @Override
    public void close() {

        Log.d(Constants.TAG_SOCKET_CLOSE, "closing...");

        try {
            Log.d1(Constants.TAG_SOCKET_CLOSE, Constants.LVL_SOCKET, "close input");
            if(input != null)
                input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Log.d1(Constants.TAG_SOCKET_CLOSE, Constants.LVL_SOCKET, "close output");
            if(output != null)
                output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Log.d1(Constants.TAG_SOCKET_CLOSE, Constants.LVL_SOCKET, "close socket");
            if(mSocket != null)
                mSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d1(Constants.TAG_SOCKET_CLOSE, Constants.LVL_SOCKET, "stop emitting data");
        dsp_emitter.dispose();

        android.util.Log.d(Constants.TAG_SOCKET_CLOSE, "socket closed");
    }



    /****************** STATIC *********************/

    public static Socket create(InetAddress ip, int port){

        Log.d(Constants.TAG_SOCKET_CRT, "new socket connected... create");
        Socket s = new Socket(ip, port);
        Log.d(Constants.TAG_SOCKET_CRT, "new socket connected... created");

        return s;

    }

    public static Socket create(java.net.Socket socket){

        Log.d(Constants.TAG_SOCKET_CRT, "new socket connected... create");
        Socket s = new Socket(socket);
        Log.d(Constants.TAG_SOCKET_CRT, "new socket connected... created");

        return s;
    }
}
