package com.votafore.warlords.net;


public interface IConnection {

    void sendCommand(String command);
    void close();
}
