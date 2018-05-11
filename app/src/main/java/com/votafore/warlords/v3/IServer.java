//package com.votafore.warlords.v3;
//
//import android.content.Context;
//
//import org.json.JSONObject;
//
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//
///**
// * @author Votafore
// * Created on 26.12.2017.
// *
// * abstraction for server
// */
//
//public interface IServer {
//
//    /**
//     *
//     * @param receiver
//     */
//    Disposable setReceiver(Consumer<JSONObject> receiver);
//
//    /**
//     *
//     * @param data
//     */
//    void send(JSONObject data);
//
//    /**
//     * method for starting server
//     * @param context
//     */
//    void start(Context context);
//
//    /**
//     * method for stopping server
//     */
//    void stop();
//}
