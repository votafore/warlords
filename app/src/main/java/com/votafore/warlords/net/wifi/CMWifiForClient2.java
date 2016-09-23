package com.votafore.warlords.net.wifi;

import android.util.Log;

import com.votafore.warlords.net.IClient;
import com.votafore.warlords.net.IConnection;
import com.votafore.warlords.net.IServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Votafore
 * Created on 21.09.2016.
 *
 * Connection Manager WI-FI
 */

public class CMWifiForClient2 implements IConnection, IServer {

    public String   mServerIP;
    public int      mServerPort;

    private IClient mClient;


    private List<SocketConnection> mSocketConnectionList;

    public CMWifiForClient2(IClient client){

        mClient     = client;

        mSocketConnectionList = new ArrayList<>();

        isConnected = false;
    }


    public void connect(){

        if(isConnected)
            return;

        Log.v("MSOCKET", "init: установили параметры подключения");
        mServerIP       = "192.168.0.101";
        mServerPort     = 6000;

        init();
    }

    public void disconnect(){

        release();

        isConnected = false;

        mClient.onConnectionChange(isConnected);
    }

    /**
     * признак успешного запуска механизма
     */

    private volatile boolean isConnected;


    /**
     * метод ответственен за получение параметров подключения и их использование
     */
    @Override
    public void init() {

        // создаем сокет
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v("MSOCKET", "init: поток создания сокета запущен");

                try {
                    Socket socket = new Socket(InetAddress.getByName(mServerIP), mServerPort);
                    Log.v("MSOCKET", "init: сокет создан");

                    mSocketConnectionList.add(new SocketConnection(socket, mClient, mSocketConnectionList));

                    isConnected = true;

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v("MSOCKET", "init: сокет НЕ создан");
                    return;
                }

                mClient.onConnectionChange(isConnected);
            }
        }).start();


    }

    @Override
    public void release() {

        Log.v("MSOCKET", "release: остановка механизма");

        for (SocketConnection connection : mSocketConnectionList) {
            connection.close();
            mSocketConnectionList.remove(connection);
        }
    }

    @Override
    public void sendMessage(){


    }

    @Override
    public void addUnit() {

    }
}
