package com.votafore.warlords.game;

import com.votafore.warlords.test.ConnectionManager2;


public abstract class EndPoint {

    protected ConnectionManager2 mConnectionManager2;

    public void setConnectionManager2(ConnectionManager2 manager2){
        mConnectionManager2 = manager2;
    }

    public abstract void execute(String command);
}