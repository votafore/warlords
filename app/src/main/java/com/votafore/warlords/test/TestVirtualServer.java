package com.votafore.warlords.test;

/**
 * @author Votafore
 * Created on 26.09.2016.
 */

public class TestVirtualServer implements IServer{

    private TestConnectionManager mConnectionManager;

    private IClient mClient;

    public TestVirtualServer(){

    }

    public void setConnectionManager(TestConnectionManager manager){
        mConnectionManager = manager;
    }


    /****************************************************************************/
    /******************************** IServer ***********************************/
    /****************************************************************************/

    @Override
    public void handleCommand(String command) {
        mConnectionManager.sendMessage(command);
    }

    @Override
    public void connect() {

        // не имеет смысла в текущем виде
        // т.к. подразумевается возможность подключения к разным серверам

        // как минимум нужны параметры для подключения к серверу

        // вполне возможно что ConnectionManager вообще будет сюда передаваться
        // уже созданным. В таком случае connect() вообще не имеет смысла.
    }

    @Override
    public void disconnect() {

    }
}