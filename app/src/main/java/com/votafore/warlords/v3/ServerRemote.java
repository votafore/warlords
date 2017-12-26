package com.votafore.warlords.v3;

import org.json.JSONObject;

import io.reactivex.functions.Consumer;

/**
 * @author Vorafore
 * Created on 26.12.2017.
 *
 * implementation for IServer (for remote server)
 */

public class ServerRemote implements IServer {

    public ServerRemote(){

    }








    /********************* IServer ********************/

    @Override
    public void setReceiver(Consumer<JSONObject> data) {

    }

    @Override
    public void send(JSONObject data) {

    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
