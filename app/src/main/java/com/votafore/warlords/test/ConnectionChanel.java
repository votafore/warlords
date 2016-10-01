package com.votafore.warlords.test;



import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.votafore.warlords.GameManager;
import com.votafore.warlords.test2.IConnection;
import com.votafore.warlords.test2.ISocketListener2;
import com.votafore.warlords.test2.SocketConnection3;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionChanel implements IChanel, ISocketListener2{


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

        Log.v(GameManager.TAG, "ConnectionManager2: sendCommand(). Отправка команды");

        for (IConnection connection : mConnections) {
            Log.v(GameManager.TAG, "ConnectionManager2: sendCommand(). Отправка команды - отправлена сокету");
            connection.sendCommand(command);
        }
    }

    @Override
    public void registerObserver(IObserver observer){
        mObservers.add(observer);
    }

    @Override
    public void unregisterObserver(IObserver observer){
        mObservers.remove(observer);
    }







    /*****************************************************************************/
    /****************************** ISocketListener ******************************/
    /*****************************************************************************/

    List<IConnection> mConnections;

    @Override
    public void onIncommingCommandReceived(IConnection connection, String message) {

        for (IObserver observer : mObservers) {
            observer.notifyObserver(message);
        }
    }

    @Override
    public void onSocketConnected(IConnection connection) {
        mConnections.add(connection);
    }

    @Override
    public void onSocketDisconnected(IConnection connection) {
        mConnections.remove(connection);
    }



    /*****************************************************************************/
    /******************* функции для управления каналом (системой) ***************/
    /*****************************************************************************/

    public void close(){

        while(mConnections.size() > 0){

            try {
                mConnections.get(0).close();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            Log.v(GameManager.TAG, "ConnectionManager2: close(). закрытие сокетов - 1 закрыт");

            // TODO: найдена проблема
            // из-за того что удаление из списка происходит в другой процедуре (обратный вызов)
            // происходит несколько попыток закрыть один и тот же сокет
        }

        if(mConnectionAppend != null)
            mConnectionAppend.stop();
    }


    public void setupAppend(int type){

        switch (type){
            case TYPE_FOR_CLIENT:

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
                                    SocketConnection3 mConnection = new SocketConnection3(socket, mHandler, ConnectionChanel.this);

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

                mConnectionAppend = new Append() {

                    private Thread          mWorkThread;
                    private ServerSocket    mServerSocket;

                    @Override
                    public void addConnection() {

                        this.mWorkThread = new Thread(new Runnable() {
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

                                        SocketConnection3 connection = new SocketConnection3(socket, mHandler, ConnectionChanel.this);

                                    } catch (IOException e) {
                                        Log.v(GameManager.TAG, "AdderServerSocket: addConnection(). Поток сокета - входящие подключения. Ошибка: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        this.mWorkThread.start();
                    }

                    @Override
                    public void addConnection(InetAddress serverIP, int mServerPort) {

                    }

                    @Override
                    public void stop() {

                        Log.v(GameManager.TAG, "AdderServerSocket: stop()");

                        if(this.mServerSocket != null){
                            try {
                                this.mServerSocket.close();

                                Log.v(GameManager.TAG, "AdderServerSocket: stop(). Остановка сервера сокетов - остановлен");

                            } catch (IOException e) {
                                Log.v(GameManager.TAG, "AdderServerSocket: stop(). Остановка сервера сокетов - ошибка: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        if(this.mWorkThread != null){
                            this.mWorkThread.interrupt();
                            Log.v(GameManager.TAG, "AdderServerSocket: stop(). Остановка потока сервера сокетов - остановлен");
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



    private abstract class Append {

        public Append(){

        }

        public abstract void addConnection();
        public abstract void addConnection(final InetAddress serverIP, final int mServerPort);
        public abstract void stop();
    }
}