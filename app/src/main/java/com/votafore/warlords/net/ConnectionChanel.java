package com.votafore.warlords.net;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.votafore.warlords.GameManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionChanel implements IChanel, ISocketListener {

    private HandlerThread   mWorkThread;
    private Handler         mWorkHandler;

    public ConnectionChanel(int type){

        //Log.v(GameManager.TAG, "ConnectionChanel: конструктор");

        mObservers      = new ArrayList<>();
        mConnections    = new ArrayList<>();

        mHandler        = new Handler(Looper.getMainLooper());
        mWorkThread     = new HandlerThread("Connection chanel thread");

        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper());

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
    public interface IObserver{
        void notifyObserver(int connectionId, String message);
    }

    private List<IObserver> mObservers;



    @Override
    public void sendCommand(String command){

        //Log.v(GameManager.TAG, "ConnectionChanel: sendCommand(). Отправка команды");

        synchronized (mConnectionLock){
            for (IConnection connection : mConnections) {
                //Log.v(GameManager.TAG, "ConnectionChanel: sendCommand(). добавили команду в стек");
                connection.put(command);
            }
        }

        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {

                for (IConnection connection : mConnections) {
                    connection.send();
                }
            }
        });
    }

    @Override
    public void registerObserver(IObserver observer){
        //Log.v(GameManager.TAG, "ConnectionChanel: registerObserver()");
        mObservers.add(observer);
    }

    @Override
    public void unregisterObserver(IObserver observer){
        //Log.v(GameManager.TAG, "ConnectionChanel: unregisterObserver()");
        mObservers.remove(observer);
    }



    /***************************** служебные данные ******************************/

    public final Object mStackLock = new Object();
    public final Object mConnectionLock = new Object();


    /*****************************************************************************/
    /****************************** ISocketListener ******************************/
    /*****************************************************************************/

    private List<IConnection> mConnections;

    @Override
    public void onCommandReceived(IConnection connection, String message) {

        //Log.v(GameManager.TAG, "ConnectionChanel: onIncommingCommandReceived() есть сообщение");

        for (IObserver observer : mObservers) {
//            Log.v(GameManager.TAG, "ConnectionChanel: onIncommingCommandReceived() есть сообщение. отправляем сообщение подписчику");
//            Log.v(GameManager.TAG, "ConnectionChanel: onIncommingCommandReceived() есть сообщение. ИД подключения: " + String.valueOf(mConnections.indexOf(connection)));
            observer.notifyObserver(mConnections.indexOf(connection), message);
        }

        if(mCustomListener != null)
            mCustomListener.onCommandReceived(connection, message);
    }

    @Override
    public void onSocketConnected(IConnection connection) {
        //Log.v(GameManager.TAG, "ConnectionChanel: onSocketConnected()");

        synchronized (mConnectionLock){
            mConnections.add(connection);
        }

        if(mCustomListener != null)
            mCustomListener.onSocketConnected(connection);
    }

    @Override
    public void onSocketDisconnected(IConnection connection) {
        //Log.v(GameManager.TAG, "ConnectionChanel: onSocketDisconnected()");

        synchronized (mConnectionLock){
            mConnections.remove(connection);
        }

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

        //Log.v(GameManager.TAG, "ConnectionChanel: close()");

        mWorkThread.quitSafely();

        while(mConnections.size() > 0){

            try {
                mConnections.get(0).close();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                //Log.v(GameManager.TAG, "ConnectionChanel: close(). закрытие сокетов - Ошибка: " + e.getMessage());
            }

            //Log.v(GameManager.TAG, "ConnectionChanel: close(). закрытие сокетов - 1 закрыт");
        }

        if(mConnectionAppend != null){
            //Log.v(GameManager.TAG, "ConnectionChanel: close(). закрываем append-ер");
            mConnectionAppend.stop();
        }
    }

    public void clearObservers(){
        //Log.v(GameManager.TAG, "ConnectionChanel: clearObservers()");
        mObservers = new ArrayList<>();
    }

    public List<IConnection> getConnections(){
        return mConnections;
    }

    private void setupAppend(int type){

        //Log.v(GameManager.TAG, "ConnectionChanel: setupAppend()");

        switch (type){
            case TYPE_FOR_CLIENT:

                //Log.v(GameManager.TAG, "ConnectionChanel: setupAppend(). клиент");

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

                                //Log.v(GameManager.TAG, "ConnectionChanel: setupAppend(). поток создания сокета. запущен");

                                try {
                                    //Log.v(GameManager.TAG, "ConnectionChanel: setupAppend(). поток создания сокета. создаем сокет");
                                    Socket socket      = new Socket(InetAddress.getByName(ip), mServerPort);
                                    //Log.v(GameManager.TAG, "ConnectionChanel: setupAppend(). поток создания сокета. создаем подключение");
                                    new SocketConnection(socket, mHandler, ConnectionChanel.this);
                                    //Log.v(GameManager.TAG, "ConnectionChanel: setupAppend(). поток создания сокета. все создано");
                                } catch (IOException e) {
                                    //Log.v(GameManager.TAG, "ConnectionChanel: setupAppend(). поток создания сокета. ошибка - " + e.getMessage());
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

                //Log.v(GameManager.TAG, "ConnectionChanel: setupAppend(). сервер");

                mConnectionAppend = new Append() {

                    private Thread          mWorkThread;
                    private ServerSocket    mServerSocket;

                    @Override
                    public void addConnection() {

                        mWorkThread = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                //Log.v(GameManager.TAG, "ConnectionChanel: addConnection(). Поток сокета - запущен");

                                try {
                                    mServerSocket = new ServerSocket(0);

                                    mPort = mServerSocket.getLocalPort();
                                } catch (IOException e) {
                                    //Log.v(GameManager.TAG, "ConnectionChanel: addConnection(). Поток сокета - создание сервера. Ошибка: " + e.getMessage());
                                    e.printStackTrace();
                                    return;
                                }

                                while(!Thread.currentThread().isInterrupted()){

                                    try {

                                        Socket socket = mServerSocket.accept();

                                        //Log.v(GameManager.TAG, "ConnectionChanel: addConnection(). Поток сокета - есть входящее подключение, настраиваем его");
                                        new SocketConnection(socket, mHandler, ConnectionChanel.this);

                                    } catch (IOException e) {
                                        //Log.v(GameManager.TAG, "ConnectionChanel: addConnection(). Поток сокета - входящие подключения. Ошибка: " + e.getMessage());
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

                        //Log.v(GameManager.TAG, "ConnectionChanel: stop()");

                        if(this.mServerSocket != null){
                            try {
                                this.mServerSocket.close();

                                //Log.v(GameManager.TAG, "ConnectionChanel: stop(). Остановка сервера сокетов - остановлен");

                            } catch (IOException e) {
                                //Log.v(GameManager.TAG, "ConnectionChanel: stop(). Остановка сервера сокетов - ошибка: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        if(this.mWorkThread != null){
                            this.mWorkThread.interrupt();
                            //Log.v(GameManager.TAG, "ConnectionChanel: stop(). Остановка потока сервера сокетов - остановлен");
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
}