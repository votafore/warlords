package com.votafore.warlords.network;

import org.json.JSONException;
import org.json.JSONObject;

public class Queries {

    public static final int QUERY_INSTANCE = 148;




    public static final int CLIENT_ID = 0;




    // query types
    public static final String QT_QUERY     = "QUERY";
    public static final String QT_BROADCAST = "BROADCAST";


    public static String getQuery(int type){

        String result = "";

        try{

            switch(type){

                case QUERY_INSTANCE:

                    JSONObject query = new JSONObject();

                    query.put("clientID" , CLIENT_ID);
                    query.put("type"     , "InstanceInfo");
                    query.put("command"  , "get");
                    query.put("queryType", QT_QUERY);

                    result = query.toString();

                    //Log.v(GameFactory.TAG, "Queries - getQuery: сформировали запрос (" + query + ")");

                    break;
            }

        } catch (JSONException e) {
            //Log.v(GameFactory.TAG, "ServiceScanner: поток рассылки запросов, создание запроса: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public static JSONObject getQueryCamMove(float deltaX, float deltaY){

        JSONObject message = new JSONObject();

        try {

            message.put("clientID" , CLIENT_ID);
            message.put("type"     , "data");
            message.put("event"  , "camMove");
            message.put("queryType", QT_BROADCAST);
            message.put("deltaX"   , String.valueOf(deltaX));
            message.put("deltaY"   , String.valueOf(deltaY));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }

    public static JSONObject getQueryCamRotate(float deltaX, float deltaY){

        JSONObject message = new JSONObject();

        try {

            message.put("clientID" , CLIENT_ID);
            message.put("type"     , "data");
            message.put("event"  , "camRotate");
            message.put("queryType", QT_BROADCAST);
            message.put("deltaX"   , String.valueOf(deltaX));
            message.put("deltaY"   , String.valueOf(deltaY));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }
}
