package com.votafore.warlords.net;


import com.votafore.warlords.net.ConnectionChanel;

public interface IChanel {
    void sendCommand(String command);
    void registerObserver(ConnectionChanel.IObserver observer);
    void unregisterObserver(ConnectionChanel.IObserver observer);
}