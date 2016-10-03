package com.votafore.warlords;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.opengl.GLSurfaceView;
import android.os.Trace;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.votafore.warlords.game.EndPoint;
import com.votafore.warlords.game.Instance;
import com.votafore.warlords.game.Server;
import com.votafore.warlords.glsupport.GLRenderer;
import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLView;
import com.votafore.warlords.glsupport.GLWorld;
import com.votafore.warlords.net.ConnectionChanel;
import com.votafore.warlords.net.SocketConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

        mWorld = new GLWorld(this);
        mWorld.camMove(GLWorld.AXIS_Y, 3f);

        Log.v(TAG, "GameManager: пытаемся получить NsdManager");
        mNsdManager           = (NsdManager) context.getSystemService(Context.NSD_SERVICE);

        mDiscoveryListener    = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.v(TAG, "NsdManager.DiscoveryListener: onStartDiscoveryFailed");
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.v(TAG, "NsdManager.DiscoveryListener: onStopDiscoveryFailed");
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.v(TAG, "NsdManager.DiscoveryListener: onDiscoveryStarted");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.v(TAG, "NsdManager.DiscoveryListener: onDiscoveryStopped");

                // как только прекратили искать (5 сек. ожидания)
                // посылаем запрос на информацию о созданных инстансах

                Log.v(TAG, "NsdManager.DiscoveryListener: рассылаем запросы");

                // при отправке запроса:
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Log.v(TAG, "NsdManager.DiscoveryListener: поток рассылки запросов - запущен");

                        // отправляем запрос на информацию об инстансе
                        try {

                            JSONObject query = new JSONObject();

                            query.put("clientID" , 0);
                            query.put("type"     , "InstanceInfo");
                            query.put("command"  , "get");

                            Log.v(TAG, "NsdManager.DiscoveryListener: поток рассылки запросов - отправляем запрос");

                            //mConnectionManager.sendMessage(query.toString());

                            clientChanel.sendCommand(query.toString());

                        } catch (JSONException e) {

                            Log.v(TAG, "NsdManager.DiscoveryListener: поток рассылки запросов, создание запроса: " + e.getMessage());

                            e.printStackTrace();
                        }

                        Log.v(TAG, "NsdManager.DiscoveryListener: поток рассылки запросов - ждем ответы (5 сек)");

                        // ждем ответы 5 сек
                        long end = System.currentTimeMillis() + 5 * 1000;
                        while(System.currentTimeMillis() < end){

                            try {
                                Thread.sleep(end - System.currentTimeMillis());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.v(TAG, "NsdManager.DiscoveryListener: поток рассылки запросов - закрываем все соединения");

                        // закрываем соединение(я)
                        //mConnectionManager.close();

                        clientChanel.close();

                    }
                }).start();
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {

                Log.v(TAG, "NsdManager.DiscoveryListener: onServiceFound. нашли сервис. пытаемся получить инфу для подключения");

                if(!serviceInfo.getServiceName().contains(mServiceName))
                    return;

                Log.v(TAG, "NsdManager.DiscoveryListener: onServiceFound. пытаемся подключиться к сервису");
                mNsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {
                    @Override
                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                        Log.v(TAG, "NsdManager.ResolveListener: onResolveFailed. не удалось подключиться к сервису");
                    }

                    @Override
                    public void onServiceResolved(NsdServiceInfo serviceInfo) {

                        Log.v(TAG, "NsdManager.ResolveListener: onServiceResolved. добавляем соединение");
                        Log.v(TAG, "хост: " + serviceInfo.getHost().toString());
                        Log.v(TAG, "порт: " + String.valueOf(serviceInfo.getPort()));

                        clientChanel.getConnectionAppend().addConnection(serviceInfo.getHost().toString(), serviceInfo.getPort());
                    }
                });
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.v(TAG, "NsdManager.DiscoveryListener: onServiceLost. закрываем соединения");

                // думаю что все подключения закрывать не обязательно
                //mConnectionManager.close();


                //mClientManager.close();

