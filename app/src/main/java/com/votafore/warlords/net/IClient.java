package com.votafore.warlords.net;

/**
 * @author Vorafore
 * Created on 21.09.2016.
 *
 * интерфейс клиента (реагирование на получение сообщения с сервера)
 */

public interface IClient {

    void onMessageReceive(int ID);
    void onConnectionChange(boolean connected);
}
