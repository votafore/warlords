package com.votafore.warlords;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.opengl.GLSurfaceView;
import android.os.Trace;
import android.util.Log;
import android.view.MotionEvent;

import com.votafore.warlords.game.EndPoint;
import com.votafore.warlords.game.Instance;
import com.votafore.warlords.game.Server;
import com.votafore.warlords.glsupport.GLRenderer;
import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLView;
import com.votafore.warlords.glsupport.GLWorld;
import com.votafore.warlords.net.ConnectionChanel;
import com.votafore.warlords.net.IConnection;
import com.votafore.warlords.net.ISocketListener;
import com.votafore.warlords.test.ListAdapter;
import com.votafore.warlords.test.ServiceBroadcaster;
import com.votafore.warlords.test.ServiceScanner;

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
public class GameManager {

    private static volatile GameManager mThis;

    static GameManager getInstance(Context context){

        if(mThis == null)
            mThis = new GameManager(context);

        return mThis;
    }

    private GameManager(Context context){

        Log.v(TAG, "GameManager: вызвали конструктор");

//        mWorld = new GLWorld(this);
//        mWorld.camMove(GLWorld.AXIS_Y, 3f);
//
//        Log.v(TAG, "GameManager: пытаемся получить NsdManager");
//        mNsdManager           = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
////
//        mRegistrationListener = new NsdManager.RegistrationListener() {
//            @Override
//            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
//                Log.v(TAG, "NsdManager.RegistrationListener: onRegistrationFailed");
//            }
//
//            @Override
//            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
//                Log.v(TAG, "NsdManager.RegistrationListener: onUnregistrationFailed");
//            }
//
//            @Override
//            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
//
//                // актуализация имени сервиса
//                mServiceName = serviceInfo.getServiceName();
//
//                Log.v(TAG, "NsdManager.RegistrationListener: onServiceRegistered!!! Service name - " + mServiceName);
//            }
//
//            @Override
//            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
//                Log.v(TAG, "NsdManager.RegistrationListener: onServiceUnregistered");
//            }
//        };

        //mAdapter    = new ListAdapter();

//        mAdapter.setListener(new ClickListener() {
//            @Override
//            public void onClick(int position) {
//
//            }
//        });

//        //////////////////////////////////////////////////
//        // настройка клиентской части
//        //////////////////////////////////////////////////
//
//        Log.v(TAG, "GameManager: ***************** настройка клиента ******************");
//
//        Trace.beginSection("GameManager_setupClient");
//
//        mInstance       = new Instance(context);
//        clientChanel    = new ConnectionChanel(ConnectionChanel.TYPE_FOR_CLIENT);
//
//        mInstance.setChanel(clientChanel);
//
//        Log.v(TAG, "GameManager: ***************** настройка клиента завершена******************");
//
//        mClient = mInstance;
//        Trace.endSection();
    }


    public static String TAG = "TEST";

    /*************************************************************************************************/
    /******************************** обработка контрольных событий **********************************/
    /*************************************************************************************************/

    public ServiceScanner mScanner;

    // основные контрольные точки
    public static final String STATE_LISTACTIVITY_STARTED  = "ACTIVITY_STARTED";
    public static final String STATE_SERVER_CREATED        = "SERVER_CREATED";
    public static final String STATE_GAME_STARTED          = "GAME_STARTED";


    // дополнительные состояния
    public static final String STATE_LISTACTIVITY_RESUMED  = "ACTIVITY_RESUMED";
    public static final String STATE_LISTACTIVITY_PAUSED   = "ACTIVITY_PAUSED";


    public static final String STATE_EXIT                  = "EXIT";

    private List curStates = new ArrayList();

