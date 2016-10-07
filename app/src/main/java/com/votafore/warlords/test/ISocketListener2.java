package com.votafore.warlords.test;


import com.votafore.warlords.net.IConnection;

public interface ISocketListener2 {

    void onIncommingCommandReceived(IConnection2 connection, String message);
    void onSocketConnected(IConnection2 connection);
    void onSocketDisconnected(IConnection2 connection);
}
