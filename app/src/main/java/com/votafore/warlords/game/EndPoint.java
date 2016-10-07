package com.votafore.warlords.game;

import com.votafore.warlords.net.ConnectionChanel;
import com.votafore.warlords.net.SocketConnection;


public abstract class EndPoint implements ConnectionChanel.IObserver{

    protected ConnectionChanel mChanel;

    public void setChanel(ConnectionChanel chanel){
        mChanel = chanel;
    }

    @Override
    public void notifyObserver(int connectionID, String message){
        execute(message);
    }

    public abstract void execute(String command);
}