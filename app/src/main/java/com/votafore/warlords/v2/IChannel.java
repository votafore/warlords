package com.votafore.warlords.v2;


import org.json.JSONObject;

import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;

/**
 * @author Votafore
 * Created on 20.12.2017.
 *
 * presents object responsible for sending of information to server
 */

public interface IChannel {
    PublishProcessor<JSONObject> getSender();
    void setReceiver(Consumer<JSONObject> c);

    // TODO: 23.12.2017 add a "close" method to interface
}