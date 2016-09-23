package com.votafore.warlords.net;

/**
 * @author Votafore
 * Created on 22.09.2016.
 *
 * класс, описывающий объект для локального подключения
 */

public class CMLocal implements IClient,IServer{

    private IClient mClient;
    private IServer mServer;

    public CMLocal(IClient client, IServer server){

        mClient = client;
        mServer = server;
    }

    /////////////////////////////
    // методы для клиента
    /////////////////////////////

    @Override
    public void onMessageReceive(int ID) {
        mClient.onMessageReceive(ID);
    }

    @Override
    public void onConnectionChange(boolean connected) {

    }



    /////////////////////////////
    // методы для сервера
    /////////////////////////////

    @Override
    public void addUnit() {
        mServer.addUnit();
    }

    @Override
    public void connect() {
        mClient.onConnectionChange(true);
    }

    @Override
    public void disconnect() {
        mClient.onConnectionChange(false);
    }
}
