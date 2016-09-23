package com.votafore.warlords.net.wifi;

import android.util.Log;

import com.votafore.warlords.net.ISocketListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Votafore
 * Created on 22.09.2016.
 */

public class SocketConnection3 {

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

    private String TAG = "MSOCKET_Connection3";

    public SocketConnection3(final Socket socket, ISocketListener listener){

        mSocket   = socket;
        mListener = listener;

        try {
            mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            Log.v(TAG, "получили входящий поток сокета");
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG, "НЕ получили входящий поток сокета");
            return;
        }

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
                            mListener.onSocketDisconnected(SocketConnection3.this);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        continue;
                    }

                    // при получении сообщения отправляем другое сообщение
                    // в основной поток сервера

                    mListener.onObtainMessage(msg);
                }
            }
        });

        mThread.start();
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
