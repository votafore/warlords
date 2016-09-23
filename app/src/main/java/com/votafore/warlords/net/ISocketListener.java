package com.votafore.warlords.net;


import com.votafore.warlords.net.wifi.SocketConnection3;

public interface ISocketListener {
    void onObtainMessage(String msg);
    void onSocketConnected(SocketConnection3 connection);
    void onSocketDisconnected(SocketConnection3 connection);
}