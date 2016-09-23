package com.votafore.warlords.game;

import android.content.Context;

import com.votafore.warlords.net.IClient2;
import com.votafore.warlords.net.IServer2;
import com.votafore.warlords.net.wifi.CMWifiClient;
import com.votafore.warlords.net.wifi.CMWifiServer;
import com.votafore.warlords.net.ISocketCallback;

/**
 * @author Votafore
 * Created on 17.09.2016.
 *
 * Instance создается специальной для этого командой (кнопкой)
 * если игрок не нашел для себя подходящей игры (из существующих.... уже созданных другими игроками)
 *
 *
 * Идея класса.
 * он должен:
 * - делиться на клиентскую и серверную части.
 *      - клиентская (часть игрока). Отображение объектов, возможность изменения
 *              параметров\состояния своих объектов (управление).
 *      - серверная (часть программы). Синхронизирует изменения объектов между
 *              клиентами. Другими словами хранит состояние объектов и дает доступ
 *              к этой информации (рассылает).
 *
 *      Клиент (каждый клиент) хранит полный список объектов и всю информацию по каждому объекту
 *      т.к. это необходимо для отрисовки объекта(ов) на клиенте.
 *      Сервер содержит весь список объектов и измененные параметры объектов для
 *      передачи остальным клиентам (синхронизации).
 *      Т.е. списки отличаются содержанием информации.
 *
 * - иметь информацию об игроках инстанса (пока не известно в каком виде)
 *      уже начинаю думать что это не обязательно (пока достаточно только списка объектов)
 * - иметь информацию об объектах инстанса
 *      - для отрисовки (что построено\создано игроками)
 *      - чьи это объекты
 * - иметь механизм изменения параметров объектов сцены
 *      управляемый другими игроками (при многопользовательской игре)
 *      управляемый текущим игроком
 *
 * - реагировать на события управления (команды, onTouch и т.д.)
 *
 * Именно этот класс будет основным в треьтем Активити. GameManager передаст
 * его и в процессе игры (далее) будет учавствовать только этот он.
 */

public class Instance implements IClient2, IServer2 {

    // виды соединения между игроками
    private static final int TYPE_WIFI      = 157;
    private static final int TYPE_BLUETOOTH = 846;
    private static final int TYPE_LOCAL     = 699;



    /**
     * служебные переменные
     */
    Context mContext;


    public Instance(Context context, int creator){

        mContext    = context;
//        mGameID     = System.currentTimeMillis();
//        mCreatorID  = creator;
//        mPlayers    = new ArrayList<>();
//        mObjects    = new ArrayList<>();
//
//        mBase       = new MeshUnit(mContext);
//        mBase.init();
//
//        // это сервер - если ид текущего игрока и ид создалеля игры одинаковые.
//        mPlayerID = creator; // потом заменять на реального игрока
//
//        boolean isServer = mPlayerID == mCreatorID;
//
        createConnection(TYPE_WIFI);

    }



    /*****************************************************************************************************************/
    /*********************************************** РАЗДЕЛ РАБОТЫ ПО СЕТИ (ИЛИ ЛОКАЛЬНО) ****************************/
    /*****************************************************************************************************************/

    //////////////////////////////////////
    // КЛИЕНТСКАЯ ЧАСТЬ
    //////////////////////////////////////

    /**
     * что бы клиент мог отправлять сообщения серверу
     * ему нужен соответствующий объект (представитель... имитатор) сервера
     */

    private IServer2 mServer;


    /**
     * клиент может отправлять сообщения серверу
     * точнее команды
     */

    public void someClientFunc(){

        // создаем описание команды
        String cmd = "create_object";

        // отправляем команду на сервер
        mServer.handleCommand(cmd);
    }

    /**
     * клиент может получать сообщения от сервера (асинхронные... т.е. из другого потока)
     */

    @Override
    public void onMessageReceived(String msg){

        // TODO: обработка сообщения сервера
    }




    //////////////////////////////////////
    // ЧАСТЬ ЛОКАЛЬНОГО КЛИЕНТА
    // это когда сервер на этом же девайсе
    // ведь тогда не надо работать по сети
    //////////////////////////////////////

    /**
     * он ведет себя как сокет, который отправляет полученное сообщение
     * серверной части Connection Manager Wi-Fi
     * для этого нужен ISocketListener - объект, которому
     * сокет шлет полученные сообщения
     */

    private ISocketCallback mSocketListener;

    /**
     * он получает сообщения как обычный клиент
     * метод для этого уже есть в разделе КЛИЕНТ
     */

    /**
     * он может отправлять сообщения
     * имитируя работу по сети (имитируя работу сокета)
     * хотя на самом деле это локально
     */

    public void someLocalClientFunc(){

        // создаем описание команды
        String cmd = "create_object";

        // отправляем команду на сервер
        mSocketListener.onObtainMessage(cmd);
    }







    //////////////////////////////////////
    // СЕРВЕРНАЯ ЧАСТЬ
    //////////////////////////////////////

