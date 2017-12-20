package com.votafore.warlords.v2;

import android.net.nsd.NsdServiceInfo;

/**
 * @author Votafore
 * Created on 18.12.2017.
 */

public interface IAdapter {
    void addServer(ServiceInfo info);
    void removeServer(ServiceInfo info);
}
