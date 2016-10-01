package com.votafore.warlords.test2;

import android.os.Handler;
import android.util.Log;

import com.votafore.warlords.GameManager;
import com.votafore.warlords.test.ConnectionManager2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;



public class AdderClientSocket extends SocketConnectionAdder {

    public AdderClientSocket(ConnectionManager2 manager) {
        super(manager);
        Log.v(GameManager.TAG, "AdderClientSocket: конструктор");
    }

    @Override
    public void addConnection() {

    }

    @Override
    public void addConnection(final InetAddress serverIP, final int mServerPort) {

        Log.v(GameManager.TAG, "AdderClientSocket: addConnection()");

        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v(GameManager.TAG, "AdderClientSocket: addConnection(). Поток сокета - запущен");

                Socket socket;

                try {
                    socket      = new Socket(serverIP, mServerPort);
                    final SocketConnection2 mConnection = new SocketConnection2(socket, mHandler, mManager);

                    Log.v(GameManager.TAG, "AdderClientSocket: addConnection(). Поток сокета - есть SocketConnection2");

                    mManager.store(mConnection);

                } catch (IOException e) {
                    Log.v(GameManager.TAG, "AdderClientSocket: addConnection(). Поток сокета - ошибка: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void stop() {

    }
}
