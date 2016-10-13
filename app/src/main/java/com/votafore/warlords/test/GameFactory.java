package com.votafore.warlords.test;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.votafore.warlords.GameManager;
import com.votafore.warlords.game.EndPoint;
import com.votafore.warlords.game.Instance;
import com.votafore.warlords.game.Server;
import com.votafore.warlords.net.ConnectionChanel;
import com.votafore.warlords.net.IConnection;
import com.votafore.warlords.net.ISocketListener;
import com.votafore.warlords.support.Stack;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * @author Votafore
 * Created on 17.09.2016.
 */
public class GameFactory {

    private static volatile GameFactory mThis;

    public static GameFactory getInstance(Context context){

        if(mThis == null)
            mThis = new GameFactory();

        return mThis;
    }

    private GameFactory(){

        //Log.v(GameManager.TAG + "_1", "GameFactory: вызвали конструктор");
    }



    /*************************************************************************************************/
    /******************************** обработка контрольных событий **********************************/
    /*************************************************************************************************/

    /************************ Activity with server list */

    public ServiceScanner mScanner;

    public void onActivityCreate(Context context){

        //Log.v(GameManager.TAG + "_1", "GameFactory - onActivityCreate");

        mAdapter = new ListAdapter();

        mScanner = new ServiceScanner(context);
        mScanner.setAdapter(mAdapter);
    }

    public void onActivityResume(){

        //Log.v(GameManager.TAG + "_1", "GameFactory - onActivityResume");

        mScanner.startScan();

        if(mBroadcaster != null){
            mBroadcaster.startBroadcast();
        }
    }

    public void onActivityPause(){

        //Log.v(GameManager.TAG + "_1", "GameFactory - onActivityPause");

        mScanner.stopScan();

        if(mBroadcaster != null){
            mBroadcaster.stopBroadcast();
        }
    }



    /******************************************** other */

    private EndPoint mServer;

    public void createServer(final Context context){

        //Log.v(GameManager.TAG + "_1", "GameFactory - createServer");

        new Thread(new Runnable() {
            @Override
            public void run() {

                // настройка серверной части
                Server server;

                server       = new Server();
                serverChanel = new ConnectionChanel(ConnectionChanel.TYPE_FOR_SERVER);

                server.setChanel(serverChanel);
                serverChanel.registerObserver(server);
                serverChanel.getConnectionAppend().addConnection();

                mServer = server;



                //Log.v(GameManager.TAG, "GameFactory - createServer. Создание первого итема списка");

                ListAdapter.ListItem item = new ListAdapter.ListItem();

                item.mCreator     = 123;
                item.mCreatorName = "Andrew";
                item.mResMap      = android.R.drawable.ic_lock_idle_lock;
                item.mHost        = "/"+getLocalIpAddress(context);

                //Log.v(GameManager.TAG, "GameFactory - createServer. Создание первого итема списка. ХОСТ - " + item.mHost);

                mAdapter.addItem(item);

                // дадим ненмого времени на подключение сервера сокетов
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // регистрация сервиса для автоподключения
                NsdServiceInfo info;

                info = new NsdServiceInfo();
                info.setServiceName(ServiceBroadcaster.mServiceName);
                info.setServiceType(ServiceBroadcaster.mServiceType);
                info.setPort(serverChanel.getPort());


                mBroadcaster = new ServiceBroadcaster(context);
                mBroadcaster.setServiceInfo(info);

                mBroadcaster.startBroadcast();
            }
        }).start();
    }

    public void startGame(Context context){

        //Log.v(GameManager.TAG + "_1", "GameFactory - startGame");

        mScanner.stopScan();
        mScanner.close();

        if(mBroadcaster != null){
            mBroadcaster.stopBroadcast();
        }



        //ConnectionChanel clientChanel;
        Instance          mInstance;

        mInstance     = new Instance(context);
        clientChanel  = new ConnectionChanel(ConnectionChanel.TYPE_FOR_CLIENT);

        mInstance.setChanel(clientChanel);
        clientChanel.registerObserver(mInstance);

        if(mServer != null){

            ClientAdapter adapter = new ClientAdapter(clientChanel, serverChanel);

            clientChanel.onSocketConnected(adapter.getClientSocket());
            serverChanel.onSocketConnected(adapter.getServerSocket());

            //mAdapter.getItemByHost("undefined").mConnection = adapter.getClientSocket();
        }else{

            // TODO: установить выбранное подключения для клиентского канала
        }
    }

