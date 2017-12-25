package com.votafore.warlords.v2.versions;

import java.net.InetAddress;

/**
 * @author Votafore
 * Created on 20.12.2017.
 *
 * presents object responsible for sending of information to server
 */

public interface IChannel_v1 {
    void connect(InetAddress ip, int port);
    void disconnect();
}
