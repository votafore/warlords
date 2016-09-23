package com.votafore.warlords.net;

/**
 * @author Votafore
 * Created on 22.09.2016.
 */

public interface IServer {

    // работа с объектами
    void addUnit();

    // служебные методы сервера
    void connect();
    void disconnect();
}