    public void changeState(Context context, String newState){

        Log.v(TAG, "GameManager - changeState");

        if(curStates.contains(newState))
            return;

        switch(newState){
            case STATE_LISTACTIVITY_STARTED:

                Log.v(TAG, "GameManager - changeState: STATE_LISTACTIVITY_STARTED");

                mAdapter = new ListAdapter();

                mScanner = new ServiceScanner(context);
                mScanner.setAdapter(mAdapter);

                curStates.add(newState);

                break;
            case STATE_SERVER_CREATED:

                Log.v(TAG, "GameManager - changeState: STATE_SERVER_CREATED");

                createServer(context);

                curStates.add(newState);

                break;

            case STATE_GAME_STARTED:

                Log.v(TAG, "GameManager - changeState: STATE_GAME_STARTED");

                mScanner.stopScan();

                if(curStates.contains(STATE_SERVER_CREATED)){
                    mBroadcaster.stopBroadcast();
                }

                startGame(context);

                // TODO: запускаем активити с игрой

                break;
            case STATE_EXIT:

                Log.v(TAG, "GameManager - changeState: STATE_EXIT");

                if(curStates.contains(STATE_LISTACTIVITY_STARTED)){

                    Log.v(TAG, "GameManager - changeState: Полная остановка клиента");

                    mScanner.stopScan();
                    mScanner.close();
                }

                if(curStates.contains(STATE_SERVER_CREATED)){

                    Log.v(TAG, "GameManager - changeState: Полная остановка сервера");

                    mBroadcaster.stopBroadcast();

                    serverChanel.close();
                    serverChanel.clearObservers();
                }

                curStates = new ArrayList();

                break;
            case STATE_LISTACTIVITY_RESUMED:

                Log.v(TAG, "GameManager - changeState: STATE_LISTACTIVITY_RESUMED");

                if(curStates.contains(STATE_LISTACTIVITY_STARTED)){
                    mScanner.startScan();
                }

                if(curStates.contains(STATE_SERVER_CREATED)){
                    mBroadcaster.startBroadcast();
                }

                break;
            case STATE_LISTACTIVITY_PAUSED:

                Log.v(TAG, "GameManager - changeState: STATE_LISTACTIVITY_PAUSED");

                // если не был стартован, то и остановить не можем
                if(curStates.contains(STATE_LISTACTIVITY_STARTED)){
                    mScanner.stopScan();
                }

                if(curStates.contains(STATE_SERVER_CREATED)){
                    mBroadcaster.stopBroadcast();
                }

                break;
        }
    }


    /*************************************************************************************************/
    /*********************************** ОСНОВНЫЕ ОБЪЕКТЫ СИСТЕМЫ ************************************/
    /*************************************************************************************************/

    /**
     * здесь находятся
     * - объект, определяющий параметры игры
     * - объект для отрисовки 3D мира
     * - 3D мир (управление камерой)
     */

    private Instance mInstance;
    private GLView   mSurfaceView;
    private GLWorld  mWorld;

    GLSurfaceView getSurfaceView(){
        return mSurfaceView;
    }



    /*************************************************************************************************/
    /*********************************** УПРАВЛЕНИЕ ИГРОВЫМ ПРОЦЕССОМ ********************************/
    /*************************************************************************************************/

    /**
     * запускаем игру
     */

