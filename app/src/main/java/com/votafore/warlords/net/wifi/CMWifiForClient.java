package com.votafore.warlords.net.wifi;

import android.util.Log;

import com.votafore.warlords.net.IClient;
import com.votafore.warlords.net.IConnection;

import java.io.IOException;
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

public class CMWifiForClient implements IConnection {

    public String   mServerIP;
    public int      mServerPort;

    private IClient mClient;


    private List<SocketConnection> mSocketConnectionList;

    public CMWifiForClient(IClient client){

        isStarted   = false;
        mClient     = client;

        mSocketConnectionList = new ArrayList<>();

        Log.v("MSOCKET", "init: установили параметры подключения");

        mServerIP       = "192.168.0.101";
        mServerPort     = 6000;
    }


    /**
     * признак успешного запуска механизма
     */

    private volatile boolean isStarted;


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
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v("MSOCKET", "init: сокет НЕ создан");
                    return;
                }
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
}
