package com.votafore.warlords.v2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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

public class Channel implements IChannel {

    // TODO: 21.12.2017 check if using of interface is necessary
    
    protected PublishProcessor<JSONObject> sender;
    protected Consumer<JSONObject> receiver;

    public Channel(){

        sender = PublishProcessor.create();
        sender.observeOn(Schedulers.io());

        receiver_map_dsp = new HashMap<>();
        sender_map_dsp   = new HashMap<>();
    }

    public void close(){
        sender.onComplete();
    }

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
    protected Map<ISocket, Disposable> receiver_map_dsp;
    protected Map<ISocket, Disposable> sender_map_dsp;

    /**
     * This method create subscriber, that defines channel's behaviour when new socket is created
     *
     * whenever new socket is added he has to be set as observer for PublishProcessor (sender)
     * @return Consumer<Socket> - may be set to PublishProcessor or Observable
     */
    public Consumer<ISocket> getSubscriber(){

        return new Consumer<ISocket>() {
            @Override
            public void accept(final ISocket socket) throws Exception {

                //Log.v("TESTRX", ">>>>>>>>> got new socket in subscriber :" + socket.toString());

                sender_map_dsp.put(socket, sender.subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        Log.v("TESTRX", ">>>>>>>>> Channel - socket subscriber. send request for server info into output: " + jsonObject.toString());
                        socket.send(jsonObject);
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
                    public void subscribe(final ObservableEmitter<JSONObject> e) throws Exception {

                        socket.setDataListener(new IDataListener<String>() {
                            @Override
                            public void onDataReceived(String data) {
                                if(data == null){
                                    e.onComplete();
                                    // TODO: 21.12.2017 close socket
                                }else{
                                    //Log.v("TESTRX", ">>>>>>>>> Channel - socket input. got data!!!! yahooooo");
                                    try {
                                        e.onNext(new JSONObject(data));
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                })
                        //.subscribeOn(Schedulers.newThread())
                        .subscribe(receiver, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {

                                Log.v("TESTRX", ">>>>>>>>> Channel - socket . FINISH ?????");

                                receiver_map_dsp.get(socket).dispose();
                                receiver_map_dsp.remove(socket);

                                // TODO: 21.12.2017 check if current disposable is disposed
                                sender_map_dsp.remove(socket);
                            }
                        }));
            }
        };
    }


    /**
     * Description
     *
     *
     */





    /************ tests *************/


}