package com.votafore.warlords.v2.test;


import org.json.JSONObject;

import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;

/**
 * @author Votafore
 * Created on 20.12.2017.
 *
 * presents object responsible for sending of information to server
 */

public interface IChannel_v2 {
    PublishProcessor<JSONObject> getSender();
    void setReceiver(Consumer<JSONObject> c);
}
