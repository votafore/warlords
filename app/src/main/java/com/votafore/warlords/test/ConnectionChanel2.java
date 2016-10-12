package com.votafore.warlords.test;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.votafore.warlords.GameManager;
import com.votafore.warlords.net.ConnectionChanel;
import com.votafore.warlords.net.IChanel;
import com.votafore.warlords.net.IConnection;
import com.votafore.warlords.net.ISocketListener;
import com.votafore.warlords.net.SocketConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionChanel2 implements IChanel, ISocketListener {

    private WorkerThread mWorkThread;
    private Runnable     mTaskSend;

    public ConnectionChanel2(int type){

        Log.v(GameManager.TAG + "_1", "ConnectionChanel2: конструктор");

        mObservers      = new ArrayList<>();
        mConnections    = new ArrayList<>();
        mHandler        = new Handler(Looper.getMainLooper());
        mWorkThread     = new WorkerThread("Connection chanel thread");

        mWorkThread.start();
        mWorkThread.prepareHandler();

        mTaskSend = new Runnable() {
            @Override
            public void run() {

                Log.v(GameManager.TAG, "ConnectionChanel2 - поток стека - выполнение задачи рассылки.");

                // обход всех подключений
                for (IConnection connection : mConnections) {
                    connection.send();
                }
            }
        };

        setupAppend(type);
    }


    // есть разные запросы:
    // есть запрос, а есть рассылка
    // TODO: придумать как это дело организовать



    /*****************************************************************************/
    /********************************* IChanel ***********************************/
    /*****************************************************************************/

    /**
     * у канала есть "подписчики" для этого они должны поддерживать определенный
     * интерфейс
     */

    private List<ConnectionChanel.IObserver> mObservers;



    @Override
    public void sendCommand(String command){

        Log.v(GameManager.TAG + "_1", "ConnectionChanel2: sendCommand(). Отправка команды");

        synchronized (mStackLock){
            for (IConnection connection : mConnections) {
                Log.v(GameManager.TAG, "ConnectionChanel2: sendCommand(). добавили команду в стек");
                connection.put(command);
            }
        }

        Log.v(GameManager.TAG, "ConnectionChanel2: sendCommand(). запуск задачи рассылки");
        mWorkThread.postTask();
    }

    @Override
    public void registerObserver(ConnectionChanel.IObserver observer){
        Log.v(GameManager.TAG + "_1", "ConnectionChanel2: registerObserver()");
        mObservers.add(observer);
    }

    @Override
    public void unregisterObserver(ConnectionChanel.IObserver observer){
        Log.v(GameManager.TAG + "_1", "ConnectionChanel2: unregisterObserver()");
        mObservers.remove(observer);
    }



    /***************************** служебные данные ******************************/

    public final Object mStackLock = new Object();


    /*****************************************************************************/
    /****************************** ISocketListener ******************************/
    /*****************************************************************************/

    private List<IConnection> mConnections;

    @Override
    public void onIncommingCommandReceived(IConnection connection, String message) {

        Log.v(GameManager.TAG + "_1", "ConnectionChanel2: onIncommingCommandReceived() есть сообщение");

        for (ConnectionChanel.IObserver observer : mObservers) {
            Log.v(GameManager.TAG, "ConnectionChanel2: onIncommingCommandReceived() есть сообщение. отправляем сообщение подписчику");
            Log.v(GameManager.TAG, "ConnectionChanel2: onIncommingCommandReceived() есть сообщение. ИД подключения: " + String.valueOf(mConnections.indexOf(connection)));
            observer.notifyObserver(mConnections.indexOf(connection), message);
        }

        if(mCustomListener != null)
            mCustomListener.onIncommingCommandReceived(connection, message);
    }

    @Override
    public void onSocketConnected(IConnection connection) {
        Log.v(GameManager.TAG + "_1", "ConnectionChanel2: onSocketConnected()");

        mConnections.add(connection);

        if(mCustomListener != null)
            mCustomListener.onSocketConnected(connection);
    }

    @Override
    public void onSocketDisconnected(IConnection connection) {
        Log.v(GameManager.TAG + "_1", "ConnectionChanel: onSocketDisconnected()");

        mConnections.remove(connection);

        if(mCustomListener != null)
            mCustomListener.onSocketDisconnected(connection);
    }





    /*****************************************************************************/
    /******************* функции для управления каналом (системой) ***************/
    /*****************************************************************************/

    /**
     * закрывает и отключает существующие подключения
     */
    public void close(){

        Log.v(GameManager.TAG + "_1", "ConnectionChanel2: close()");

        mWorkThread.quitSafely();

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

    public void clearObservers(){
        Log.v(GameManager.TAG + "_1", "ConnectionChanel2: clearObservers()");
        mObservers = new ArrayList<>();
    }

    public List<IConnection> getConnections(){
        return mConnections;
    }

    private void setupAppend(int type){

        Log.v(GameManager.TAG + "_1", "ConnectionChanel2: setupAppend()");

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
                                    new SocketConnection(socket, mHandler, ConnectionChanel2.this);
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
                                        new SocketConnection(socket, mHandler, ConnectionChanel2.this);

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

    /*****************************************************************************/
    /*************** системный раздел, реализующий работу класса *****************/
    /*****************************************************************************/

    private Handler mHandler;

    private Append mConnectionAppend;

    public Append getConnectionAppend(){
        return mConnectionAppend;
    }


    public static final int TYPE_FOR_CLIENT = 0;
    public static final int TYPE_FOR_SERVER = 1;



    public abstract class Append {

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



    /*****************************************************************************/
    /*********************** раздел еще в разработке *****************************/

    private ISocketListener mCustomListener;

    public void setCustomListener(ISocketListener socketListener){
        mCustomListener = socketListener;
    }



    public class WorkerThread extends HandlerThread{

        private ThreadHandler mWorkerHandler;

        public WorkerThread(String name) {
            super(name);
        }

        public void postTask(Runnable task){
            mWorkerHandler.post(task);
        }

        public void postTask(){

            mWorkerHandler.sendMessage(mWorkerHandler.obtainMessage());

        }

        public void prepareHandler(){
            mWorkerHandler = new ThreadHandler(getLooper());
        }
    }


    public class ThreadHandler extends Handler{

        public ThreadHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            Log.v(GameManager.TAG, "ThreadHandler: handleMessage(). обработка отправки команды");

            // обход всех подключений
            for (IConnection connection : mConnections) {
                connection.send();
            }

            super.handleMessage(msg);
        }
    }
}