package com.votafore.warlords.v2.test;

import com.votafore.warlords.v2.ServiceInfo;

/**
 * @author Votafore
 * Created on 18.12.2017.
 */

public interface IAdapter {
    void addServer(ServiceInfo info);
    void removeServer(ServiceInfo info);
}
