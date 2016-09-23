package com.votafore.warlords.tests;


public class CMWifiServer implements IClient,ISocketCallback{

    private IServer mServer;

    public CMWifiServer(IServer server){

        mServer = server;
    }

    @Override
    public void onMessageReceived(String msg) {

        ////////////////////
        // РАССЫЛКА СООБЩЕНИЯ
        ////////////////////

        // берем список подключений и рассылаем параметры
    }




    //////////////////////////////
    // прослушивание сокетов
    //////////////////////////////

    @Override
    public void onObtainMessage(String msg){
        mServer.handleCommand(msg);
    }





    //////////////////////////////
    // управление локальным клиентом
    //////////////////////////////


}