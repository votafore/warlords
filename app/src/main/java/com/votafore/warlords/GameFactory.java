package com.votafore.warlords;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.opengl.GLSurfaceView;
import android.text.format.Formatter;
import android.util.Log;

import com.votafore.warlords.game.EndPoint;
import com.votafore.warlords.game.Instance;
import com.votafore.warlords.game.Server;
import com.votafore.warlords.net.ConnectionChanel;
import com.votafore.warlords.net.IConnection;
import com.votafore.warlords.net.ISocketListener;
import com.votafore.warlords.net.SocketConnection;
import com.votafore.warlords.support.Stack;
import com.votafore.warlords.support.ListAdapter;
import com.votafore.warlords.support.ServiceBroadcaster;
import com.votafore.warlords.support.ServiceScanner;
import com.votafore.warlords.test.MeshMapTest;


/**
 * @author Votafore
 * Created on 17.09.2016.
 */
public class GameFactory {

    private static volatile GameFactory mThis;

    public static GameFactory getInstance(){

        if(mThis == null)
            mThis = new GameFactory();

        return mThis;
    }

    public GameFactory(){

        //Log.v(GameFactory.TAG + "_1", "GameFactory: вызвали конструктор");
    }



    /*************************************************************************************************/
    /******************************** обработка контрольных событий **********************************/
    /*************************************************************************************************/

    /************************ Activity with server list */

    public ServiceScanner mScanner;

    public void onActivityCreate(final Context context){

        //Log.v(GameFactory.TAG, "GameFactory - onActivityCreate");

        mAdapter = new ListAdapter();

        mScanner = new ServiceScanner(context);
        mScanner.setAdapter(mAdapter);
    }

    public void onActivityResume(){

        //Log.v(GameFactory.TAG, "GameFactory - onActivityResume");

        mScanner.startScan();

        if(mBroadcaster != null){
            mBroadcaster.startBroadcast();
        }
    }

    public void onActivityPause(){

        //Log.v(GameFactory.TAG, "GameFactory - onActivityPause");

        mScanner.stopScan();

        if(mBroadcaster != null){
            mBroadcaster.stopBroadcast();
        }
    }



    /******************************************** other */

    private EndPoint mServer;

    public void createServer(final Context context){

        Log.v(GameFactory.TAG, "GameFactory - createServer");

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



                Log.v(GameFactory.TAG, "GameFactory - createServer. Создание первого итема списка");

                ListAdapter.ListItem item = new ListAdapter.ListItem();

                item.mCreator     = 123;
                item.mCreatorName = "Andrew";
                item.mResMap      = android.R.drawable.ic_lock_idle_lock;
                item.mHost        = getLocalIpAddress(context);

                Log.v(GameFactory.TAG, "GameFactory - createServer. Создание первого итема списка. ХОСТ - " + item.mHost);

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

    public void startGame(int selectedServerPosition, Context context){

        Log.v(GameFactory.TAG, "GameFactory - startGame");

        ListAdapter.ListItem item = mAdapter.getItemByPosition(selectedServerPosition);

        // если выбран удаленный сервер игры
        if(item.mConnection != null){

            // при закрытии сканера закрывается и канал, а при этом закрываются все сокеты
            // из списка.
            // поэтому что бы сохранить подключение активным мы удаляем его из списка (канал сканера)
            // и передаем в новый канал (клиента)... а все остальное закрываем
            mScanner.getChanel().onSocketDisconnected(item.mConnection);
        }

        mScanner.stopScan();
        mScanner.close();

        if(mBroadcaster != null){
            mBroadcaster.stopBroadcast();
        }





        mInstance     = new Instance(context);
        clientChanel  = new ConnectionChanel(ConnectionChanel.TYPE_FOR_CLIENT);

        mInstance.setMap(new MeshMapTest(context));
        mInstance.setChanel(clientChanel);
        clientChanel.registerObserver(mInstance);

        if(item.mConnection == null){

            Log.v(GameFactory.TAG, "GameFactory - startGame. Выбрали игру с сервером на текущем девайсе");

            // выбрана игра с сервером на текущем девайсе
            mLocalAdapter = new ClientAdapter(clientChanel, serverChanel);

            clientChanel.onSocketConnected(mLocalAdapter.getClientSocket());
            serverChanel.onSocketConnected(mLocalAdapter.getServerSocket());

        }else{

            Log.v(GameFactory.TAG, "GameFactory - startGame. Выбрали игру с сервером на удаленном девайсе");

            // TODO: место для оптимизации
            // подмена слушателя сокета т.к. был канал сканера,
            // теперь будет канал клиента
            ((SocketConnection)item.mConnection).setListener(clientChanel);

            clientChanel.onSocketConnected(item.mConnection);

            if(mServer != null){

                mServer.stop();

                mServer        = null;
                serverChanel   = null;
            }
        }


        game = new Game();
        game.setClient(mInstance);

        if(mServer != null)
            game.setServer(mServer);

        game.start(context);
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

            mServer.stop();
        }

        if(mInstance != null){

            //Log.v(GameManager.TAG, "GameFactory - exit: остановка клиента (канала клиента)");

            mInstance.stop();
        }

        mServer       = null;
        serverChanel  = null;
        mBroadcaster  = null;
    }




    // временно... для тестов
    Instance          mInstance;

    ClientAdapter mLocalAdapter;

    Game game;
















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

        Log.v(GameFactory.TAG + "_1", "GameManager: stopServer(). закрываем подключения сервера");

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
                    //Log.v(GameFactory.TAG, "ClientAdapter: ClientSocket - put(). Команда в стеке. Размер стека: " + String.valueOf(stack.size()));
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
                    //Log.v(GameFactory.TAG, "ClientAdapter: ServerSocket - put(). Команда в стеке. Размер стека: " + String.valueOf(stack.size()));
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


    public ListAdapter mAdapter;

    public ListAdapter getAdapter(){
        return mAdapter;
    }

    public interface ClickListener{
        void onClick(int position);
    }



    /************************************** раздел еще в разработке **********************************/

    public static final String TAG = "TEST";

    public void someFunc(){

        Log.v(GameFactory.TAG + "_1", "GameManager: someFunc(). произвольная функция инстанса");

        mInstance.someFunc();
    }

    public void stopClient(){

        Log.v(GameFactory.TAG + "_1", "GameManager: stopClient(). остановка клиента");

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
        String ip = "/" + Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return ip;
    }

    public GLSurfaceView getSurfaceView(){
        return game.getSurfaceView();
    }



    // TODO: сделать слежение за состоянием Wi-Fi при запуске и во время игры


}
