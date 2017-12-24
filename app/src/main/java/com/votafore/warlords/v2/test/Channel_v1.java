package com.votafore.warlords.v2.test;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Votafore
 * Created on 20.12.2017.
 */

public abstract class Channel_v1 {

    protected CompositeDisposable mDisposable;
    protected PublishProcessor<JSONObject> sender;
    protected Consumer<JSONObject> receiver;

    public Channel_v1(){

        mDisposable = new CompositeDisposable();

        sender = PublishProcessor.create();
        sender.observeOn(Schedulers.io());
    }

    public PublishProcessor<JSONObject> getProcessor(){
        return sender;
    }

    public void close(){
        mDisposable.dispose();
    }

    public void setConsumer(Consumer<JSONObject> consumer){
        receiver = consumer;
    }

    protected void onSocketAdded(final com.votafore.warlords.v2.test.Socket socket){

//        mDisposable.add(sender.subscribe(new Consumer<JSONObject>() {
//            @Override
//            public void accept(JSONObject jsonObject) throws Exception {
//                socket.output.print(jsonObject.toString());
//            }
//        }, new Consumer<Throwable>() {
//            @Override
//            public void accept(Throwable throwable) throws Exception {
//
//            }
//        }, new Action() {
//            @Override
//            public void run() throws Exception {
//                try {
//                    socket.close();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }));


//        mDisposable.add(Observable.create(new ObservableOnSubscribe<JSONObject>() {
//            @Override
//            public void subscribe(ObservableEmitter<JSONObject> e) throws Exception {
//
////                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
////
////                String data = "";
////
////                while (data != null){
////
////                    try {
////                        data = null;
////                        data = input.readLine();
////                    } catch (IOException exception) {
////                        exception.printStackTrace();
////                    }
////
////                    if(data == null){
////                        e.onError(new Throwable("no data"));
////                    }else{
////                        e.onNext(new JSONObject(data));
////                    }
////                }
//            }
//        })
//                .observeOn(Schedulers.newThread())
//                .subscribe(receiver));

    }

    public abstract void addSocket();
    public abstract void addSocket(InetAddress ip, int port);

}