    /**
     * что бы сервер мог отправлять ответ (сообщения) клиенту
     * ему нужен соответствющий объект (имитатор)
     */

    private IClient2 mClient;


    /**
     * сервер может отправлять сообщения клиенту
     */

    public void someServerFunc(){

        // формируем ответ клиенту
        String response = "server response";

        mClient.onMessageReceived(response);
    }

    /**
     * сервер должен уметь обрабатывать команды клиента
     */

    @Override
    public void handleCommand(String command){

        // TODO: обработка команды клиента
    }




//    /**
//     * типа ID игры (боя)
//     */
//    private long mGameID;
//
//    /**
//     * ID текущего игрока
//     */
//    private int mPlayerID;
//
//    /**
//     * ID игрока, создавшего инстанс
//     */
//    private int mCreatorID;
//
//
//
//    /**
//     * здание командного центра
//     */
//    public GLUnit mBase;







//    /**
//     * карта выбранная для текущего боя
//     */
//    private GLUnit mMap;
//
//    public void setMap(GLUnit map){
//        mMap = map;
//        mMap.init();
//    }
//
//    public GLUnit getMap(){
//        return mMap;
//    }
//
//
//
//
//
//
//    List<Player> mPlayers;
//
//    public void addPlayer(Player player){
//
//        // на случай, если такой уже есть (правда не понятно как такое может получиться)
//        mPlayers.remove(player);
//
//        mPlayers.add(player);
//    }
//
//
//
//
//    List<GLUnit> mObjects;
//
//    public List<GLUnit> getObjects(){
//        return mObjects;
//    }
//
//
//
//
//
//
//
//
//
//
//
//    //////////////////////////////////
//    // клиентская часть
//    //////////////////////////////////
//
//    /**
//     * клиентская часть (отправляющая запросы)
//     */
//    private IConnection mClientConnectivity;
//
//
//
//
//
//    /**
//     * клиентская часть (имитирующая работу сервера)
//     * (отправляющая запросы)
//     */
//
//    private IServer mServer;
//
//    // получение сообщений от сервера
//    @Override
//    public void onMessageReceive(int ID) {
//        Log.v("MSOCKET", "onServerMessageReceive: получено сообщение от сервера");
//    }
//
//    @Override
//    public void onConnectionChange(boolean connected) {
//
//    }
//
//
//
//
//
//    private SocketConnection2.IConnectionListener mConnectionListener;
//
//
//
//
//
//
//
//    //////////////////////////////////
//    // серверная часть
//    //////////////////////////////////
//
//    /**
//     * серверная часть (принимающая управление объектами и синхр. остальные)
//     */
//    private IConnection mServerConnectivity;
//
//
//
//
//
//
//    private List<ObjectServerInfo> mObjectServerInfoList;
//
//    private class ObjectServerInfo{
//
//        private int mLastID; // IDы всех объектов (инкрементируется при создании объектов)
//
//        int      ID;      // ID объекта
//        int      ownerID; // ID игрока
//        //public GLUnit   object;  // думаю тут все понятно
//    }
//
//
//
//    @Override
//    public void addUnit(){
//
//        ObjectServerInfo newObj;
//
//        newObj          = new ObjectServerInfo();
//        newObj.ID       = 0;
//        newObj.ownerID  = 0;
//
//        if(mObjectServerInfoList.size() > 0)
//            // увеличиваем значение ID из последнего объекта
//            newObj.ID = mObjectServerInfoList.get(mObjectServerInfoList.size()-1).ID+1;
//
//        mObjectServerInfoList.add(newObj);
//    }
//
//    @Override
//    public void connect() {
//
//    }
//
//    @Override
//    public void disconnect() {
//
//    }
//
//
//
//
//
    //////////////////////////////////
    // служебный раздел
    //////////////////////////////////

    private void createConnection(int type){

        switch(type){
            case TYPE_WIFI:

                boolean isServer = false;

                if(isServer){

                    CMWifiClient client = new CMWifiClient(this);

                    mClient             = client;
                    mSocketListener     = client;

                }else{

                    mServer = new CMWifiServer(this);
                }



//                if(mPlayerID == mCreatorID){
//
//                    // игра создана на текущем девайсе
//                    // он будет сервером
//
//                    CMWifiForClient2 conn;
//
//                    conn = new CMWifiForClient2(this);
//                    conn.connect();
//
//                    mServer = conn;
//                }else{
//
//                    // создаем подключение клиента
//
//                    CMWifiForClient2 conn;
//
//                    conn = new CMWifiForClient2(this);
//                    conn.connect();
//
//                    mServer = conn;
//                }

                break;

            case TYPE_BLUETOOTH:

                // создаем и настраиваем объекты для работы по Bluetooth

                break;
            case TYPE_LOCAL:

                // создаем и настраиваем объекты для работы без соединений
                // т.е. на прямую, но через установленные интерфейсы

//                mServer = new CMLocal(this, this);
//                mServer.connect();
        }
    }

}