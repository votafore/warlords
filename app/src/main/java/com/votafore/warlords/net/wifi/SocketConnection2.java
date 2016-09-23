package com.votafore.warlords.net.wifi;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * @author Votafore
 * Created on 22.09.2016.
 */

public class SocketConnection2 {

    public interface IConnectionListener {
        //void onSocketConnected(SocketConnection2 connection);
        void onSocketDisconnected(SocketConnection2 connection);
        void onReceiveMessage(SocketConnection2 connection, String msg);
    }

    /**
     * хранит ссылку на сокет клиента
     */
    private Socket mSocket;

    /**
     * обработчик основного потока сервера (пока не понятно нужен он или нет)
     */
    //private IClient mClient;

    /**
     * текущий "рабочий" поток
     */
    private Thread  mThread;

    /**
     * поток входящих сообщений
     */
    private BufferedReader mInput;

    /**
     * для того, что бы SocketConnection мог удалить свой экземпляр
     * из списка при закрытии сокета
     * будем хранить ссылку на список
     */
    private List<SocketConnection2> mSocketConnectionList;

    private IConnectionListener mListener;

    public SocketConnection2(final Socket socket, IConnectionListener listener){

        Log.v("MSOCKET", "SocketConnection: создан");

        mSocket   = socket;
        mListener = listener;

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
                            mListener.onSocketDisconnected(SocketConnection2.this);

                            Log.v("MSOCKET", "SocketConnection: поток вх. сообщений - сокет закрыт");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        continue;
                    }

                    // при получении сообщения отправляем другое сообщение
                    // в основной поток сервера
                    mListener.onReceiveMessage(SocketConnection2.this, msg);
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