    void startGame(Context context){

        ConnectionChanel clientChanel;

        mInstance     = new Instance(context);
        clientChanel  = new ConnectionChanel(ConnectionChanel.TYPE_FOR_CLIENT);

        mInstance.setChanel(clientChanel);
        clientChanel.registerObserver(mInstance);

        mClient = mInstance;

        if(mServer != null){

            ClientAdapter adapter = new ClientAdapter(clientChanel, serverChanel);

            clientChanel.onSocketConnected(adapter.getClientSocket());
            serverChanel.onSocketConnected(adapter.getServerSocket());
        }else{

            // TODO: установить выбранное подключения для клиентского канала
        }



        mWorld = new GLWorld();
        mWorld.camMove(GLWorld.AXIS_Y, 3f);

        GLShader   mShader     = new GLShader(context, R.raw.shader_vertex, R.raw.shader_fragment);
        GLRenderer mRenderer   = new GLRenderer(mWorld, mInstance, mShader);

        mSurfaceView = new GLView(context, mWorld, mRenderer) {
            @Override
            protected void init() {

                mHandler = new MotionHandlerJoystick(mContext, mCamera);
            }

            private MotionHandlerJoystick mHandler;

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return mHandler.onHandleEvent(event);
            }
        };
    }


    /**
     * прекращаем игру
     */

    public void stopGame(){

    }

    /*************************************************************************************************/
    /*********************************** РАБОТА ПО СЕТИ В ЭТОМ КЛАССЕ ********************************/
    /*************************************************************************************************/

    /******************************** для взаимодействия Клиент - Сервер *****************************/

    private EndPoint mServer;

    private EndPoint mClient;



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


    public void createServer(Context context){

        Log.v(TAG, "GameManager: createServer()");

        Trace.beginSection("Server create (thread UI)");

        mBroadcaster = new ServiceBroadcaster(context);

        new Thread(new Runnable() {
            @Override
            public void run() {

                Trace.beginSection("Server create");

                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера - запущен");

                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера   ************** настройка сервера перед запуском ******************");

                // настройка серверной части
                Server server;

                server       = new Server();
                serverChanel = new ConnectionChanel(ConnectionChanel.TYPE_FOR_SERVER);

                serverChanel.getConnectionAppend().addConnection();

                server.setChanel(serverChanel);

                serverChanel.registerObserver(server);

                mServer = server;

                //Log.v(TAG, "GameManager: createServer(). Поток настройки сервера - установка и настройка адаптера для локального клиента");

                //ClientAdapter adapter = new ClientAdapter(clientChanel, serverChanel);

//                clientChanel.close();
//                clientChanel.clearObservers();
//
//                clientChanel.onSocketConnected(adapter.getClientSocket());
//                clientChanel.registerObserver(mClient);

                //serverChanel.onSocketConnected(adapter.getServerSocket());



                ListAdapter.ListItem item = new ListAdapter.ListItem();

                //item.mConnection  = adapter.getClientSocket();
                item.mCreator     = 123;
                item.mCreatorName = "Andrew";
                item.mResMap      = android.R.drawable.ic_lock_idle_lock;

                mAdapter.addItem(item);

                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера   ************** сервер создан ******************");

                // дадим ненмого времени на подключение сервера сокетов
                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера - ждем 5 сек");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера - идем дальше. настраиваем регистрацию сервиса.");

                // регистрация сервиса для автоподключения
                NsdServiceInfo info;

                info = new NsdServiceInfo();
                info.setServiceName(ServiceBroadcaster.mServiceName);
                info.setServiceType(ServiceBroadcaster.mServiceType);
                info.setPort(serverChanel.getPort());

                //Log.v(TAG, "GameManager: createServer(). Поток настройки сервера - включаем транслящию сервиса. port: " + String.valueOf(serverChanel.getPort()));

                mBroadcaster.setServiceInfo(info);
                mBroadcaster.startBroadcast();

                Trace.endSection();

            }
        }).start();

        Trace.endSection();

    }

    public void stopServer(){

        Log.v(TAG, "GameManager: stopServer(). закрываем подключения сервера");

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

                List<String> stack = new ArrayList<>();

                private final Object mStackLock = new Object();

                @Override
                public void put(String command) {

                    synchronized(mStackLock){
                        stack.add(command);
                    }
                }

                @Override
                public void send() {

                    if(stack.size() == 0)
                        return;

                    synchronized(mStackLock){

                        String command = stack.get(0);
                        mServerChanel.onIncommingCommandReceived(mServerSocket, command);
                        stack.remove(0);
                    }
                }

                @Override
                public void close() {
                    mClientChanel.onSocketDisconnected(mClientSocket);
                }
            };

            mServerSocket = new IConnection() {

                List<String> stack = new ArrayList<>();

                private final Object mStackLock = new Object();

                @Override
                public void put(String command) {

                    synchronized(((ConnectionChanel)mServerChanel).mStackLock){
                        stack.add(command);
                    }
                }

                @Override
                public void send() {

                    if(stack.size() == 0)
                        return;

                    synchronized(((ConnectionChanel)mServerChanel).mStackLock){

                        String command = stack.get(0);
                        mClientChanel.onIncommingCommandReceived(mClientSocket, command);
                        stack.remove(0);
                    }
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

        Log.v(TAG, "GameManager: someFunc(). произвольная функция инстанса");

        //mInstance.someFunc();
    }

    public void stopClient(){

        Log.v(TAG, "GameManager: stopClient(). остановка клиента");

        clientChanel.close();
        clientChanel.clearObservers();
    }

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }
}
