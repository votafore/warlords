package com.votafore.warlords.net;


public interface IClient2 {

    void onMessageReceived(String msg);
    void release();
}
