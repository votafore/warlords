package com.votafore.warlords.v3;

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
        try {
            input = new DataInputStream(mSocket.getInputStream());
            output = new DataOutputStream(mSocket.getOutputStream());
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
        try {
            output.writeUTF(data.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListener(final IDataReceiver<String> listener) {

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

                    listener.onDataReceived(data);
                }

            }
        }).start();
    }

    @Override
    public void close() {

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
