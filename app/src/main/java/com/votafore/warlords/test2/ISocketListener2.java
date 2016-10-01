package com.votafore.warlords.test2;


public interface ISocketListener2 {

    void onIncommingCommandReceived(IConnection connection, String message);
    void onSocketConnected(IConnection connection);
    void onSocketDisconnected(IConnection connection);
}
