package com.votafore.warlords.v2.test;

import android.util.Log;

import java.io.BufferedReader;
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

    PrintWriter output;
    BufferedReader input;

    java.net.Socket mSocket;

    public Socket(InetAddress ip, int port) throws IOException{
        mSocket = new java.net.Socket(ip, port);

        output = new PrintWriter(mSocket.getOutputStream());
        input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
    }

    public Socket(java.net.Socket s) throws IOException{

        mSocket = s;

        output = new PrintWriter(mSocket.getOutputStream());
        input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
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
