package com.votafore.warlords.test;

import android.os.Handler;
import android.util.Log;

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


    /**
     * произвольный слушатель сокета
     */
    ISocketListener mListener;

    public void  setListener(ISocketListener listener){
        mListener = listener;
    }




    /*****************************************************************************/
    /***************** пока не интерфейс, но возможно им будет *******************/
    /*****************************************************************************/

    public void sendMessage(String msg){
        for (SocketConnection connection : mConnections) {
            Log.v("GAMESERVICE","отослали запрос");
            connection.sendMessage(msg);
        }
    }

    public void addConnection(final InetAddress adress, final int port){

        Log.v("GAMESERVICE","добавляем подключение");

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Socket socket = new Socket(adress, port);
                    Log.v("GAMESERVICE","сокет есть");
                    SocketConnection con = new SocketConnection(socket, mHandler, TestConnectionManager.this);
                } catch (IOException e) {
                    Log.v("GAMESERVICE","добавляем подключение - ошибка");
                    Log.v("GAMESERVICE",e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void closeConnection(SocketConnection connection){
        connection.close();
    }

    public void closeAll(){

        while(mConnections.size() > 0){

            mConnections.get(0).close();
            mConnections.remove(0);
        }
    }

    /*****************************************************************************/
    /****************************** ISocketListener ******************************/
    /*****************************************************************************/

    @Override
    public void onObtainMessage(SocketConnection connection, String msg) {

        if(mListener != null)
            mListener.onObtainMessage(connection, msg);
    }

    @Override
    public void onSocketConnected(SocketConnection connection) {

        Log.v("GAMESERVICE","подключение добавлено");
        mConnections.add(connection);

        if(mListener != null)
            mListener.onSocketConnected(connection);
    }

    @Override
    public void onSocketDisconnected(SocketConnection connection) {
        mConnections.remove(connection);

        if(mListener != null)
            mListener.onSocketDisconnected(connection);
    }
}
