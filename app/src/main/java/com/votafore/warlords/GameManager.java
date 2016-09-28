package com.votafore.warlords;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.votafore.warlords.game.Instance;
import com.votafore.warlords.game.Server;
import com.votafore.warlords.glsupport.GLRenderer;
import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLView;
import com.votafore.warlords.glsupport.GLWorld;
import com.votafore.warlords.net.IClient;
import com.votafore.warlords.net.IServer;
import com.votafore.warlords.net.ISocketListener;
import com.votafore.warlords.net.wifi.CMWifiClient;
import com.votafore.warlords.net.wifi.CMWifiServer;
import com.votafore.warlords.net.wifi.SocketConnection;
import com.votafore.warlords.test.TestConnectionManager;

import junit.framework.Test;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Votafore
 * Created on 17.09.2016.
 */
public class GameManager {//extends BroadcastReceiver{

    private static volatile GameManager mThis;

    static GameManager getInstance(Context context){

        if(mThis == null)
            mThis = new GameManager(context);

        return mThis;
    }

    private GameManager(Context context){

        ISocketListener mCustomListener;

        mWorld = new GLWorld(this);
        mWorld.camMove(GLWorld.AXIS_Y, 3f);

        mNsdManager           = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        mDiscoveryListener    = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.v(TAG, "onStartDiscoveryFailed");
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.v(TAG, "onStopDiscoveryFailed");
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.v(TAG, "onDiscoveryStarted");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.v(TAG, "onDiscoveryStopped");

                // как только прекратили искать (5 сек. ожидания)
                // посылаем запрос на информацию о созданных инстансах

                Log.v(TAG, "поиск остановлен, рассылаем запросы");

                // при отправке запроса:
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Log.v(TAG, "поток рассылки запросов запущен");

                        // отправляем запрос на информацию об инстансе
                        try {

                            JSONObject query = new JSONObject();

                            query.put("clientID" , 0);
                            query.put("type"     , "InstanceInfo");
                            query.put("command"  , "get");

                            mConnectionManager.sendMessage(query.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.v(TAG, "ждем ответы (5 сек)");

                        // ждем ответы 5 сек
                        long end = System.currentTimeMillis() + 5 * 1000;
                        while(System.currentTimeMillis() < end){

                            try {
                                Thread.sleep(end - System.currentTimeMillis());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.v(TAG, "закрываем все соединения");
                        // закрываем соединение(я)
                        mConnectionManager.closeAll();
                    }
                }).start();
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {

                if(!serviceInfo.getServiceName().contains(mServiceName))
                    return;

                mNsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {
                    @Override
                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                        Log.v(TAG, "onResolveFailed");
                    }

                    @Override
                    public void onServiceResolved(NsdServiceInfo serviceInfo) {
                        mConnectionManager.addConnection(serviceInfo.getHost(), serviceInfo.getPort());
                    }
                });
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.v(TAG, "onServiceLost");
            }
        };
        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {

                // актуализация имени сервиса
                mServiceName = serviceInfo.getServiceName();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

            }
        };
        mCustomListener       = new ISocketListener() {
            @Override
            public void onObtainMessage(SocketConnection connection, String msg) {

                Log.v(TAG, "получили ответ");

                JSONObject response;

                try {
                    response = new JSONObject(msg);

                    switch(response.getString("type")){
                        case "InstanceInfo":

                            InstanceContainer instanceInfo;

                            instanceInfo = new InstanceContainer();
                            instanceInfo.mResMap       = response.getInt("map");
                            instanceInfo.mCreator      = response.getInt("creatorID");
                            instanceInfo.mCreatorName  = response.getString("creatorName");

                            instanceInfo.mAddress      = connection.getHost();
                            instanceInfo.mPort         = connection.getPort();

                            mInstances.add(instanceInfo);

                            mAdapter.notifyItemInserted(mInstances.size()-1);

                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }

            @Override
            public void onSocketConnected(SocketConnection connection) {

            }

            @Override
            public void onSocketDisconnected(SocketConnection connection) {

            }
        };

        mConnectionManager.setListener(mCustomListener);

        mInstances  = new ArrayList<>();
        mAdapter    = new GameServerAdapter();



        //////////////////////////////////////////////////
        // настройка клиентской части
        //////////////////////////////////////////////////

        mInstance   = new Instance(context);
        mClient     = mInstance;



        final TestConnectionManager mConnectionManagerForCustomServer;

        mConnectionManagerForCustomServer = new TestConnectionManager();

        mAdapter.setListener(new ClickListener() {
            @Override
            public void onClick(int position) {

                InstanceContainer item = mInstances.get(position);

                mConnectionManagerForCustomServer.closeAll();
                mConnectionManagerForCustomServer.addConnection(item.mAddress, item.mPort);
            }
        });


        CMWifiServer customServer;

        customServer = new CMWifiServer(mClient);
        customServer.setConnectionManager(mConnectionManagerForCustomServer);

        mCustomServer = customServer;

        mInstance.setServer(mCustomServer);
    }



    private String TAG = "GAMESERVICE";


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

    /**
     * Строим систему следующим образом:
     *      1) Создаем объект клиента (он будет в любом случае). У него будет свой
     *      объект сервера (будет ли это реальный сервер или сокеты - вопрос другой)
     *
     *      2) создаем сервер (в случае необходимости). У него будет свой объект
     *      клиента (реальный клиент или сокеты - разберемся по ходу)
     *
     * Таким образом получаем что необходимо 4 объекта.
     * Они ниже
     */

    private IServer mServer;
    private IClient mCustomClient;


    private IClient mClient;
    private IServer mCustomServer;


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
    private String mServiceName = "Warlords";


    /**
     * протокол - транспорт сервиса
     */
    private String mServiceType = "_http._tcp.";



    /******************************* создание собственного сервиса (и сервера) ***********************/


    /**
     * запускаем наш сервис в мир
     * что бы его увидели и могли подключиться
     */
    public void startBroadcastService(){

//        // создаем сервер подключений (сокетов)
//        CMWifiClient client = new CMWifiClient(null);
//        mCustomClient = client;

        // создаем сервис для автоматического подключения пользователей
        NsdServiceInfo info;

        info = new NsdServiceInfo();
        info.setServiceName(mServiceName);
        info.setServiceType(mServiceType);
        //info.setPort(client.mServerSocket.getLocalPort());

        mNsdManager.registerService(info, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }


    /**
     * останавливаем сервис
     */
    public void stopBroadcastService(){

        // отменяем регистрацию (трансляцию) сервиса в сети
        mNsdManager.unregisterService(mRegistrationListener);
    }


    /**
     * создаем сервер
     */
    public void createServer(){

        Server server = new Server();

        mServer = server;
    }


    /********************************** поиск созданных сервисов *************************************/


    /**
     * запускаем поиск игр (сервисов для подключения)
     * созданных другими игроками
     */
    public void discoverServers(Context context){

        Log.v(TAG, "запускаем поиск серверов (вызов функции)");

        mInstances = new ArrayList<>();
        mAdapter.notifyDataSetChanged();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.v(TAG, "запускаем поиск серверов (запуск потока)");
                // начинаем поиск сервисов
                mNsdManager.discoverServices(mServiceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

                long end = System.currentTimeMillis() + 5 * 1000;

                while(System.currentTimeMillis() < end){

                    Log.v(TAG, "ждем 5 сек");

                    try {
                        Thread.sleep(end - System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Log.v(TAG, "останавливаем поиск");
                // останавливаем поиск сервисов
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            }
        }).start();
    }


    /**
     * контейнер подключений
     */
    private TestConnectionManager mConnectionManager = new TestConnectionManager();






    /***************************** отображение найденных игр (серверов) ******************************/

    /**
     * список созданных игр (найденных)
     */
    private List<InstanceContainer> mInstances;


    /**
     * контейнер, для хранения информации об инстансе
     */
    private class InstanceContainer{

        public InetAddress  mAddress;
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



}
