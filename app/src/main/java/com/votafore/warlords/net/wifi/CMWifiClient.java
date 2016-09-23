package com.votafore.warlords.net.wifi;

import android.util.Log;

import com.votafore.warlords.game.Instance;
import com.votafore.warlords.net.IClient2;
import com.votafore.warlords.net.IServer2;
import com.votafore.warlords.net.ISocketListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CMWifiClient implements IClient2,ISocketListener {

    private IServer2 mServer;
    private IClient2 mLocalClient;


    private String TAG = "MSOCKET_CMWifiClient";

    public CMWifiClient(Instance instance){

        Log.v(TAG, "создем объект CMWifiClient");

        mLocalClient  = instance;
        mServer       = instance;

        mSocketConnectionList = new ArrayList<>();

        Log.v(TAG, "********************** ЗАПУСК СЕРВЕРА ********************");
        startServer();

        Log.v(TAG, "********************** ЗАПУСК СЕРВЕРА (КОНЕЦ) ********************");
    }


    /*******************************************************************************************************/
    /************************************************** CLIENT *********************************************/

    @Override
    public void onMessageReceived(String msg) {

        ////////////////////
        // РАССЫЛКА СООБЩЕНИЯ
        ////////////////////

        // берем список подключений и рассылаем параметры
        for (SocketConnection3 connection : mSocketConnectionList) {

            Log.v(TAG, "IClient: отправляем сообщение через сокет");
            connection.sendMessage(msg);
        }


        Log.v(TAG, "IClient: отправляем сообщение локальному клиенту");
        // и не забываем о локальном клиенте
        mLocalClient.onMessageReceived(msg);
    }

    @Override
    public void release(){

        Log.v(TAG, "IClient: получили release - закрываем подключения к серверу, сворачиваем сервер");

        // свернуться если сервер вызвал этот метод

        while(mSocketConnectionList.size() > 0){

            Log.v(TAG, "IClient: закрытие сокета");
            mSocketConnectionList.get(0).close();

            Log.v(TAG, "IClient: удаление сокета из списка подключений");
            mSocketConnectionList.remove(0);
        }

        try {
            mServerSocket.close();
            Log.v(TAG, "IClient: закрыли сокет сервера");
        } catch (IOException e) {
            Log.v(TAG, "IClient: НЕ закрыли сокет сервера");
            e.printStackTrace();
        }

        mWorkThread.interrupt();
        Log.v(TAG, "IClient: поток сервера остановлен");

    }

    /*******************************************************************************************************/
    /**************************************** SOCKET LISTENER **********************************************/

    @Override
    public void onObtainMessage(String msg){

        Log.v(TAG, "ISocketListener: отправляем сообщение серверу");
        mServer.handleCommand(msg);
    }

    @Override
    public void onSocketConnected(SocketConnection3 connection){

        Log.v(TAG, "ISocketListener: получили новое подключение. добавляем в список");
        mSocketConnectionList.add(connection);
    }

    @Override
    public void onSocketDisconnected(SocketConnection3 connection){

        Log.v(TAG, "ISocketListener: подключение закрыто. удаляем из списка");
        mSocketConnectionList.remove(connection);
    }


    /*******************************************************************************************************/
    /**************************************** СЕРВЕРНАЯ ЧАСТЬ СОКЕТОВ **************************************/

    private volatile List<SocketConnection3>    mSocketConnectionList;
    private Thread                              mWorkThread;
    private ServerSocket                        mServerSocket;

    private int                                 mServerPort = 6000;

    private void startServer(){

        Log.v(TAG, "Часть сервера: создаем поток сервера");

        // запускаем ожидание входящих подключений
        mWorkThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    mServerSocket = new ServerSocket(mServerPort);
                    Log.v(TAG, "Поток сервера: создали сокет сервера");
                } catch (IOException e) {

                    Log.v(TAG, "Поток сервера: НЕ создали сокет сервера");
                    e.printStackTrace();
                    return;
                }

                while(!Thread.currentThread().isInterrupted()){

                    try {

                        Socket socket = mServerSocket.accept();
                        Log.v(TAG, "Поток сервера: есть входящее подключение");

                        Log.v(TAG, "Поток сервера: создаем SocketConnection3");
                        SocketConnection3 connection = new SocketConnection3(socket, CMWifiClient.this);

                        onSocketConnected(connection);

                    } catch (IOException e) {

                        Log.v(TAG, "Поток сервера: исключение");
                        e.printStackTrace();
                    }
                }
            }
        });

        Log.v(TAG, "Часть сервера: запускаем поток сервера");
        mWorkThread.start();
    }


    /*************************** обработка отключения и подключения клиентов *******************************/



}