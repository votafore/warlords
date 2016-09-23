package com.votafore.warlords.tests;


public class CMWifiClient implements IServer,ISocketCallback {

    private IClient mClient;

    public CMWifiClient(IClient client){

        mClient = client;
    }

    @Override
    public void handleCommand(String command) {

        ////////////////////
        // ОТПРАВКА СООБЩЕНИЯ
        ////////////////////
    }




    //////////////////////////////
    // прослушивание сокетов
    //////////////////////////////

    @Override
    public void onObtainMessage(String msg){
        mClient.onMessageReceived(msg);
    }
}