package com.votafore.warlords.test2;

import android.os.Handler;
import android.os.Looper;
import android.os.Trace;

import com.votafore.warlords.test.ConnectionManager2;

import java.net.InetAddress;

public abstract class SocketConnectionAdder {

    protected ConnectionManager2 mManager;

    protected Handler mHandler;

    public SocketConnectionAdder(ConnectionManager2 manager){

        Trace.beginSection("SocketConnectionAdder");

        mManager = manager;
        mManager.setAdder(this);

        mHandler = new Handler(Looper.getMainLooper());

        Trace.endSection();
    }

    public abstract void addConnection();
    public abstract void addConnection(final InetAddress serverIP, final int mServerPort);
    public abstract void stop();

}
