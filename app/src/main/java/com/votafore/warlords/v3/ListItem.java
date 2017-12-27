package com.votafore.warlords.v3;

import android.util.Log;

import com.votafore.warlords.v2.Constants;

import org.json.JSONObject;

import io.reactivex.functions.Consumer;

/**
 * @author Votafore
 * Created on 26.12.2017.
 */

public class ListItem {

    String TAG = Constants.TAG;

    String prefix= Constants.PFX_LIST_ITEM;

    String format1 = Constants.format1;
    String format2 = Constants.format2;
    String format3 = Constants.format3;
    String format4 = Constants.format4;





    public String owner;
    public String count;

    private IServer mServer;
    private IItemChangeListener mListener;

    public ListItem(IServer server){

        Log.v(TAG, String.format(format1, prefix, "ListItem"));

        mServer = server;

        Log.v(TAG, String.format(format2, prefix, "ListItem", "server set"));

        mServer.setReceiver(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject object) throws Exception {
                owner = object.getString("owner");
                count = object.getString("count");

                Log.v(TAG, String.format(format3, prefix, "ListItem", "Receiver", "new data received... notify adapter"));

                mListener.onChange(ListItem.this);
            }
        });

        Log.v(TAG, String.format(format2, prefix, "ListItem", "default receiver set"));
    }

    public void send(JSONObject data){
        Log.v(TAG, String.format(format1, prefix, "send"));
        mServer.send(data);
    }

    public void stop(){
        Log.v(TAG, String.format(format1, prefix, "stop"));
        mServer.stop();
    }

    public void setListener(IItemChangeListener listener){
        Log.v(TAG, String.format(format1, prefix, "listener for adapter set"));
        mListener = listener;
    }




    @Override
    public String toString(){
        return mServer.toString();
    }

    public interface IItemChangeListener{
        void onChange(ListItem item);
    }
}
