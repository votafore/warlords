package com.votafore.warlords.net;


public interface IConnection {

    void put(String command);
    void send();
    void close();
}
