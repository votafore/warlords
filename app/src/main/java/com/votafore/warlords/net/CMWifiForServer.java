package com.votafore.warlords.net;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
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

public class CMWifiForServer implements IConnection {

    private IClient  mClient;

    private ServerSocket mServerSocket;

    private List<SocketConnection> mSocketConnectionList;

    private Thread mWorkThread;

    private int mServerPort     = 6000;

    public CMWifiForServer(IClient client){

        mClient     = client;

        mSocketConnectionList = new ArrayList<>();
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
                        mSocketConnectionList.add(new SocketConnection(socket, mClient));

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

        for (SocketConnection connection : mSocketConnectionList) {

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

        for (SocketConnection connection : mSocketConnectionList) {
            connection.sendMessage("");
        }
    }


    private class SocketConnection{

        /**
         * хранит ссылку на сокет клиента
         */
        private Socket  mSocket;

        /**
         * обработчик основного потока сервера (пока не понятно нужен он или нет)
         */
        private IClient mClient;

        /**
         * текущий "рабочий" поток
         */
        private Thread  mThread;

        /**
         * поток входящих сообщений
         */
        private BufferedReader mInput;

        public SocketConnection(final Socket socket, IClient client){

            Log.v("MSOCKET", "SocketConnection: канал создан");

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
