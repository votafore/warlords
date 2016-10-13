package com.votafore.warlords.game;


import android.util.Log;

import com.votafore.warlords.GameManager;
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
    public void execute(IConnection connection, String command) {

        Log.v(GameManager.TAG, "Server: execute() сервер принял команду. Готовим ответ");

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

                    Log.v(GameManager.TAG, "Server: execute() сервер принял команду. Готовим ответ. отправляем");

                    // на некоторые типы запросов ответ идет только тем, кто его прислал
                    connection.put(response.toString());
                    connection.send();

                    break;

                default:

                    Log.v(GameManager.TAG, "Server: execute() сервер принял команду. Готовим ответ. отправляем");

                    mChanel.sendCommand("response");
            }

        } catch (JSONException e) {
            Log.v(GameManager.TAG, "Server: execute() сервер принял команду. Готовим ответ. Ошибка: " + e.getMessage());
            e.printStackTrace();
        }

        Log.v(GameManager.TAG, "Server: execute() сервер принял команду. Готовим ответ. отправляем");

        mChanel.sendCommand("response");
    }

}
