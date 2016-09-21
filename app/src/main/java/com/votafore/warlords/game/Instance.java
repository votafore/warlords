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
