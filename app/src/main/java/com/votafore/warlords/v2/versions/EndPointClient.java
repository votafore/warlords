package com.votafore.warlords.v2.versions;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Vorafore
 * Created on 20.12.2017.
 *
 * represent a client
 */

public class EndPointClient extends com.votafore.warlords.v2.test.EndPoint {

    public EndPointClient(InetAddress ip, int port){

        PPsender = PublishProcessor.create();
        PPsender.observeOn(Schedulers.io());

        mChanel = new Chanel(PPsender, new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                yahooooWeGotData(jsonObject);
            }
        });

        mChanel.connect(ip, port);
    }

    public void release(){
        mChanel.disconnect();
        PPsender.onComplete();
    }




    public class Chanel implements IChannel_v1 {

        private Socket mSocket;

        private PrintWriter     output;
        private BufferedReader  input;

        private Disposable dsp_receiver;
        private Disposable dsp_sender;

        public Chanel(PublishProcessor<JSONObject> p, Consumer<JSONObject> c){

            dsp_receiver = Observable.create(new ObservableOnSubscribe<JSONObject>() {
                @Override
                public void subscribe(ObservableEmitter<JSONObject> e) throws Exception {

                    String data = "";

                    while (data != null){

                        try {
                            data = null;
                            data = input.readLine();
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }

                        if(data == null){
                            e.onError(new Throwable("no data"));
                        }else{
                            e.onNext(new JSONObject(data));
                        }
                    }
                }
            })
                    .observeOn(Schedulers.io())
                    .subscribe(c);

            dsp_sender = p.subscribe(new Consumer<JSONObject>() {
                @Override
                public void accept(JSONObject jsonObject) throws Exception {
                    output.print(jsonObject.toString());
                }
            });
        }



        @Override
        public void connect(InetAddress ip, int port) {

            try {
                mSocket = new Socket(ip, port);

                output = new PrintWriter(mSocket.getOutputStream(), true);
                input  = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void disconnect() {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dsp_receiver.dispose();
            dsp_sender.dispose();
        }
    }











    /*************** miscellaneous *******************/

    private String TAG = "CLIENTTEST";


    public void funcTEST(){
        PPsender.onNext(new JSONObject());
    }

    public void yahooooWeGotData(JSONObject data){
        Log.v(TAG, ">>>>>>>>>>  yahoooooo!!!");
    }
}
