package com.votafore.warlords.net;


public interface IConnection {

    void send(String command);
    void close();
}
