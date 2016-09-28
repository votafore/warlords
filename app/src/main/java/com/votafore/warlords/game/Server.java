package com.votafore.warlords.game;

import com.votafore.warlords.net.IClient;
import com.votafore.warlords.net.IServer;
import com.votafore.warlords.test.TestServerConnection;

/**
 * @author Votafore
 * Created on 28.09.2016.
 *
 * серверная часть игры
 */

public class Server implements IServer{










    /*****************************************************************************************************************/
    /*********************************************** РАЗДЕЛ РАБОТЫ ПО СЕТИ (ИЛИ ЛОКАЛЬНО) ****************************/
    /*****************************************************************************************************************/

    IClient mClient;

    public void setClient(IClient client){
        mClient = client;
    }



    /*****************************************************************************************************************/
    /*********************************************** ОБЯЗАННОСТИ СЕРВЕРА ;) ******************************************/
    /*****************************************************************************************************************/


    @Override
    public void handleCommand(String command) {

        // сообщение полученное от одного получат все
        mClient.onMessageReceived(command);
    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }
}
