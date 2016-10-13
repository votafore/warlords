package com.votafore.warlords.game;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;

import com.votafore.warlords.net.ConnectionChanel;
import com.votafore.warlords.net.IConnection;

public abstract class EndPoint implements ConnectionChanel.IObserver{

    protected ConnectionChanel      mChanel;
    protected HandlerThread         mWorkerThread;
    protected Handler               mWorkerHandler;

    public EndPoint(){

        mWorkerThread = new HandlerThread("EndPoint thread");
        mWorkerThread.start();

        mWorkerHandler = new Handler(mWorkerThread.getLooper());

        // TODO: подумать как остановить этот поток
    }

    public void setChanel(ConnectionChanel chanel){
        mChanel = chanel;
    }

    @Override
    public void notifyObserver(int connectionID, final String message){

        final IConnection queryConnection;
        queryConnection = mChanel.getConnections().get(connectionID);

        mWorkerHandler.post(new Runnable() {
            @Override
            public void run() {
                execute(queryConnection, message);
            }
        });

    }

    public abstract void execute(IConnection connection, String command);

}