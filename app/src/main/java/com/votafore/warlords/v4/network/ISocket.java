//package com.votafore.warlords.v4.network;
//
//import org.json.JSONObject;
//
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.processors.PublishProcessor;
//
///**
// * @author Votafore
// * Created on 26.12.2017.
// */
//
//public interface ISocket {
//    void send(JSONObject data);
//    void subscribeSocket(PublishProcessor<JSONObject> sender);
//    Disposable setReceiver(Consumer<JSONObject> consumer);
//
//
//    void close();
//}
