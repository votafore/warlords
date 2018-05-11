package com.votafore.warlords.v4.network;

import android.content.Context;

import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Votafore
 * Created on 26.12.2017.
 *
 * abstraction for server
 */

public interface IServer {

    /**
     *
     * @param receiver
     */
    Disposable setReceiver(Consumer<JSONObject> receiver);

    /**
     *
     * @param data
     */
    void send(JSONObject data);

    /**
     * method for starting server
     */
    void start();

    /**
     * method for stopping server
     */
    void stop();


    /************ utils *************/

    /**
     * method for start searching connections
     */
    void startSearching(Context context);

    /**
     * method for stop searching connections
     */
    void stopSearching();


    void setSearchingListener(ISearchingListener listener);
}
