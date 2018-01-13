package com.votafore.warlords.v4.network;

/**
 * @author Vorafore
 * Created on 26.12.2017.
 */

public interface IDataReceiver<T> {
    void onDataReceived(T data);
}
