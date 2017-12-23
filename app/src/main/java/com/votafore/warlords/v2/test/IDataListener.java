package com.votafore.warlords.v2.test;

/**
 * @author Votafoer
 * Created on 24.12.2017.
 *
 * Callback that called when data received
 */

public interface IDataListener<T> {
    T onDataReceived();
}
