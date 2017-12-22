package com.votafore.warlords.v2.test;

import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketAddress;
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

public class Channel_v3 implements IChannel_v2 {

    // TODO: 21.12.2017 check if using of interface is necessary
    
    protected PublishProcessor<JSONObject> sender;
    protected Consumer<JSONObject> receiver;

    public Channel_v3(){

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
    protected Map<Socket, Disposable> receiver_map_dsp;
    protected Map<Socket, Disposable> sender_map_dsp;

    /**
     * This method create subscriber, that define channel's behaviour when new socket is created
     *
     * whenever new socket is added he has to be set as observer for PublishProcessor (sender)
     * @return Consumer<Socket> - may be set to PublishProcessor or Observable
     */
    public Consumer<Socket> getSubscriber(){

        return new Consumer<Socket>() {
            @Override
            public void accept(final Socket socket) throws Exception {

                Log.v("TESTRX", ">>>>>>>>> got new socket in subscriber :" + socket.toString());

                sender_map_dsp.put(socket, sender.subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        Log.v("TESTRX", ">>>>>>>>> Channel - socket subscriber. send request for server info into output: " + jsonObject.toString());

//                        try(PrintWriter pw = new PrintWriter(socket.mSocket.getOutputStream())){
//                            pw.print("hello, there!!");
//                        }

                        jsonObject.put("data", "hello, there");
                        socket.output.writeUTF(jsonObject.toString());

                        //PrintWriter pw = new PrintWriter(socket.mSocket.getOutputStream());
                        //pw.println("hello, there!!");
                        //pw.close();
                        //socket.output.println(jsonObject.toString());
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
                        while (true){

                            Log.v("TESTRX", ">>>>>>>>> Channel - socket input. wait for data");

                            try {
                                data = null;
                                data = socket.input.readUTF();

                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }

                            if(data == null){
                                e.onComplete();
                                // TODO: 21.12.2017 close socket
                                break;
                            }else{
                                Log.v("TESTRX", ">>>>>>>>> Channel - socket input. got data!!!! yahooooo");
                                JSONObject res = new JSONObject();
                                res.put("data", data);
                                e.onNext(res);
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