package com.votafore.warlords.game;

import android.content.Context;
import android.util.Log;

import com.votafore.warlords.MeshUnit;
import com.votafore.warlords.glsupport.GLUnit;
import com.votafore.warlords.net.CMWifiForClient;
import com.votafore.warlords.net.IClient;
import com.votafore.warlords.net.IConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Votafore
 * Created on 17.09.2016.
 *
 * Instance создается специальной для этого командой (кнопкой)
 * если игрок не нашел для себя подходящей игры (из существующих.... уже созданных другими игроками)
 */

/**
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
 * - иметь информацию об объектах инстанса
 *      - для отрисовки (что построено\создано игроками)
 *      - чьи это объекты
 * - иметь механизм изменения параметров объектов сцены
 *      управляемый другими игроками (при многопользовательской игре)
 *      управляемый текущим игроком
 *
 * Именно этот класс будет основным в треьтем Активити. GameManager передаст
 * его и в процессе игры (далее) будет учавствовать только этот он.
 */

public class Instance implements IClient{

    /**
     * типа ID игры (боя)
     */
    private long mGameID;

    /**
     * ID текущего игрока
     */
    private int mPlayerID;

    /**
     * ID игрока, создавшего инстанс
     */
    private int mCreatorID;



    /**
     * здание командного центра
     */
    public GLUnit mBase;

    /**
     * служебные переменные
     */

    Context mContext;


    /**
     * серверная часть (принимающая управление объектами и синхр. остальные)
     */
    private IConnection mServerConnectivity;

    /**
     * клиентская часть (отправляющая запросы)
     */
    private IConnection mClientConnectivity;

    public Instance(Context context, int creator){

        mContext    = context;
        mGameID     = System.currentTimeMillis();
        mCreatorID  = creator;
        mPlayers    = new ArrayList<>();
        mObjects    = new ArrayList<>();

        mBase       = new MeshUnit(mContext);
        mBase.init();

        // это сервер - если ид текущего игрока и ид создалеля игры одинаковые.
        mPlayerID = creator; // потом заменять на реального игрока

        boolean isServer = mPlayerID == mCreatorID;

        mClientConnectivity = new CMWifiForClient(this);
        mClientConnectivity.init();
    }


    public void stopGame(){
        mClientConnectivity.release();
    }


    /**
     * карта выбранная для текущего боя
     */
    private GLUnit mMap;

    public void setMap(GLUnit map){
        mMap = map;
        mMap.init();
    }

    public GLUnit getMap(){
        return mMap;
    }






    List<Player> mPlayers;

    public void addPlayer(Player player){

        // на случай, если такой уже есть (правда не понятно как такое может получиться)
        mPlayers.remove(player);

        mPlayers.add(player);
    }




    List<GLUnit> mObjects;

    public List<GLUnit> getObjects(){
        return mObjects;
    }






    @Override
    public void onMessageReceive(int ID) {
        Log.v("MSOCKET", "onServerMessageReceive: получено сообщение от сервера");
    }

}
