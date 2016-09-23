package com.votafore.warlords.tests;



public class Instance implements IClient, IServer{


    public Instance(){

        mServer        = new CMWifiClient(this);

        CMWifiServer server = new CMWifiServer(this);

        mLocalClient   = server;
        mClient        = server;
    }






    //////////////////////////////////
    // CLIENT
    //////////////////////////////////

    private IServer mServer;

    @Override
    public void onMessageReceived(String msg) {

    }

    public void someFuncClient(){

        // обработка управления
        String cmd = "команда";

        mServer.handleCommand(cmd);
    }







    //////////////////////////////////
    // LOCAL CLIENT
    //////////////////////////////////

    private ISocketCallback mLocalClient;

    public void someFinc(){

        String someCommand = "create";

        mLocalClient.onObtainMessage(someCommand);
    }







    //////////////////////////////////
    // SERVER
    //////////////////////////////////


    // клиент (объект рассылающий сообщения остальным)
    // который получит уведомление об обработке
    private IClient mClient;

    @Override
    public void handleCommand(String command) {

        //////////////////////////
        // ОБРАБОТКА КОМАНДЫ
        //////////////////////////


        // возвращаем полученные данные для рассылки между остальными
        mClient.onMessageReceived(command);

    }
}