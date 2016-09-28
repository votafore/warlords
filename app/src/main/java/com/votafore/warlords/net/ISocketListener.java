package com.votafore.warlords.net;


import com.votafore.warlords.net.wifi.SocketConnection;

public interface ISocketListener {
    void onObtainMessage(SocketConnection connection, String msg);
    void onSocketConnected(SocketConnection connection);
    void onSocketDisconnected(SocketConnection connection);
}