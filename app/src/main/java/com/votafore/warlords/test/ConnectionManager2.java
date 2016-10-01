package com.votafore.warlords.test;

import android.os.Trace;
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

        Trace.beginSection("ConnectionManager2");

        mConnections    = new ArrayList<>();
        mClient         = client;

        Log.v(GameManager.TAG, "ConnectionManager2:");

        Trace.endSection();
    }


    /*****************************************************************************/
    /********************************* IChanel ***********************************/
    /*****************************************************************************/


    public void sendCommand(String command){

        Log.v(GameManager.TAG, "ConnectionManager2: sendCommand(). Отправка команды");

        for (IConnection connection : mConnections) {
            Log.v(GameManager.TAG, "ConnectionManager2: sendCommand(). Отправка команды - отправлена сокету");
            connection.sendCommand(command);
        }
    }


    public void close(){

        Log.v(GameManager.TAG, "ConnectionManager2: close(). закрытие сокетов");

        while(mConnections.size() > 0){

            try {
                mConnections.get(0).close();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            Log.v(GameManager.TAG, "ConnectionManager2: close(). закрытие сокетов - 1 закрыт");

            // TODO: найдена проблема
            // из-за того что удаление из списка происходит в другой процедуре (обратный вызов)
            // происходит несколько попыток закрыть один и тот же сокет
        }

        if(mAdder != null)
            mAdder.stop();
    }




    /*****************************************************************************/
    /***************** пока не интерфейс, но возможно им будет *******************/
    /*****************************************************************************/

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
        Log.v(GameManager.TAG, "ConnectionManager2: onIncommingCommandReceived()");
        mClient.execute(msg);
    }


    @Override
    public void onSocketDisconnected(IConnection connection) {
        Log.v(GameManager.TAG, "ConnectionManager2: onSocketDisconnected()");
        mConnections.remove(connection);
    }




    /*****************************************************************************/
    /****************************** раздел в разработке **************************/



    /****************************** реализация наблюдателей  **********************/

//    private List<IChanelObserver> mObservers;
//
//    public void registerObserver(IChanelObserver observer){
//        mObservers.add(observer);
//    }
//
//    public void unregisterObserver(IChanelObserver observer){
//        mObservers.remove(observer);
//    }
}
