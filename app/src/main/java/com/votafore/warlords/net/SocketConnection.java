package com.votafore.warlords.net;

import android.os.Handler;
import android.util.Log;

import com.votafore.warlords.GameManager;
import com.votafore.warlords.support.Stack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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


    /**
     * стек команд
     */
    private Stack mStack;


    private ISocketListener mListener;

    private Handler mHandler;

    public SocketConnection(final Socket socket, Handler handler, ISocketListener listener) throws IOException{

        //Log.v(GameManager.TAG + "_1", "SocketConnection: конструктор");

        mSocket   = socket;
        mListener = listener;
        mHandler  = handler;
        mStack    = new Stack(50);

        mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        //Log.v(GameManager.TAG, "SocketConnection: получили входящий поток сокета");

        // при создании соединения стартует поток
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {

                //Log.v(GameManager.TAG, "SocketConnection: Поток входящих сообщений - запущен");

                while(!Thread.currentThread().isInterrupted()){

                    String msg = null;

                    try {
                        msg = mInput.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Log.v(GameManager.TAG, "SocketConnection: Поток входящих сообщений - есть сообщение (" + msg + ")");

                    if(msg == null){

                        //Log.v(GameManager.TAG, "SocketConnection: Поток входящих сообщений - останавливаем, закрываем сокет");

                        close();
                        continue;
                    }

                    // при получении сообщения отправляем другое сообщение
                    // в основной поток сервера

                    //final String message = msg;

//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mListener.onIncommingCommandReceived(SocketConnection.this, message);
//                        }
//                    });

                    mListener.onCommandReceived(SocketConnection.this, msg);
                }
            }
        });

        mThread.start();

        mListener.onSocketConnected(this);
    }

    @Override
    synchronized public void close(){

        //Log.v(GameManager.TAG + "_1", "SocketConnection: close()");

        if(!mThread.isInterrupted()){
            mThread.interrupt();
            //Log.v(GameManager.TAG, "SocketConnection: close(). поток входящих сообщений остановлен");
        }

        if(mSocket != null){

            //Log.v(GameManager.TAG, "SocketConnection: close(). Закрытие сокета");

            try {
                mSocket.close();

                //Log.v(GameManager.TAG, "SocketConnection: close(). Закрытие сокета - закрыт");

            } catch (IOException e) {
                //Log.v(GameManager.TAG, "SocketConnection: close(). Закрытие сокета - ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }

        mListener.onSocketDisconnected(SocketConnection.this);
    }

    @Override
    public void send(){

        if(mSocket == null)
            return;

        if(!mStack.hasNext())
            return;

        //Log.v(GameManager.TAG + "_1", "SocketConnection: send(). отправка команды - размер стека: " + String.valueOf(mStack.size()));

        try {
            PrintWriter out = new PrintWriter(mSocket.getOutputStream(),true);
            out.println(mStack.get());

            //Log.v(GameManager.TAG, "SocketConnection: send(). отправка команды - отправлена");

        } catch (IOException e) {
            //Log.v(GameManager.TAG, "SocketConnection: send(). отправка команды - ошибка: " + e.getMessage());
            e.printStackTrace();
        }

        //Log.v(GameManager.TAG, "SocketConnection: send(). отправка команды - размер стека после отправки: " + String.valueOf(mStack.size()));
    }

    @Override
    public void put(String command){
        mStack.put(command);
        //Log.v(GameManager.TAG + "_1", "SocketConnection: put(). Команда в стеке. Размер стека: " + String.valueOf(mStack.size()));
    }





    public String getHost() {
        return mSocket.getInetAddress().toString();
    }

    public int getPort() {
        return mSocket.getPort();
    }
}
