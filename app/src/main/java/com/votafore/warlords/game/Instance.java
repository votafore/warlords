package com.votafore.warlords.game;

import android.content.Context;
import android.util.Log;

import com.votafore.warlords.MeshUnit;
import com.votafore.warlords.glsupport.GLUnit;
import com.votafore.warlords.net.CMLocal;
import com.votafore.warlords.net.wifi.CMWifiForClient2;
import com.votafore.warlords.net.IClient;
import com.votafore.warlords.net.IConnection;
import com.votafore.warlords.net.IServer;
import com.votafore.warlords.net.wifi.SocketConnection;
import com.votafore.warlords.net.wifi.SocketConnection2;

import java.util.ArrayList;
import java.util.List;

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

public class Instance implements IClient, IServer{

    // виды соединения между игроками
    private static final int TYPE_WIFI      = 157;
    private static final int TYPE_BLUETOOTH = 846;
    private static final int TYPE_LOCAL     = 699;


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
//
//    /**
//     * служебные переменные
//     */
//
//    Context mContext;
//
//
//    public Instance(Context context, int creator){
//
//        mContext    = context;
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
//        createConnection(TYPE_WIFI);
//    }
//
//
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
//    //////////////////////////////////
//    // служебный раздел
//    //////////////////////////////////
//
//    private void createConnection(int type){
//
//        switch(type){
//            case TYPE_WIFI:
//
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
//
//                break;
//
//            case TYPE_BLUETOOTH:
//
//                // создаем и настраиваем объекты для работы по Bluetooth
//
//                break;
//            case TYPE_LOCAL:
//
//                // создаем и настраиваем объекты для работы без соединений
//                // т.е. на прямую, но через установленные интерфейсы
//
//                mServer = new CMLocal(this, this);
//                mServer.connect();
//        }
//    }

}