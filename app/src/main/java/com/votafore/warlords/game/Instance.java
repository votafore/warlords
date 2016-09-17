package com.votafore.warlords.game;

import android.content.Context;

import com.votafore.warlords.GameManager;
import com.votafore.warlords.glsupport.GLUnit;

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
     * служебные переменные
     */

    Context mContext;

    public Instance(Context context, int creator){

        mContext    = context;
        mGameID     = System.currentTimeMillis();
        mPlayerID   = creator;
    }

    public void setMap(GLUnit map){
        mMap = map;
        mMap.init();

        GameManager.getInstance(mContext).getWorld().attachObject(mMap);
    }

    public GLUnit getMap(){
        return mMap;
    }


    /**
     * команда для старта игры
     */
    public void startInstance(){

        GameManager manager = GameManager.getInstance(mContext);
    }
}
