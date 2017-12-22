package com.votafore.warlords.v2.test;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Votafore
 * Created on 20.12.2017.
 *
 * abstraction for consumer.
 * object-consumer doesn't have to know anythig about interaction with sockets.
 * he just sends command into/via channel. That's it.
 *
 * object responsible for sending data via socket(s)
 */

public abstract class Channel_v2 implements IChannel_v2 {

    protected PublishProcessor<JSONObject> sender;
    protected Consumer<JSONObject> receiver;

    public Channel_v2(){

        sender = PublishProcessor.create();
        sender.observeOn(Schedulers.io());

        receiver_map_dsp = new HashMap<>();
        sender_map_dsp = new HashMap<>();
    }

    public void close(){

        sender.onComplete();

        // TODO: 21.12.2017 check if code below is redundant
        for(Socket s: receiver_map_dsp.keySet()){
            receiver_map_dsp.get(s).dispose();
        }
        receiver_map_dsp.clear();

        for(Socket s: sender_map_dsp.keySet()){
            sender_map_dsp.get(s).dispose();
        }
        sender_map_dsp.clear();
    }



    public abstract void addSocket();
    public abstract void addSocket(InetAddress ip, int port);

    @Override
    public PublishProcessor<JSONObject> getSender(){
        return sender;
    }

    @Override
    public void setReceiver(Consumer<JSONObject> c){
        receiver = c;
    }








    /*************** UTILS ***************/

    /**
     * in order to know which disposable was created for socket maps are made
     */
    protected Map<Socket, Disposable> receiver_map_dsp;
    protected Map<Socket, Disposable> sender_map_dsp;

    /**
     * whenever new socket is added he has to be set as observer for PublishProcessor (sender)
     * @param socket
     */
    @Deprecated
    protected void onSocketAdded(final Socket socket){

        sender_map_dsp.put(socket, sender.subscribe(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                Log.v("TESTRX", "Channel_v2 - onSocketAdded >>>>>>>>> Chanel for list. send request");
                socket.output.print(jsonObject.toString());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                // method "dispose" will be called when receiver unsubscribe
                // or, may be, it is not required
            }
        }));


        receiver_map_dsp.put(socket, Observable.create(new ObservableOnSubscribe<JSONObject>() {
            @Override
            public void subscribe(ObservableEmitter<JSONObject> e) throws Exception {

                String data = "";

                while (data != null){

                    try {
                        data = null;
                        data = socket.input.readLine();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }

                    if(data == null){
                        e.onComplete();
                        // TODO: 21.12.2017 close socket
                    }else{
                        e.onNext(new JSONObject(data));
                    }
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .subscribe(receiver, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        receiver_map_dsp.get(socket).dispose();
                        receiver_map_dsp.remove(socket);

                        // TODO: 21.12.2017 check if current disposable is disposed
                        sender_map_dsp.remove(socket);
                    }
                }));
    }


    /**
     * Description
     *
     *
     */





    /************ tests *************/

    /**
     * This method create subscriber, that define channel's behaviour when new socket is created
     * @return Consumer<Socket> - may be set to PublishProcessor or Observable
     */
    public Consumer<Socket> TEST_getSubscriber(){

        return new Consumer<Socket>() {
            @Override
            public void accept(final Socket socket) throws Exception {

                sender_map_dsp.put(socket, sender.subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        socket.output.print(jsonObject.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        try {
                            socket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        // method "dispose" will be called when receiver unsubscribe
                        // or, may be, it is not required
                    }
                }));


                receiver_map_dsp.put(socket, Observable.create(new ObservableOnSubscribe<JSONObject>() {
                    @Override
                    public void subscribe(ObservableEmitter<JSONObject> e) throws Exception {

                        String data = "";

                        while (data != null){

                            try {
                                data = null;
                                data = socket.input.readLine();
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }

                            if(data == null){
                                e.onComplete();
                                // TODO: 21.12.2017 close socket
                            }else{
                                e.onNext(new JSONObject(data));
                            }
                        }
                    }
                })
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(receiver, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {
                                receiver_map_dsp.get(socket).dispose();
                                receiver_map_dsp.remove(socket);

                                // TODO: 21.12.2017 check if current disposable is disposed
                                sender_map_dsp.remove(socket);
                            }
                        }));
            }
        };
    }
}