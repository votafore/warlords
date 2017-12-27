package com.votafore.warlords.v3;

import android.util.Log;

import com.votafore.warlords.v2.Constants;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

/**
 * @author Votafore
 * Created on 26.12.2017.
 *
 * implementation for ISocket
 */

public class Socket implements ISocket {

    String TAG = Constants.TAG;
    String prefix= Constants.PFX_SOCKET;

    String format1 = Constants.format1;
    String format2 = Constants.format2;
    String format3 = Constants.format3;
    String format4 = Constants.format4;

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

        Log.v(TAG, String.format(format1, prefix, "Socket"));

        try {
            input = new DataInputStream(mSocket.getInputStream());
            output = new DataOutputStream(mSocket.getOutputStream());
            Log.v(TAG, String.format(format2, prefix, "Socket", "input and output created"));
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

        Log.v(TAG, String.format(format1, prefix, "send"));

        try {
            output.writeUTF(data.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListener(final IDataReceiver<String> listener) {

        Log.v(TAG, String.format(format1, prefix, "setListener"));

        // TODO: 26.12.2017 may be one thread should be for all sockets

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(!mSocket.isClosed()){

                    String data = "";

                    try {
                        data = null;
                        data = input.readUTF();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }

                    Log.v(TAG, String.format(format2, prefix, "SOCKET", "new data received"));

                    listener.onDataReceived(data);
                }

            }
        }).start();
    }

    @Override
    public void close() {

        Log.v(TAG, String.format(format1, prefix, "close"));

        try {

            if(input != null)
                input.close();

            if(output != null)
                output.close();

            if(mSocket != null)
                mSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /****************** STATIC *********************/

    public static Socket create(InetAddress ip, int port){
        return new Socket(ip, port);
    }

    public static Socket create(java.net.Socket socket){
        return new Socket(socket);
    }
}
