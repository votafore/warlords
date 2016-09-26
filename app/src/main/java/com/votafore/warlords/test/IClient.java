package com.votafore.warlords.test;


public interface IClient {

    void onMessageReceived(String msg);
    void release();
}
