package com.votafore.warlords.test;


public interface IChanel {
    void sendCommand(String command);
    void registerObserver(ConnectionChanel.IObserver observer);
    void unregisterObserver(ConnectionChanel.IObserver observer);
}