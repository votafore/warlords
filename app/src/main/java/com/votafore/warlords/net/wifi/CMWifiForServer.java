package com.votafore.warlords.net.wifi;

import android.util.Log;

import com.votafore.warlords.net.IClient;
import com.votafore.warlords.net.IConnection;
import com.votafore.warlords.net.IServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Votafore
 * Created on 21.09.2016.
 *
 * Connection Manager WI-FI
 */

public class CMWifiForServer implements IConnection, SocketConnection2.IConnectionListener {

    private IClient mClient;

    private IServer mServer;

    private ServerSocket mServerSocket;

    private List<SocketConnection2> mSocketConnectionList;

    private Thread mWorkThread;

    private int mServerPort     = 6000;

    //private Handler mHandler;

    public CMWifiForServer(IServer server){

        mServer     = server;

        mSocketConnectionList = new ArrayList<>();

        //mHandler = new Handler();
    }

    /**
     * метод ответственен за получение параметров подключения и их использование
     */
    @Override
    public void init() {

        // создаем сокет
        mWorkThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v("MSOCKET", "init: поток сервера сокета - запущен");

                try {
                    mServerSocket = new ServerSocket(mServerPort);
                    Log.v("MSOCKET", "init: поток сервера сокета - сервер создан");

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v("MSOCKET", "init: поток сервера сокета - сервер НЕ создан");
                    return;
                }

                while(!Thread.currentThread().isInterrupted()){

                    try {

                        Socket socket = mServerSocket.accept();
                        Log.v("MSOCKET", "init: поток сервера сокета - кто-то подключился. Создаем канал");

                        SocketConnection2 connection = new SocketConnection2(socket, CMWifiForServer.this);
                        onSocketConnected(connection);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mWorkThread.start();


    }

    @Override
    public void release() {

        Log.v("MSOCKET", "release: остановка механизма:");

        for (SocketConnection2 connection : mSocketConnectionList) {

            Log.v("MSOCKET", "release: закрытие сокета");

            connection.close();
            mSocketConnectionList.remove(connection);
        }

        try {
            mServerSocket.close();
            Log.v("MSOCKET", "release: остановка сервера сокетов - сервер остановлен");
        } catch (IOException e) {
            e.printStackTrace();
        }

        mWorkThread.interrupt();
        Log.v("MSOCKET", "release: остановлен поток сервера");
    }

    @Override
    public void sendMessage(){

        Log.v("MSOCKET", "sendMessage: рассылка сообщений");

        for (SocketConnection2 connection : mSocketConnectionList) {
            connection.sendMessage("");
        }
    }




    ///////////////////////////
    // пробные решения
    ///////////////////////////

    private void onSocketConnected(SocketConnection2 connection){
        mSocketConnectionList.add(connection);
    }

    @Override
    public void onSocketDisconnected(SocketConnection2 connection) {
        mSocketConnectionList.remove(connection);
    }

    @Override
    public void onReceiveMessage(SocketConnection2 connection, String msg) {

        // анализируем полученное сообщение и вызываем соответствующий
        // метод объекта сервера:
        // - изменение параметров
        // - создание \ удаление объекта


        // пока тестируем (добавляем объект)
        mServer.addUnit();
    }
}
