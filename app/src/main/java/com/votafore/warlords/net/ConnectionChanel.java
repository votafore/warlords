package com.votafore.warlords.net;



import android.os.Handler;
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


    public ConnectionChanel(){

        mObservers      = new ArrayList<>();
        mConnections    = new ArrayList<>();

        mHandler        = new Handler(Looper.getMainLooper());
    }


    /*****************************************************************************/
    /********************************* IChanel ***********************************/
    /*****************************************************************************/

    /**
     * у канала есть "подписчики" для этого они должны поддерживать определенный
     * интерфейс
     */
    public interface IObserver{
        void notifyObserver(String message);
    }

    private List<IObserver> mObservers;





    @Override
    public void sendCommand(String command){

        Log.v(GameManager.TAG, "ConnectionChanel: sendCommand(). Отправка команды");

        for (IConnection connection : mConnections) {
            Log.v(GameManager.TAG, "ConnectionChanel: sendCommand(). Отправка команды - отправлена сокету");
            connection.sendCommand(command);
        }
    }

    @Override
    public void registerObserver(IObserver observer){
        Log.v(GameManager.TAG, "ConnectionChanel: registerObserver()");
        mObservers.add(observer);
    }

    @Override
    public void unregisterObserver(IObserver observer){
        Log.v(GameManager.TAG, "ConnectionChanel: unregisterObserver()");
        mObservers.remove(observer);
    }







    /*****************************************************************************/
    /****************************** ISocketListener ******************************/
    /*****************************************************************************/

    List<IConnection> mConnections;

    @Override
    public void onIncommingCommandReceived(IConnection connection, String message) {

        Log.v(GameManager.TAG, "ConnectionChanel: onIncommingCommandReceived() есть сообщение");

        for (IObserver observer : mObservers) {
            Log.v(GameManager.TAG, "ConnectionChanel: onIncommingCommandReceived() есть сообщение. отправляем сообщение подписчику");
            observer.notifyObserver(message);
        }
    }

    @Override
    public void onSocketConnected(IConnection connection) {
        Log.v(GameManager.TAG, "ConnectionChanel: onSocketConnected()");
        mConnections.add(connection);
    }

    @Override
    public void onSocketDisconnected(IConnection connection) {
        Log.v(GameManager.TAG, "ConnectionChanel: onSocketDisconnected()");
        mConnections.remove(connection);
    }



    /*****************************************************************************/
    /******************* функции для управления каналом (системой) ***************/
    /*****************************************************************************/

    /**
     * закрывает и отключает существующие подключения
     */
    public void close(){

        Log.v(GameManager.TAG, "ConnectionChanel: close()");

        while(mConnections.size() > 0){

            try {
                mConnections.get(0).close();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            Log.v(GameManager.TAG, "ConnectionChanel: close(). закрытие сокетов - 1 закрыт");

            // TODO: найдена проблема
            // из-за того что удаление из списка происходит в другой процедуре (обратный вызов)
            // происходит несколько попыток закрыть один и тот же сокет
        }

        if(mConnectionAppend != null){
            Log.v(GameManager.TAG, "ConnectionChanel: close(). закрываем append-ер");
            mConnectionAppend.stop();
        }
    }

    public void setupAppend(int type){

        Log.v(GameManager.TAG, "ConnectionChanel: setupAppend()");

        switch (type){
            case TYPE_FOR_CLIENT:

                Log.v(GameManager.TAG, "ConnectionChanel: setupAppend(). клиент");

                mConnectionAppend = new Append() {
                    @Override
                    public void addConnection() {

                    }

                    @Override
                    public void addConnection(final InetAddress serverIP, final int mServerPort) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    Socket socket      = new Socket(serverIP, mServerPort);
                                    SocketConnection mConnection = new SocketConnection(socket, mHandler, ConnectionChanel.this);

                                } catch (IOException e) {
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

                Log.v(GameManager.TAG, "ConnectionChanel: setupAppend(). сервер");

                mConnectionAppend = new Append() {

                    private Thread          mWorkThread;
                    private ServerSocket    mServerSocket;

                    @Override
                    public void addConnection() {

                        mWorkThread = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Log.v(GameManager.TAG, "ConnectionChanel: addConnection(). Поток сокета - запущен");

                                try {
                                    mServerSocket = new ServerSocket(0);

                                    mHost = mServerSocket.getInetAddress();
                                    mPort = mServerSocket.getLocalPort();

                                    Log.v(GameManager.TAG, "ConnectionChanel: addConnection(). Поток сокета - есть сервер");

                                } catch (IOException e) {
                                    Log.v(GameManager.TAG, "ConnectionChanel: addConnection(). Поток сокета - создание сервера. Ошибка: " + e.getMessage());
                                    e.printStackTrace();
                                    return;
                                }

                                while(!Thread.currentThread().isInterrupted()){

                                    try {

                                        Socket socket = mServerSocket.accept();

                                        Log.v(GameManager.TAG, "ConnectionChanel: addConnection(). Поток сокета - есть входящее подключение, настраиваем его");

                                        SocketConnection connection = new SocketConnection(socket, mHandler, ConnectionChanel.this);

                                    } catch (IOException e) {
                                        Log.v(GameManager.TAG, "ConnectionChanel: addConnection(). Поток сокета - входящие подключения. Ошибка: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        mWorkThread.start();
                    }

                    @Override
                    public void addConnection(InetAddress serverIP, int mServerPort) {

                    }

                    @Override
                    public void stop() {

                        Log.v(GameManager.TAG, "ConnectionChanel: stop()");

                        if(this.mServerSocket != null){
                            try {
                                this.mServerSocket.close();

                                Log.v(GameManager.TAG, "ConnectionChanel: stop(). Остановка сервера сокетов - остановлен");

                            } catch (IOException e) {
                                Log.v(GameManager.TAG, "ConnectionChanel: stop(). Остановка сервера сокетов - ошибка: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        if(this.mWorkThread != null){
                            this.mWorkThread.interrupt();
                            Log.v(GameManager.TAG, "ConnectionChanel: stop(). Остановка потока сервера сокетов - остановлен");
                        }
                    }
                };
        }

    }

    public void clearObservers(){
        Log.v(GameManager.TAG, "ConnectionChanel: clearObservers()");
        mObservers = new ArrayList<>();
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
        public abstract void addConnection(final InetAddress serverIP, final int mServerPort);
        public abstract void stop();
    }



    /***********************  только для сервера ***********************/
    private InetAddress mHost;
    private int         mPort;

    public InetAddress getHost(){
        return mHost;
    }

    public int getPort(){
        return mPort;
    }
}