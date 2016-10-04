package com.votafore.warlords.net;

import android.os.Handler;
import android.util.Log;

import com.votafore.warlords.GameManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Votafore
 * Created on 22.09.2016.
 */

public class SocketConnection implements IConnection {

    /**
     * хранит ссылку на сокет клиента
     */
    private Socket mSocket;


    /**
     * текущий "рабочий" поток
     */
    private Thread  mThread;


    /**
     * поток входящих сообщений
     */
    private BufferedReader mInput;


    private ISocketListener mListener;

    private Handler mHandler;

    public SocketConnection(final Socket socket, Handler handler, ISocketListener listener) throws IOException{

        Log.v(GameManager.TAG, "SocketConnection: конструктор");

        mSocket   = socket;
        mListener = listener;
        mHandler  =  handler;


        mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        Log.v(GameManager.TAG, "SocketConnection: получили входящий поток сокета");

        // при создании соединения стартует поток
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v(GameManager.TAG, "SocketConnection: Поток входящих сообщений - запущен");

                while(!Thread.currentThread().isInterrupted()){

                    String msg = null;

                    try {
                        msg = mInput.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.v(GameManager.TAG, "SocketConnection: Поток входящих сообщений - есть сообщение (" + msg + ")");

                    if(msg == null){

                        Log.v(GameManager.TAG, "SocketConnection: Поток входящих сообщений - останавливаем, закрываем сокет");

                        Thread.currentThread().interrupt();
                        try {

                            mSocket.close();

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mListener.onSocketDisconnected(SocketConnection.this);
                                }
                            });

                        } catch (IOException e) {
                            Log.v(GameManager.TAG, "SocketConnection: Поток входящих сообщений - остановка. Ошибка: " + e.getMessage());
                            e.printStackTrace();
                        }

                        continue;
                    }

                    // при получении сообщения отправляем другое сообщение
                    // в основной поток сервера

                    final String message = msg;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onIncommingCommandReceived(SocketConnection.this, message);
                        }
                    });
                }
            }
        });

        mThread.start();

        mListener.onSocketConnected(this);
    }

    @Override
    public void close(){

        Log.v(GameManager.TAG, "SocketConnection: close()");

        if(mSocket != null){

            Log.v(GameManager.TAG, "SocketConnection: close(). Закрытие сокета");

            try {
                mSocket.close();

                Log.v(GameManager.TAG, "SocketConnection: close(). Закрытие сокета - закрыт");

            } catch (IOException e) {
                Log.v(GameManager.TAG, "SocketConnection: close(). Закрытие сокета - ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if(!mThread.isInterrupted()){
            mThread.interrupt();
            Log.v(GameManager.TAG, "SocketConnection: close(). поток входящих сообщений остановлен");
        }

        mListener.onSocketDisconnected(SocketConnection.this);
    }

    @Override
    public void sendCommand(String command){

        if(mSocket == null)
            return;

        try {
            PrintWriter out = new PrintWriter(mSocket.getOutputStream(),true);
            out.println(command);

            Log.v(GameManager.TAG, "SocketConnection: sendCommand(). отправка команды - отправлена");

        } catch (IOException e) {
            Log.v(GameManager.TAG, "SocketConnection: sendCommand(). отправка команды - ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }





    public String getHost() {
        return mSocket.getInetAddress().toString();
    }

    public int getPort() {
        return mSocket.getPort();
    }
}