//                if(mServerManager != null)
//                    mServerManager.close();
            }
        };
        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.v(TAG, "NsdManager.RegistrationListener: onRegistrationFailed");

            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.v(TAG, "NsdManager.RegistrationListener: onUnregistrationFailed");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {

                // актуализация имени сервиса
                mServiceName = serviceInfo.getServiceName();

                Log.v(TAG, "NsdManager.RegistrationListener: onServiceRegistered!!! Service name - " + mServiceName);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.v(TAG, "NsdManager.RegistrationListener: onServiceUnregistered");
            }
        };




        mInstances  = new ArrayList<>();
        mAdapter    = new GameServerAdapter();


        //////////////////////////////////////////////////
        // настройка клиентской части
        //////////////////////////////////////////////////

        Log.v(TAG, "GameManager: ***************** настройка клиента ******************");

        Trace.beginSection("GameManager_setupClient");

        mInstance       = new Instance(context);

        clientChanel = new ConnectionChanel();
        clientChanel.setupAppend(ConnectionChanel.TYPE_FOR_CLIENT);

        mInstance.setChanel(clientChanel);

        final ConnectionChanel.IObserver observer;

        observer = new ConnectionChanel.IObserver() {
            @Override
            public void notifyObserver(SocketConnection connection, String message) {

                Log.v(TAG, "ConnectionChanel.IObserver: notifyObserver(). есть ответ от сервера");

                JSONObject response;

                try {
                    response = new JSONObject(message);

                    switch(response.getString("type")){
                        case "InstanceInfo":

                            Log.v(TAG, "ConnectionChanel.IObserver: notifyObserver(). это инфа о созданном инстансе");

                            InstanceContainer instanceInfo;

                            instanceInfo = new InstanceContainer();
                            instanceInfo.mResMap       = response.getInt("map");
                            instanceInfo.mCreator      = response.getInt("creatorID");
                            instanceInfo.mCreatorName  = response.getString("creatorName");

                            instanceInfo.mAddress      = connection.getHost();
                            instanceInfo.mPort         = connection.getPort();

                            mInstances.add(instanceInfo);
                            Log.v(TAG, "ConnectionChanel.IObserver: notifyObserver(). Добавили информацию об инстансе в список");

                            mAdapter.notifyItemInserted(mInstances.size()-1);

                            break;
                    }

                } catch (JSONException e) {
                    Log.v(TAG, "ConnectionChanel.IObserver: notifyObserver(). обработка ответа сервера. Ошибка: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
            }
        };

        clientChanel.registerObserver(observer);



        mAdapter.setListener(new ClickListener() {
            @Override
            public void onClick(int position) {

                InstanceContainer item = mInstances.get(position);

                clientChanel.getConnectionAppend().addConnection(item.mAddress, item.mPort);
                clientChanel.unregisterObserver(observer);

                clientChanel.registerObserver(mInstance);
            }
        });

        Log.v(TAG, "GameManager: ***************** настройка клиента завершена******************");

        mClient = mInstance;
        Trace.endSection();

    }


    public static String TAG = "TEST";


    /*************************************************************************************************/
    /*********************************** ОСНОВНЫЕ ОБЪЕКТЫ СИСТЕМЫ ************************************/
    /*************************************************************************************************/

    /**
     * должен быть объект, определяющий параметры игры
     */
    private Instance mInstance;

    /**
     * объект для отрисовки 3D мира
     * создаем заранее
     */
    private GLView mSurfaceView;

    /**
     * 3D мир
     * управление камерой
     */
    private GLWorld     mWorld;


    GLSurfaceView getSurfaceView(){
        return mSurfaceView;
    }




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


    /*************************************************************************************************/
    /*********************************** УПРАВЛЕНИЕ ИГРОВЫМ ПРОЦЕССОМ ********************************/
    /*************************************************************************************************/

    //??????????????

    /**
     * запускаем игру
     */

    void startGame(Context context){

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


    /**
     * объект для поиска\создания сервиса, позволяющего
     * автоматически создать сеть для игры между игроками
     */
    private NsdManager mNsdManager;




    /******************************* вспомогательные объекты и переменные ****************************/

    /**
     * слушатель для начала и остановки поиска сервиса, а так же
     * для реакции на событие нахождения сервиса.
     */
    private NsdManager.DiscoveryListener mDiscoveryListener;


    /**
     * слушатель для регистрации сервиса и отмены его регистрации
     */
    private NsdManager.RegistrationListener mRegistrationListener;


    /**
     * имя сервиса (или хотя бы как оно должно выглядеть)
     */
    public String mServiceName = "Warlords";


    /**
     * протокол - транспорт сервиса
     */
    public String mServiceType = "_http._tcp.";



    /******************************* создание собственного сервиса (и сервера) ***********************/

    /**
     * останавливаем трансляцию сервиса
     */
    public void stopBroadcastService(){

        Log.v(TAG, "GameManager: stopBroadcastService()");

        // отменяем регистрацию (трансляцию) сервиса в сети
        try {
            mNsdManager.unregisterService(mRegistrationListener);
        } catch (IllegalArgumentException e) {
            Log.v(TAG, "GameManager: stopBroadcastService(). Ошибка - " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * создаем сервер
     *
     * запускаем наш сервис в мир
     * что бы его увидели и могли подключиться
     */
    public void createServer(){

        Log.v(TAG, "GameManager: createServer()");

        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера - запущен");

                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера   ************** настройка сервера перед запуском ******************");

                // настройка серверной части
                Server server;
                server          = new Server();

                serverChanel = new ConnectionChanel();
                serverChanel.setupAppend(ConnectionChanel.TYPE_FOR_SERVER);
                serverChanel.getConnectionAppend().addConnection();

                server.setChanel(serverChanel);

                serverChanel.registerObserver(server);

                mServer = server;

                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера   ************** сервер создан ******************");

                // дадим ненмого времени на подключение сервера сокетов
                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера - ждем 1 секунду");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера - идем дальше. настраиваем регистрацию сервиса.");

                // регистрация сервиса для автоподключения
                NsdServiceInfo info;

                info = new NsdServiceInfo();
                info.setServiceName(mServiceName);
                info.setServiceType(mServiceType);
                info.setPort(serverChanel.getPort());

                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера - включаем транслящию сервиса. port: " + String.valueOf(serverChanel.getPort()));

                mNsdManager.registerService(info, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
            }
        }).start();

    }

    public void stopServer(){

        Log.v(TAG, "GameManager: stopServer(). закрываем подключения сервера");

        if(serverChanel == null)
            return;

        serverChanel.close();
        serverChanel.clearObservers();
    }


    /********************************** поиск созданных сервисов *************************************/


    /**
     * запускаем поиск игр (сервисов для подключения)
     * созданных другими игроками
     */
    public void discoverServers(Context context){

        Log.v(TAG, "GameManager: discoverServers().");

        mInstances = new ArrayList<>();
        mAdapter.notifyDataSetChanged();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v(TAG, "GameManager: discoverServers(). Поток поиска сервисов (серверов) - запущен. Начинаем поиск");

                // начинаем поиск сервисов
                mNsdManager.discoverServices(mServiceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

                long end = System.currentTimeMillis() + 15 * 1000;

                while(System.currentTimeMillis() < end){

                    Log.v(TAG, "GameManager: discoverServers(). Поток поиска сервисов (серверов) - ждем 15 сек");

                    try {
                        Thread.sleep(end - System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Log.v(TAG, "GameManager: discoverServers(). Поток поиска сервисов (серверов) - останавливаем поиск");
                // останавливаем поиск сервисов
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            }
        }).start();
    }


    /***************************** отображение найденных игр (серверов) ******************************/

    /**
     * список созданных игр (найденных)
     */
    private List<InstanceContainer> mInstances;


    /**
     * контейнер, для хранения информации об инстансе
     */
    private class InstanceContainer{

        public String       mAddress;
        public int          mPort;


        public int          mResMap;
        public int          mCreator;
        public String       mCreatorName;
    }



    private GameServerAdapter mAdapter;

    GameServerAdapter getAdapter(){
        return mAdapter;
    }

    interface ClickListener{
        void onClick(int position);
    }

    class GameServerAdapter extends RecyclerView.Adapter<GameServerAdapter.Holder>{

        ClickListener mListener;

        public void setListener(ClickListener listener){
            mListener = listener;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = View.inflate(parent.getContext(), R.layout.item_found_game, null);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {

            InstanceContainer item = mInstances.get(position);

            holder.mImageView.setImageResource(item.mResMap);
            holder.mOwnerName.setText(item.mCreatorName);
            holder.mPlayerCount.setText("undefined");
        }

        @Override
        public int getItemCount() {

            return mInstances.size();
        }

        public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{

            public ImageView    mImageView;
            public TextView     mOwnerName;
            public TextView     mPlayerCount;

            public Holder(View itemView) {
                super(itemView);

                mImageView      = (ImageView) itemView.findViewById(R.id.map_thumbnail);
                mOwnerName      = (TextView) itemView.findViewById(R.id.owner_name);
                mPlayerCount    = (TextView) itemView.findViewById(R.id.player_count);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                mListener.onClick(getAdapterPosition());
            }
        }
    }











    /************************************** раздел еще в разработке **********************************/


    public void someFunc(){

        Log.v(TAG, "GameManager: someFunc(). произвольная функция инстанса");

        mInstance.someFunc();
    }

    public void stopClient(){

        Log.v(TAG, "GameManager: stopClient(). остановка клиента");

        clientChanel.close();
        clientChanel.clearObservers();
    }

}
