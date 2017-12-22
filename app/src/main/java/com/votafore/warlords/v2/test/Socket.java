package com.votafore.warlords.v2.test;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;


/**
 * @author Votafore
 * Created on 20.12.2017.
 *
 * class represents a socket
 */

public class Socket {

    public DataOutputStream output;
    public DataInputStream input;

    public java.net.Socket mSocket;

    public Socket(InetAddress ip, int port) throws IOException{

        mSocket = new java.net.Socket(ip, port);

        init();
    }

    public Socket(java.net.Socket s) throws IOException{

        mSocket = s;

        init();
    }

    private void init() throws IOException{

        output = new DataOutputStream(mSocket.getOutputStream());
        input = new DataInputStream(mSocket.getInputStream());

        while(!mSocket.isConnected()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }






    public void close() throws IOException {
        mSocket.close();
        Log.v("TESTRX", "SOCKET.    closed");
    }

    @Override
    public String toString() {
        return mSocket.toString();
    }
}
