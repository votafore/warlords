package com.votafore.warlords.v3;

import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Votafore
 * Created on 26.12.2017.
 */

public interface ISocket {
    void send(JSONObject data);
    Disposable setReceiver(Consumer<JSONObject> consumer);

    void close();
}
