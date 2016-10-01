package com.votafore.warlords.test;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.votafore.warlords.GameManager;
import com.votafore.warlords.game.EndPoint;
import com.votafore.warlords.test2.ISocketListener;
import com.votafore.warlords.test2.IConnection;
import com.votafore.warlords.test2.SocketConnectionAdder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Votafore
 * Created on 26.09.2016.
 */

public class ConnectionManager2 implements ISocketListener{

    public List<IConnection>    mConnections;
    private EndPoint            mClient;

    public ConnectionManager2(EndPoint client){

        mConnections    = new ArrayList<>();
        mClient         = client;

        Log.v(GameManager.TAG, "ConnectionManager2:");
    }

    /*****************************************************************************/
    /***************** пока не интерфейс, но возможно им будет *******************/
    /*****************************************************************************/


    public void sendCommand(String command){

        Log.v(GameManager.TAG, "ConnectionManager2: sendCommand(). Отправка команды");

        for (IConnection connection : mConnections) {
            connection.sendCommand(command);
            Log.v(GameManager.TAG, "ConnectionManager2: sendCommand(). Отправка команды - отправлена сокету");
        }
    }

    public void close(){

        Log.v(GameManager.TAG, "ConnectionManager2: close(). закрытие сокетов");

        while(mConnections.size() > 0){

            mConnections.get(0).close();
            mConnections.remove(0);

            Log.v(GameManager.TAG, "ConnectionManager2: close(). закрытие сокетов - 1 закрыт");
        }

        if(mAdder != null)
            mAdder.stop();
    }




    public void store(IConnection connection){

        Log.v(GameManager.TAG, "ConnectionManager2: store(). Получили новое подключение - сохраняем");

        mConnections.remove(connection);
        mConnections.add(connection);
    }

    private SocketConnectionAdder mAdder;

    public void setAdder(SocketConnectionAdder adder){
        mAdder = adder;
        Log.v(GameManager.TAG, "ConnectionManager2: setAdder(). добавлен");
    }

    /*****************************************************************************/
    /****************************** ISocketListener ******************************/
    /*****************************************************************************/

    @Override
    public void onIncommingCommandReceived(IConnection connection, String msg) {
        mClient.execute(msg);
        Log.v(GameManager.TAG, "ConnectionManager2: onIncommingCommandReceived()");
    }


    @Override
    public void onSocketDisconnected(IConnection connection) {
        mConnections.remove(connection);
        Log.v(GameManager.TAG, "ConnectionManager2: onSocketDisconnected()");
    }
}
