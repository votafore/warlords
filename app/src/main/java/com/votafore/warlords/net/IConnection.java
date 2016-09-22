package com.votafore.warlords.net;

/**
 * @author Votafore
 * Created on 19.09.2016.
 * interface for network
 */

public interface IConnection {

    void init();
    void sendMessage();
    void release();
}
