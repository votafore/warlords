package com.votafore.warlords.net;


public interface IClient {

    void onMessageReceived(String msg);
    void release();
}
