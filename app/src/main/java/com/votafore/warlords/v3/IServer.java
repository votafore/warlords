package com.votafore.warlords.v3;

import android.content.Context;

import org.json.JSONObject;

import io.reactivex.functions.Consumer;

/**
 * @author Votafore
 * Created on 26.12.2017.
 *
 * abstraction for server
 */

public interface IServer {
    void setReceiver(Consumer<JSONObject> receiver);
    void send(JSONObject data);

    void start(Context context);
    void stop();
}
