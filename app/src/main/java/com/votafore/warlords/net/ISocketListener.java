package com.votafore.warlords.net;



public interface ISocketListener {

    void onIncommingCommandReceived(IConnection connection, String message);
    void onSocketConnected(IConnection connection);
    void onSocketDisconnected(IConnection connection);
}
