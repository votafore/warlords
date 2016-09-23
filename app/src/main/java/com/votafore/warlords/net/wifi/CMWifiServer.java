package com.votafore.warlords.net.wifi;


import com.votafore.warlords.net.IClient2;
import com.votafore.warlords.net.IServer2;
import com.votafore.warlords.net.ISocketCallback;

public class CMWifiServer implements IServer2,ISocketCallback {

    private IClient2 mClient;

    public CMWifiServer(IClient2 client){

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