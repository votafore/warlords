package com.votafore.warlords.v2.test;

import org.json.JSONObject;

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
    void send(JSONObject data);

    /**
     * callback for receiving data
     * @param listener
     */
    void setDataListener(IDataListener listener);
}
