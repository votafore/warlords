package com.votafore.warlords.net;



public interface ISocketListener {

    void onCommandReceived(IConnection connection, String message);
    void onSocketConnected(IConnection connection);
    void onSocketDisconnected(IConnection connection);
}
