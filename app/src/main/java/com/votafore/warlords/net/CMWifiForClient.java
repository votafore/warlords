package com.votafore.warlords.net;

import android.util.Log;

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

                    mSocketConnectionList.add(new SocketConnection(socket, mClient));
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


    private class SocketConnection{

        /**
         * хранит ссылку на сокет клиента
         */
        private Socket mSocket;

        /**
         * обработчик основного потока сервера (пока не понятно нужен он или нет)
         */
        private IClient mClient;

        /**
         * текущий "рабочий" поток
         */
        private Thread          mThread;

        /**
         * поток входящих сообщений
         */
        private BufferedReader mInput;

        public SocketConnection(final Socket socket, IClient client){

            Log.v("MSOCKET", "SocketConnection: создан");

            mSocket   = socket;
            mClient   = client;

            // при создании соединения стартует поток
            Log.v("MSOCKET", "SocketConnection: поток вх. сообщений - создаем");
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Log.v("MSOCKET", "SocketConnection: поток вх. сообщений - запущен");

                    String msg;

                    while(!Thread.currentThread().isInterrupted()){

                        try {
                            msg = mInput.readLine();
                            Log.v("MSOCKET", "SocketConnection: поток вх. сообщений - получили сообщение");
                        } catch (IOException e) {
                            e.printStackTrace();
                            msg = null;
                        }

                        if(msg == null){
                            Thread.currentThread().interrupt();
                            Log.v("MSOCKET", "SocketConnection: поток вх. сообщений - остановлен");
                            try {

                                mSocket.close();
                                mSocketConnectionList.remove(SocketConnection.this);

                                Log.v("MSOCKET", "SocketConnection: поток вх. сообщений - сокет закрыт");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            continue;
                        }

                        // при получении сообщения отправляем другое сообщение
                        // в основной поток сервера

                        mClient.onMessageReceive(0);
                    }
                }
            });

            Log.v("MSOCKET", "SocketConnection: поток вх. сообщений - запускаем");
            mThread.start();

            try {
                mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                Log.v("MSOCKET", "SocketConnection: получили вх. поток сокета");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void close(){

            if(mSocket != null){

                try {
                    mSocket.close();
                    Log.v("MSOCKET", "close: закрытие сокета - закрыт");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v("MSOCKET", "close: закрытие сокета - ошибка");
                }
            }

            if(!mThread.isInterrupted()){
                mThread.interrupt();
                Log.v("MSOCKET", "SocketConnection: поток вх. сообщений - остановлен");
            }
        }

        public void sendMessage(String msg){

            if(mSocket == null)
                return;

            try {
                PrintWriter out = new PrintWriter(mSocket.getOutputStream(),true);
                out.println("hello from server");

                Log.v("MSOCKET", "sendMessage: сообщение отправлено");

            } catch (IOException e) {
                e.printStackTrace();
                Log.v("MSOCKET", "sendMessage: сообщение НЕ отправлено");
            }
        }
    }
}
