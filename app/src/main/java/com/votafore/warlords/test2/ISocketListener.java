package com.votafore.warlords.test2;


public interface ISocketListener {

    void onIncommingCommandReceived(IConnection connection, String message);
    void onSocketDisconnected(IConnection connection);
}