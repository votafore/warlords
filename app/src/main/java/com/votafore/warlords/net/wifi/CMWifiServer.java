package com.votafore.warlords.net.wifi;

import android.util.Log;

import com.votafore.warlords.net.IClient2;
import com.votafore.warlords.net.IServer2;
import com.votafore.warlords.net.ISocketListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class CMWifiServer implements IServer2, ISocketListener {

    private IClient2            mClient;
    private SocketConnection3   mConnection;


    private String TAG = "MSOCKET_CMWifiServer";

    public CMWifiServer(IClient2 client) {

        Log.v(TAG, "создаем объект CMWifiServer");

        mClient = client;

        mServerIP       = "192.168.0.101";
        mServerPort     = 6000;
    }

    /*******************************************************************************************************/
    /************************************************** SERVER *********************************************/

    @Override
    public void connect() {

        Log.v(TAG, "IServer2: connect");

        new Thread(new Runnable() {
            @Override
            public void run() {

                Socket socket;

                try {
                    socket      = new Socket(InetAddress.getByName(mServerIP), mServerPort);
                    mConnection = new SocketConnection3(socket, CMWifiServer.this);

                    Log.v(TAG, "IServer2: создали SocketConnection3");

                    onSocketConnected(mConnection);

                } catch (IOException e) {
                    Log.v(TAG, "IServer2: НЕ создали SocketConnection3");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void disconnect(){

        Log.v(TAG, "IServer2: disconnect");

        if(mConnection == null)
            return;

        mConnection.close();
        Log.v(TAG, "IServer2: закрыли сокет");

        onSocketDisconnected(mConnection);
    }

    @Override
    public void handleCommand(String command) {

        ////////////////////
        // ОТПРАВКА СООБЩЕНИЯ
        ////////////////////

        Log.v(TAG, "IServer2: отправляем сообщение серверу");
        mConnection.sendMessage(command);
    }

    /*******************************************************************************************************/
    /**************************************** SOCKET LISTENER **********************************************/

    @Override
    public void onObtainMessage(String msg){
        Log.v(TAG, "ISocketListener: (onObtainMessage) отправляем полученное сообщение сервера клиенту ");
        mClient.onMessageReceived(msg);
    }

    @Override
    public void onSocketConnected(SocketConnection3 connection){
        Log.v(TAG, "ISocketListener: onSocketConnected");
    }

    @Override
    public void onSocketDisconnected(SocketConnection3 connection){
        Log.v(TAG, "ISocketListener: onSocketDisconnected");
    }


    /****************************************** возможно не самые нужные переменные *******************/

    public String   mServerIP;
    public int      mServerPort;
}