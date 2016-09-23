package com.votafore.warlords.net;


public interface IServer {
    void handleCommand(String command);

    void connect();
    void disconnect();
}
