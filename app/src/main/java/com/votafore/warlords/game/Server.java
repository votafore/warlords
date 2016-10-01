package com.votafore.warlords.game;


import android.util.Log;

import com.votafore.warlords.GameManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Votafore
 * Created on 28.09.2016.
 *
 * серверная часть игры
 */

public class Server extends EndPoint{










    /*****************************************************************************************************************/
    /*********************************************** РАЗДЕЛ РАБОТЫ ПО СЕТИ (ИЛИ ЛОКАЛЬНО) ****************************/
    /*****************************************************************************************************************/

//    private Agent mClient;
//
//    public void setClient(Agent client){
//        mClient = client;
//    }


    /*****************************************************************************************************************/
    /*********************************************** ОБЯЗАННОСТИ СЕРВЕРА ;) ******************************************/
    /*****************************************************************************************************************/


    @Override
    public void execute(String command) {

        Log.v(GameManager.TAG, "Server: execute() сервер принял команду. Готовим ответ");

        // обработка сообщения

        // рассылка остальным
        //mClient.onEndPointResponded(command);

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
                    break;

            }

            Log.v(GameManager.TAG, "Server: execute() сервер принял команду. Готовим ответ. отправляем");
            if(!response.toString().isEmpty()){
                mConnectionManager2.sendCommand(response.toString());
                return;
            }

        } catch (JSONException e) {
            Log.v(GameManager.TAG, "Server: execute() сервер принял команду. Готовим ответ. Ошибка: " + e.getMessage());
            e.printStackTrace();
        }

        Log.v(GameManager.TAG, "Server: execute() сервер принял команду. Готовим ответ. отправляем");
        mConnectionManager2.sendCommand("response");
    }

}
