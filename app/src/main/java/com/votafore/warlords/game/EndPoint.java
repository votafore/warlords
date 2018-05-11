//package com.votafore.warlords.game;
//
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.support.annotation.Nullable;
//
//import com.votafore.warlords.net.ConnectionChanel;
//import com.votafore.warlords.net.IChanel;
//import com.votafore.warlords.net.IConnection;
//
//public abstract class EndPoint implements ConnectionChanel.IObserver{
//
//    protected IChanel               mChanel;
//    protected HandlerThread         mWorkerThread;
//    protected Handler               mWorkerHandler;
//
//    public EndPoint(){
//
//        mWorkerThread = new HandlerThread("EndPoint thread");
//        mWorkerThread.start();
//
//        mWorkerHandler = new Handler(mWorkerThread.getLooper());
//    }
//
//    public void setChanel(IChanel chanel){
//        mChanel = chanel;
//    }
//
//    public IChanel getChanel(){
//        return mChanel;
//    }
//
//    @Override
//    public void notifyObserver(final IConnection connection, final String message){
//
//        mWorkerHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                execute(connection, message);
//            }
//        });
//
//    }
//
//    public abstract void execute(IConnection connection, String command);
//
//
//    public abstract void stop();
//}