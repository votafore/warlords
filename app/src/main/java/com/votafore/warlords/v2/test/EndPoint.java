package com.votafore.warlords.v2.test;

import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;

/**
 * @author Votafore
 * Created on 20.12.2017.
 *
 * this class represent an abstraction for Client and Server
 */

public class EndPoint {

    /**
     * chanel for connection
     */
    protected IChannel_v1 mChanel;


    /**
     * Extra object for connection
     * Rx is used
     *
     * - DSPreceiver receiver for messages from another point
     * - PPsender responsible for broadcasting messages
     */

    protected Disposable                    DSPreciever;
    protected PublishProcessor<JSONObject>  PPsender;


    public EndPoint(){

    }



}