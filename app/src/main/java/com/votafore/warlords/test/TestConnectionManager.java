package com.votafore.warlords.test;

import android.os.Handler;

import com.votafore.warlords.net.ISocketListener;
import com.votafore.warlords.net.wifi.SocketConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Votafore
 * Created on 26.09.2016.
 */

public class TestConnectionManager implements ISocketListener{

    public List<SocketConnection> mConnections;

    Handler mHandler;

    public TestConnectionManager(){

        mConnections    = new ArrayList<>();
        mHandler        = new Handler();
    }




    /*****************************************************************************/
    /***************** пока не интерфейс, но возможно им будет *******************/
    /*****************************************************************************/

    public void sendMessage(String msg){
        for (SocketConnection connection : mConnections) {
            connection.sendMessage(msg);
        }
    }

    public void addConnection(final InetAddress adress, final int port){

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Socket socket = new Socket(adress, port);
                    SocketConnection con = new SocketConnection(socket, mHandler, TestConnectionManager.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void closeConnection(SocketConnection connection){
        connection.close();
    }


    /*****************************************************************************/
    /****************************** ISocketListener ******************************/
    /*****************************************************************************/

    @Override
    public void onObtainMessage(String msg) {

    }

    @Override
    public void onSocketConnected(SocketConnection connection) {
        mConnections.add(connection);
    }

    @Override
    public void onSocketDisconnected(SocketConnection connection) {
        mConnections.remove(connection);
    }
}
