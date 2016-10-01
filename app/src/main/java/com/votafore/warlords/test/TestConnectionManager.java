package com.votafore.warlords.test;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.votafore.warlords.GameManager;
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
        mHandler        = new Handler(Looper.getMainLooper());

        Log.v(GameManager.TAG, "TestConnectionManager:");
    }


    /**
     * произвольный слушатель сокета
     */
    ISocketListener mListener;

    public void  setListener(ISocketListener listener){
        mListener = listener;
        Log.v(GameManager.TAG, "TestConnectionManager: setListener() установлен кастомных слушатель");
    }




    /*****************************************************************************/
    /***************** пока не интерфейс, но возможно им будет *******************/
    /*****************************************************************************/


    public void sendMessage(String msg){

        Log.v(GameManager.TAG, "TestConnectionManager: sendMessage(). Отправка сообщения");

        for (SocketConnection connection : mConnections) {
            Log.v(GameManager.TAG, "TestConnectionManager: sendMessage(). Отправка сообщения - 1 отправляется");
            connection.sendMessage(msg);
        }
    }

    public void addConnection(final InetAddress adress, final int port){

        Log.v(GameManager.TAG, "TestConnectionManager: addConnection(). Добавление подключения");

        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v(GameManager.TAG, "TestConnectionManager: addConnection(). Добавление подключения. Поток создания - запущен");

                try {
                    Socket socket = new Socket(adress, port);

                    Log.v(GameManager.TAG, "TestConnectionManager: addConnection(). Добавление подключения. Поток создания - есть сокет");

                    SocketConnection con = new SocketConnection(socket, mHandler, TestConnectionManager.this);

                    Log.v(GameManager.TAG, "TestConnectionManager: addConnection(). Добавление подключения. Поток создания - есть SocketConnection");

                    onSocketConnected(con);

                    Log.v(GameManager.TAG, "TestConnectionManager: addConnection(). Добавление подключения. Поток создания - соединение добавлено");

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v(GameManager.TAG, "TestConnectionManager: addConnection(). Добавление подключения. Поток создания - ошибка: " + e.getMessage());
                }
            }
        }).start();
    }

    public void close(){

        Log.v(GameManager.TAG, "TestConnectionManager: close().");

        while(mConnections.size() > 0){

            mConnections.get(0).close();
            //mConnections.remove(0);

            Log.v(GameManager.TAG, "TestConnectionManager: close(). 1 соединение закрыто");
        }
    }

    /*****************************************************************************/
    /****************************** ISocketListener ******************************/
    /*****************************************************************************/

    @Override
    public void onObtainMessage(SocketConnection connection, String msg) {

        Log.v(GameManager.TAG, "TestConnectionManager: onObtainMessage().");

        if(mListener != null)
            mListener.onObtainMessage(connection, msg);
    }

    @Override
    public void onSocketConnected(SocketConnection connection) {

        Log.v(GameManager.TAG, "TestConnectionManager: onSocketConnected().");

        mConnections.add(connection);

        if(mListener != null)
            mListener.onSocketConnected(connection);
    }

    @Override
    public void onSocketDisconnected(SocketConnection connection) {

        Log.v(GameManager.TAG, "TestConnectionManager: onSocketDisconnected().");

        mConnections.remove(connection);

        if(mListener != null)
            mListener.onSocketDisconnected(connection);
    }
}