    public void exit(){

        //Log.v(GameManager.TAG + "_1", "GameFactory - exit: остановка сканера");

        mScanner.stopScan();
        mScanner.close();

        if(mBroadcaster != null){

            //Log.v(GameManager.TAG, "GameFactory - exit: остановка mBroadcaster");

            mBroadcaster.stopBroadcast();
        }

        if(mServer != null){

            //Log.v(GameManager.TAG, "GameFactory - exit: остановка сервера");

            serverChanel.close();
            serverChanel.clearObservers();
        }

        if(clientChanel != null){

            //Log.v(GameManager.TAG, "GameFactory - exit: остановка клиента (канала клиента)");

            clientChanel.close();
            clientChanel.clearObservers();
        }

        mServer       = null;
        mBroadcaster  = null;
    }



















    /*************************************************************************************************/
    /*********************************** РАБОТА ПО СЕТИ В ЭТОМ КЛАССЕ ********************************/
    /*************************************************************************************************/

    /******************************** для взаимодействия Клиент - Сервер *****************************/




    /**
     * связь клиента и сервера происходит благодаря каналам связи (для клиента свой, для сервера - свой)
     * пользователь канала подключается к нему как наблюдатель (ну и не только) что бы получать
     * входящие сообщения.
     *
     * кроме клиента или сервера каналом могут пользоваться и другие объекты
     */
    private ConnectionChanel clientChanel;
    private ConnectionChanel serverChanel;

    private ServiceBroadcaster mBroadcaster;


    public void stopServer(){

        Log.v(GameManager.TAG + "_1", "GameManager: stopServer(). закрываем подключения сервера");

        if(serverChanel == null)
            return;

        serverChanel.close();
        serverChanel.clearObservers();
    }

    /******************************* вспомогательные объекты и переменные ****************************/


    private class ClientAdapter{

        private IConnection mClientSocket;
        private IConnection mServerSocket;

        private ISocketListener mServerChanel;
        private ISocketListener mClientChanel;

        public ClientAdapter(ISocketListener clientChanel, ISocketListener serverChanel){

            mClientChanel = clientChanel;
            mServerChanel = serverChanel;

            mClientSocket = new IConnection() {

                Stack stack = new Stack(50);

                @Override
                public void put(String command) {
                    stack.put(command);
                }

                @Override
                public void send() {

                    if(!stack.hasNext())
                        return;

                    mServerChanel.onCommandReceived(mServerSocket, stack.get());
                }

                @Override
                public void close() {
                    mClientChanel.onSocketDisconnected(mClientSocket);
                }
            };

            mServerSocket = new IConnection() {

                Stack stack = new Stack(50);

                @Override
                public void put(String command) {
                    stack.put(command);
                }

                @Override
                public void send() {

                    if(!stack.hasNext())
                        return;

                    mClientChanel.onCommandReceived(mClientSocket, stack.get());
                }

                @Override
                public void close() {
                    mServerChanel.onSocketDisconnected(mServerSocket);
                }
            };
        }

        public IConnection getClientSocket(){
            return mClientSocket;
        }

        public IConnection getServerSocket(){
            return mServerSocket;
        }
    }


    /***************************** отображение найденных игр (серверов) ******************************/


    private ListAdapter mAdapter;

    public ListAdapter getAdapter(){
        return mAdapter;
    }

    public interface ClickListener{
        void onClick(int position);
    }



    /************************************** раздел еще в разработке **********************************/


    public void someFunc(){

        Log.v(GameManager.TAG + "_1", "GameManager: someFunc(). произвольная функция инстанса");

        //mInstance.someFunc();
    }

    public void stopClient(){

        Log.v(GameManager.TAG + "_1", "GameManager: stopClient(). остановка клиента");

        clientChanel.close();
        clientChanel.clearObservers();
    }

    public static String getLocalIpAddress(Context context) {
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress()) {
//                        return inetAddress.getHostAddress().toString();
//                    }
//                }
//            }
//        } catch (SocketException ex) {
//            Log.e("ServerActivity", ex.toString());
//        }
//        return null;

        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return ip;
    }
}
