package com.votafore.warlords.test2;

import android.util.Log;

import com.votafore.warlords.GameManager;
import com.votafore.warlords.test.ConnectionManager2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;



public class AdderServerSocket extends SocketConnectionAdder {

    public AdderServerSocket(ConnectionManager2 manager) {
        super(manager);
        Log.v(GameManager.TAG, "AdderServerSocket: конструктор");
    }

    private Thread mWorkThread;
    private ServerSocket mServerSocket;

    @Override
    public void addConnection() {

        Log.v(GameManager.TAG, "AdderServerSocket: addConnection()");

        mWorkThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v(GameManager.TAG, "AdderServerSocket: addConnection(). Поток сокета - запущен");

                try {
                    mServerSocket = new ServerSocket(0);

                    Log.v(GameManager.TAG, "AdderServerSocket: addConnection(). Поток сокета - есть сервер");

                } catch (IOException e) {
                    Log.v(GameManager.TAG, "AdderServerSocket: addConnection(). Поток сокета - создание сервера. Ошибка: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }

                while(!Thread.currentThread().isInterrupted()){

                    try {

                        Socket socket = mServerSocket.accept();

                        Log.v(GameManager.TAG, "AdderServerSocket: addConnection(). Поток сокета - есть входящее подключение, настраиваем его");

                        final SocketConnection2 connection = new SocketConnection2(socket, mHandler, mManager);

                        mManager.store(connection);


                    } catch (IOException e) {
                        Log.v(GameManager.TAG, "AdderServerSocket: addConnection(). Поток сокета - входящие подключения. Ошибка: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

        mWorkThread.start();
    }

    @Override
    public void addConnection(final InetAddress serverIP, int mServerPort) {

    }

    @Override
    public void stop() {

        Log.v(GameManager.TAG, "AdderServerSocket: stop()");

        if(mServerSocket != null){
            try {
                mServerSocket.close();

                Log.v(GameManager.TAG, "AdderServerSocket: stop(). Остановка сервера сокетов - остановлен");

            } catch (IOException e) {
                Log.v(GameManager.TAG, "AdderServerSocket: stop(). Остановка сервера сокетов - ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if(mWorkThread != null){
            mWorkThread.interrupt();
            Log.v(GameManager.TAG, "AdderServerSocket: stop(). Остановка потока сервера сокетов - остановлен");
        }
    }



    public int getPort(){
        return mServerSocket.getLocalPort();
    }
}
