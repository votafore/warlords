package com.votafore.warlords.net.wifi;

import android.os.Handler;
import android.util.Log;

import com.votafore.warlords.net.ISocketListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.net.Socket;

/**
 * @author Votafore
 * Created on 22.09.2016.
 */

public class SocketConnection {

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

    private String TAG = "MSOCKET_Connection3";

    public SocketConnection(final Socket socket, Handler handler, ISocketListener listener) throws IOException{

        mSocket   = socket;
        mListener = listener;
        mHandler  =  handler;


        mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        Log.v(TAG, "получили входящий поток сокета");

        // при создании соединения стартует поток
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while(!Thread.currentThread().isInterrupted()){

                    String msg = null;

                    try {
                        msg = mInput.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(msg == null){
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
                            mListener.onObtainMessage(message);
                        }
                    });
                }
            }
        });

        mThread.start();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onSocketConnected(SocketConnection.this);
            }
        });
    }

    public void close(){

        if(mSocket != null){

            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(!mThread.isInterrupted()){
            mThread.interrupt();
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onSocketDisconnected(SocketConnection.this);
            }
        });
    }

    public void sendMessage(String msg){

        if(mSocket == null)
            return;

        try {
            PrintWriter out = new PrintWriter(mSocket.getOutputStream(),true);
            out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
