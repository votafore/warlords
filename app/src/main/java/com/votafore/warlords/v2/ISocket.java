package com.votafore.warlords.v2;

import com.votafore.warlords.v2.IDataListener;

import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Votafore
 * Created on 20.12.2017.
 *
 * represents connection via socket
 *
 * abstraction for communication
 */

public interface ISocket {

    /**
     * abstraction for sending data
     * @param data
     */
    void send(JSONObject data) throws IOException;

    /**
     * callback for received data
     * @param listener
     */
    void setDataListener(IDataListener<String> listener);

    /**
     * close socket
     */
    void close() throws IOException;
}
