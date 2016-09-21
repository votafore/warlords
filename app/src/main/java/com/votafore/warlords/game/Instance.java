package com.votafore.warlords.game;

import android.content.Context;

import com.votafore.warlords.GameManager;
import com.votafore.warlords.MeshUnit;
import com.votafore.warlords.glsupport.GLUnit;

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
 * - иметь информацию об игроках инстанса (пока не известно в каком виде)
 * - иметь информацию об объектах инстанса (что построено\создано игроками) для отрисовки
 * - иметь механизм изменения параметров объектов сцены
 *      управляемый другими игроками (при многопользовательской игре)
 *      управляемый текущим игроком
 */

public class Instance {

    /**
     * типа ID игры (боя)
     */
    private long mGameID;

    /**
     * ID игрока, создавшего инстанс
     */
    private int mPlayerID;





    /**
     * карта выбранная для текущего боя
     */
    private GLUnit mMap;


    /**
     * здание командного центра
     */
    public GLUnit mBase;

    /**
     * служебные переменные
     */

    Context mContext;

    public Instance(Context context, int creator){

        mContext    = context;
        mGameID     = System.currentTimeMillis();
        mPlayerID   = creator;
        mPlayers    = new ArrayList<>();
        mObjects    = new ArrayList<>();

        mBase       = new MeshUnit(mContext);
        mBase.init();
    }

    public void setMap(GLUnit map){
        mMap = map;
        mMap.init();

        GameManager.getInstance(mContext).getWorld().attachObject(mMap);
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
}
