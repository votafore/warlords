package com.votafore.warlords.net.wifi;


import com.votafore.warlords.game.Instance;
import com.votafore.warlords.net.IClient2;
import com.votafore.warlords.net.IServer2;
import com.votafore.warlords.net.ISocketCallback;

public class CMWifiClient implements IClient2,ISocketCallback {

    private IServer2 mServer;
    private IClient2 mLocalClient;

    public CMWifiClient(Instance instance){

        mLocalClient  = instance;
        mServer       = instance;
    }

    @Override
    public void onMessageReceived(String msg) {

        ////////////////////
        // РАССЫЛКА СООБЩЕНИЯ
        ////////////////////

        // берем список подключений и рассылаем параметры


        // и не забываем о локальном клиенте
        mLocalClient.onMessageReceived("");
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