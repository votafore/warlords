package com.votafore.warlords.v3;

//import android.util.Log;

import com.votafore.warlords.v2.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
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
    private PublishProcessor<JSONObject> emitter;




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

        Log.d(String.format(Constants.format1, Constants.LVL_SOCKET, "init"));

        try {
            input = new DataInputStream(mSocket.getInputStream());
            output = new DataOutputStream(mSocket.getOutputStream());
            Log.d(String.format(Constants.format1, Constants.LVL_SOCKET, "input and output created"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(String.format(Constants.format1, Constants.LVL_SOCKET, "create emitter for incoming data"));

        emitter = PublishProcessor.create();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String data;

                while(mSocket.isConnected()){

                    try {
                        data = input.readUTF();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        data = null;
                    }

                    if(data == null) {

                        emitter.onComplete();

                        try {
                            mSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;

                    }else{

                        try {
                            emitter.onNext(new JSONObject(data));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }




    /**************** ISocket *****************/

    @Override
    public void send(JSONObject data) {

        //Log.v(TAG, String.format(format1, prefix, "send"));

        try {
            output.writeUTF(data.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Disposable setReceiver(Consumer<JSONObject> consumer) {
        return emitter.subscribe(consumer);
    }

    @Override
    public void close() {

        android.util.Log.d(Constants.SOCKET_CLOSE, "closing...");

        try {

            android.util.Log.d(Constants.SOCKET_CLOSE, String.format(Constants.format1, Constants.LVL_SOCKET, "close input"));
            if(input != null)
                input.close();

            android.util.Log.d(Constants.SOCKET_CLOSE, String.format(Constants.format1, Constants.LVL_SOCKET, "close output"));
            if(output != null)
                output.close();

            android.util.Log.d(Constants.SOCKET_CLOSE, String.format(Constants.format1, Constants.LVL_SOCKET, "close socket"));
            if(mSocket != null)
                mSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /****************** STATIC *********************/

    public static Socket create(InetAddress ip, int port){

        Log.setTAG(Constants.SOCKET_CRT);

        Log.d("new socket connected... create");
        Socket s = new Socket(ip, port);
        Log.d("new socket connected... created");

        return s;

    }

    public static Socket create(java.net.Socket socket){

        android.util.Log.d(Constants.SOCKET_CRT, "new socket connected... create");
        Socket s = new Socket(socket);
        android.util.Log.d(Constants.SOCKET_CRT, "new socket connected... created");

        return s;
    }
}
