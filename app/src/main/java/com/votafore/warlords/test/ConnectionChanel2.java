package com.votafore.warlords.test;



import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.votafore.warlords.GameManager;
import com.votafore.warlords.net.ConnectionChanel;
import com.votafore.warlords.net.IChanel;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ConnectionChanel2 implements IChanel, ISocketListener2 {

    private Thread mWorkThread;

    ConnectionChanel2(){

        mObservers      = new ArrayList<>();
        mConnections    = new ArrayList<>();

        mHandler        = new Handler(Looper.getMainLooper());

        mWorkThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v(GameManager.TAG, "ConnectionChanel2 - поток стека - запущен.");

                while (!Thread.currentThread().isInterrupted()) {

                    synchronized (mStackLock) {

                        // обход всех подключений
                        for (IConnection2 connection : mConnections) {

                            connection.send();
                        }
                    }
                }
            }
        });

        mWorkThread.start();
    }


    /*****************************************************************************/
    /********************************* IChanel ***********************************/
    /*****************************************************************************/

    public static final Object mStackLock = new Object();

    private List<ConnectionChanel.IObserver> mObservers;



    @Override
    public void sendCommand(String command){

        Log.v(GameManager.TAG, "ConnectionChanel2: sendCommand(). Отправка команды");

        synchronized (mStackLock){
            for (IConnection2 connection : mConnections) {
                Log.v(GameManager.TAG, "ConnectionChanel2: sendCommand(). добавили команду в стек");
                connection.put(command);
            }
        }
    }

    @Override
    public void registerObserver(ConnectionChanel.IObserver observer){
        Log.v(GameManager.TAG, "ConnectionChanel2: registerObserver()");
        mObservers.add(observer);
    }

    @Override
    public void unregisterObserver(ConnectionChanel.IObserver observer){
        Log.v(GameManager.TAG, "ConnectionChanel2: unregisterObserver()");
        mObservers.remove(observer);
    }




    /*****************************************************************************/
    /****************************** ISocketListener ******************************/
    /*****************************************************************************/

    private List<IConnection2> mConnections;

    @Override
    public void onIncommingCommandReceived(IConnection2 connection, String message) {

        Log.v(GameManager.TAG, "ConnectionChanel2: onIncommingCommandReceived() есть сообщение");

        for (ConnectionChanel.IObserver observer : mObservers) {
            Log.v(GameManager.TAG, "ConnectionChanel2: onIncommingCommandReceived() есть сообщение. отправляем сообщение подписчику");
            Log.v(GameManager.TAG, "ConnectionChanel2: onIncommingCommandReceived() есть сообщение. ИД подключения: " + String.valueOf(mConnections.indexOf(connection)));
            observer.notifyObserver(mConnections.indexOf(connection), message);
        }
    }

    @Override
    public void onSocketConnected(IConnection2 connection) {
        Log.v(GameManager.TAG, "ConnectionChanel2: onSocketConnected()");

        mConnections.add(connection);
    }

    @Override
    public void onSocketDisconnected(IConnection2 connection) {
        Log.v(GameManager.TAG, "ConnectionChanel2: onSocketDisconnected()");

        mConnections.remove(connection);
    }





    /*****************************************************************************/
    /******************* функции для управления каналом (системой) ***************/
    /*****************************************************************************/

    /**
     * закрывает и отключает существующие подключения
     */
    void close(){

        Log.v(GameManager.TAG, "ConnectionChanel2: close()");

        mWorkThread.interrupt();

        while(mConnections.size() > 0){

            try {
                mConnections.get(0).close();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                Log.v(GameManager.TAG, "ConnectionChanel2: close(). закрытие сокетов - Ошибка: " + e.getMessage());
            }

            Log.v(GameManager.TAG, "ConnectionChanel2: close(). закрытие сокетов - 1 закрыт");
        }

        if(mConnectionAppend != null){
            Log.v(GameManager.TAG, "ConnectionChanel2: close(). закрываем append-ер");
            mConnectionAppend.stop();
        }
    }

    void setupAppend(int type){

        Log.v(GameManager.TAG, "ConnectionChanel2: setupAppend()");

        switch (type){
            case TYPE_FOR_CLIENT:

                Log.v(GameManager.TAG, "ConnectionChanel2: setupAppend(). клиент");

                mConnectionAppend = new Append() {
                    @Override
                    public void addConnection() {

                    }

                    @Override
                    public void addConnection(final String serverIP, final int mServerPort) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                String ip = serverIP.replace("/","");

                                Log.v(GameManager.TAG, "ConnectionChanel2: setupAppend(). поток создания сокета. запущен");

                                try {
                                    Log.v(GameManager.TAG, "ConnectionChanel2: setupAppend(). поток создания сокета. создаем сокет");
                                    Socket socket      = new Socket(InetAddress.getByName(ip), mServerPort);
                                    Log.v(GameManager.TAG, "ConnectionChanel2: setupAppend(). поток создания сокета. создаем подключение");
                                    //SocketConnection2 mConnection =
                                    new SocketConnection2(socket, mHandler, ConnectionChanel2.this);
                                    Log.v(GameManager.TAG, "ConnectionChanel2: setupAppend(). поток создания сокета. все создано");
                                } catch (IOException e) {
                                    Log.v(GameManager.TAG, "ConnectionChanel2: setupAppend(). поток создания сокета. ошибка - " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }

                    @Override
                    public void stop() {

                    }
                };

                break;

            case TYPE_FOR_SERVER:

                Log.v(GameManager.TAG, "ConnectionChanel2: setupAppend(). сервер");

                mConnectionAppend = new Append() {

                    private Thread          mWorkThread;
                    private ServerSocket    mServerSocket;

                    @Override
                    public void addConnection() {

                        mWorkThread = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Log.v(GameManager.TAG, "ConnectionChanel2: addConnection(). Поток сокета - запущен");

                                try {
                                    mServerSocket = new ServerSocket(0);

                                    mPort = mServerSocket.getLocalPort();

                                } catch (IOException e) {
                                    Log.v(GameManager.TAG, "ConnectionChanel2: addConnection(). Поток сокета - создание сервера. Ошибка: " + e.getMessage());
                                    e.printStackTrace();
                                    return;
                                }

                                while(!Thread.currentThread().isInterrupted()){

                                    try {

                                        Socket socket = mServerSocket.accept();

                                        Log.v(GameManager.TAG, "ConnectionChanel2: addConnection(). Поток сокета - есть входящее подключение, настраиваем его");

                                        //SocketConnection2 connection =
                                        new SocketConnection2(socket, mHandler, ConnectionChanel2.this);

                                    } catch (IOException e) {
                                        Log.v(GameManager.TAG, "ConnectionChanel2: addConnection(). Поток сокета - входящие подключения. Ошибка: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        mWorkThread.start();
                    }

                    @Override
                    public void addConnection(String serverIP, int mServerPort) {

                    }

                    @Override
                    public void stop() {

                        Log.v(GameManager.TAG, "ConnectionChanel2: stop()");

                        if(this.mServerSocket != null){
                            try {
                                this.mServerSocket.close();

                                Log.v(GameManager.TAG, "ConnectionChanel2: stop(). Остановка сервера сокетов - остановлен");

                            } catch (IOException e) {
                                Log.v(GameManager.TAG, "ConnectionChanel2: stop(). Остановка сервера сокетов - ошибка: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        if(this.mWorkThread != null){
                            this.mWorkThread.interrupt();
                            Log.v(GameManager.TAG, "ConnectionChanel2: stop(). Остановка потока сервера сокетов - остановлен");
                        }
                    }
                };
        }

    }

    void clearObservers(){
        Log.v(GameManager.TAG, "ConnectionChanel2: clearObservers()");
        mObservers = new ArrayList<>();
    }

    List<IConnection2> getConnections(){
        return mConnections;
    }


    /*****************************************************************************/
    /*************** системный раздел, реализующий работу класса *****************/
    /*****************************************************************************/

    private Handler mHandler;

    private Append mConnectionAppend;

    Append getConnectionAppend(){
        return mConnectionAppend;
    }


    public static final int TYPE_FOR_CLIENT = 0;
    public static final int TYPE_FOR_SERVER = 1;



    abstract class Append {

        public Append(){

        }

        public abstract void addConnection();
        public abstract void addConnection(final String serverIP, final int mServerPort);
        public abstract void stop();
    }



    /***********************  только для сервера ***********************/
    private int         mPort;

    public int getPort(){
        return mPort;
    }
}