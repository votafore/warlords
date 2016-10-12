package com.votafore.warlords.game;

import com.votafore.warlords.net.ConnectionChanel;
import com.votafore.warlords.test.ConnectionChanel2;

public abstract class EndPoint implements ConnectionChanel.IObserver{

    protected ConnectionChanel2 mChanel;

    public void setChanel(ConnectionChanel2 chanel){
        mChanel = chanel;
    }

    @Override
    public void notifyObserver(int connectionID, String message){
        execute(message);
    }

    public abstract void execute(String command);
}