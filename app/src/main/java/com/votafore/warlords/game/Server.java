package com.votafore.warlords.game;


import android.util.Log;

import com.votafore.warlords.net.ConnectionChanel;
import com.votafore.warlords.net.IConnection;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Votafore
 * Created on 28.09.2016.
 *
 * серверная часть игры
 */

public class Server extends EndPoint{


    public Server(){
        super();
    }


    /*****************************************************************************************************************/
    /*********************************************** ОБЯЗАННОСТИ СЕРВЕРА ;) ******************************************/
    /*****************************************************************************************************************/

    @Override
    public void stop(){

        mWorkerThread.quitSafely();

        ((ConnectionChanel)mChanel).clearObservers();
        ((ConnectionChanel)mChanel).close();
    }

    @Override
    public void execute(IConnection connection, String command) {

        //Log.v(GameFactory.TAG, "Server: execute() сервер принял команду. Готовим ответ");

        if(command.isEmpty())
            return;

        JSONObject cmd;

        try {
            cmd = new JSONObject(command);

            JSONObject response = new JSONObject();

            switch (cmd.getString("type")){
                case "InstanceInfo":

                    if(cmd.getString("command").equals("get")){

                        response.put("type"         , "InstanceInfo");
                        response.put("map"          , android.R.drawable.btn_star);
                        response.put("creatorID"    , 125);
                        response.put("creatorName"  , "Andrew");
                    }

                    //Log.v(GameFactory.TAG, "Server: execute() сервер принял команду. Готовим ответ. отправляем");

                    // на некоторые типы запросов ответ идет только тем, кто его прислал
                    connection.put(response.toString());
                    connection.send();

                    break;
                case "action":

                    //Log.v(GameFactory.TAG, "Server: execute() сервер принял команду: " + command);

                    // в данному случае делаем рассылку данных
                    mChanel.sendCommand(command);

                    break;
                default:

                    //Log.v(GameFactory.TAG, "Server: execute() сервер принял команду. Готовим ответ. отправляем");

                    mChanel.sendCommand("response");
            }

        } catch (JSONException e) {
            //Log.v(GameFactory.TAG, "Server: execute() сервер принял команду. Готовим ответ. Ошибка: " + e.getMessage());
            e.printStackTrace();
        }

        //Log.v(GameFactory.TAG, "Server: execute() сервер принял команду. Готовим ответ. отправляем");

        //mChanel.sendCommand("response");
    }

}
