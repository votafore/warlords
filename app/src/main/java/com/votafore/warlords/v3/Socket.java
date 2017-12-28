package com.votafore.warlords.v3;

//import android.util.Log;

import com.votafore.warlords.v2.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import io.reactivex.processors.PublishProcessor;

/**
 * @author Votafore
 * Created on 26.12.2017.
 *
 * implementation for ISocket
 */

public class Socket implements ISocket {

//    String TAG = Constants.TAG;
//    String prefix= Constants.PFX_SOCKET;
//
//    String format1 = Constants.format1;
//    String format2 = Constants.format2;
//    String format3 = Constants.format3;
//    String format4 = Constants.format4;

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

        android.util.Log.d(Constants.SOCKET_CRT, String.format(Constants.format1, Constants.LVL_SOCKET, "init"));

        try {
            input = new DataInputStream(mSocket.getInputStream());
            output = new DataOutputStream(mSocket.getOutputStream());
            android.util.Log.d(Constants.SOCKET_CRT, String.format(Constants.format1, Constants.LVL_SOCKET, "input and output created"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(!mSocket.isConnected()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
    public void setReceiver(final PublishProcessor<JSONObject> receiver) {

        //Log.v(TAG, String.format(format1, prefix, "setListener"));

        // TODO: 26.12.2017 may be one thread should be for all sockets

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(mSocket.isConnected()){

                    String data = "";

                    try {
                        //data = null;
                        data = input.readUTF();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }

                    //Log.v(TAG, String.format(format2, prefix, "SOCKET", "new data received"));

                    //listener.onDataReceived(data);

                    if(data == null) {

                        receiver.onComplete();

                        try {
                            mSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;

                    }else{

                        try {
                            receiver.onNext(new JSONObject(data));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void close() {

        //Log.v(TAG, String.format(format1, prefix, "close"));
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

        android.util.Log.d(Constants.SOCKET_CRT, "new socket connected... create");
        Socket s = new Socket(ip, port);
        android.util.Log.d(Constants.SOCKET_CRT, "new socket connected... created");

        return s;

    }

    public static Socket create(java.net.Socket socket){

        android.util.Log.d(Constants.SOCKET_CRT, "new socket connected... create");
        Socket s = new Socket(socket);
        android.util.Log.d(Constants.SOCKET_CRT, "new socket connected... created");

        return s;
    }
}
