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
import com.votafore.warlords.net.IConnection;
import com.votafore.warlords.net.ISocketListener;
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


        //mInstances  = new ArrayList<>();
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

        Log.v(TAG, "GameManager: ***************** настройка клиента завершена******************");

        mClient = mInstance;
        Trace.endSection();

    }


    public static String TAG = "TEST";


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
     * слушатель для регистрации сервиса и отмены его регистрации
     */
    private NsdManager.RegistrationListener mRegistrationListener;


    /**
     * имя сервиса (или хотя бы как оно должно выглядеть)
     */
    public static String mServiceName = "Warlords";


    /**
     * протокол - транспорт сервиса
     */
    public static String mServiceType = "_http._tcp.";



    /******************************* создание собственного сервиса (и сервера) ***********************/

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

    public void createServer(){

        Log.v(TAG, "GameManager: createServer()");

        Trace.beginSection("Server create (thread UI)");

        new Thread(new Runnable() {
            @Override
            public void run() {

                Trace.beginSection("Server create");

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

                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера - установка и настройка адаптера для локального клиента");

                ClientAdapter adapter = new ClientAdapter(clientChanel, serverChanel);

                clientChanel.close();
                clientChanel.clearObservers();

                clientChanel.onSocketConnected(adapter.getClientSocket());
                clientChanel.registerObserver(mClient);

                serverChanel.onSocketConnected(adapter.getServerSocket());

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
                info.setServiceName(mServiceName);
                info.setServiceType(mServiceType);
                info.setPort(serverChanel.getPort());

                Log.v(TAG, "GameManager: createServer(). Поток настройки сервера - включаем транслящию сервиса. port: " + String.valueOf(serverChanel.getPort()));

                mNsdManager.registerService(info, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

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

                @Override
                public void put(String command) {

                    synchronized(((ConnectionChanel)mClientChanel).mStackLock){
                        stack.add(command);
                    }
                }

                @Override
                public void send() {

                    if(stack.size() == 0)
                        return;

                    String command = stack.get(0);

                    mServerChanel.onIncommingCommandReceived(mServerSocket, command);

                    stack.remove(0);
                }

                @Override
                public void close() {
                    mClientChanel.onSocketDisconnected(mClientSocket);
                }
            };

            mServerSocket = new IConnection() {

                List<String> stack = new ArrayList<>();

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

                    String command = stack.get(0);

                    mClientChanel.onIncommingCommandReceived(mClientSocket, command);

                    stack.remove(0);
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

    //private List<InstanceContainer> mInstances;

    public class InstanceContainer{

        public String       mAddress;
        public int          mPort;


        public int          mResMap;
        public int          mCreator;
        public String       mCreatorName;
    }



    private GameServerAdapter mAdapter;

    public GameServerAdapter getAdapter(){
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

//            InstanceContainer item = mInstances.get(position);
//
//            holder.mImageView.setImageResource(item.mResMap);
//            holder.mOwnerName.setText(item.mCreatorName);
//            holder.mPlayerCount.setText("undefined");
        }

        @Override
        public int getItemCount() {

            return 0;//mInstances.size();
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
