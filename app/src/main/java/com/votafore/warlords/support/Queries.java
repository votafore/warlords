package com.votafore.warlords.support;

import android.util.Log;

import com.votafore.warlords.GameManager;

import org.json.JSONException;
import org.json.JSONObject;

public class Queries {

    public static final int QUERY_INSTANCE = 148;

    public static String getQuery(int type){

        String result = "";

        try{

            switch(type){

                case QUERY_INSTANCE:

                    JSONObject query = new JSONObject();

                    query.put("clientID" , 0);
                    query.put("type"     , "InstanceInfo");
                    query.put("command"  , "get");

                    result = query.toString();

                    Log.v(GameManager.TAG, "Queries - getQuery: сформировали запрос (" + query + ")");

                    break;
            }

        } catch (JSONException e) {
            Log.v(GameManager.TAG, "ServiceScanner: поток рассылки запросов, создание запроса: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
