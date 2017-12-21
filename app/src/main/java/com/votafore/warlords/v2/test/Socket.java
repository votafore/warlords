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

public class Socket extends java.net.Socket {

    PrintWriter output;
    BufferedReader input;

    public Socket(InetAddress ip, int port) throws IOException{
        super(ip, port);

        output = new PrintWriter(getOutputStream());
        input = new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public Socket(java.net.Socket s) throws IOException{

        output = new PrintWriter(s.getOutputStream());
        input = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        Log.v("SOCKET", "closed");
    }
}
