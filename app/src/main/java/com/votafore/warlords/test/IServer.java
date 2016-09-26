package com.votafore.warlords.test;


public interface IServer {
    void handleCommand(String command);

    void connect();
    void disconnect();
}
