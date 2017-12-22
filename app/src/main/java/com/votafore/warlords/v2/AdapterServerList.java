package com.votafore.warlords.v2;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.votafore.warlords.R;
import com.votafore.warlords.v2.test.Channel_v2;
import com.votafore.warlords.v2.test.IChannel_v2;
import com.votafore.warlords.v2.test.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;


/**
 * @author Votafore
 * Created on 18.12.2017.
 */

public class AdapterServerList extends RecyclerView.Adapter<AdapterServerList.ViewHolder> implements IAdapter{


    public AdapterServerList(){

        mServiceList = new ArrayList<>();
        mList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(parent.getContext(), R.layout.item_found_game, null);
        return new AdapterServerList.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ListItem item = mList.get(position);

        //holder.mImageView.setImageResource(current.mResMap);
        holder.mOwnerName.setText(item.ownerName);
        holder.mPlayerCount.setText(item.playerCount);
    }

    @Override
    public int getItemCount() {
        return mServiceList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mOwnerName;
        public TextView  mPlayerCount;

        public ViewHolder(View itemView) {
            super(itemView);

            mImageView   = (ImageView) itemView.findViewById(R.id.map_thumbnail);
            mOwnerName   = (TextView) itemView.findViewById(R.id.owner_name);
            mPlayerCount = (TextView) itemView.findViewById(R.id.player_count);
        }
    }






    /********************* IAdapter *****************/

    private List<ServiceInfo> mServiceList;
    private List<ListItem> mList;

    @Override
    public void addServer(ServiceInfo info) {

        // check if IP already exists
        for(ServiceInfo service: mServiceList){
            if(service.info.getHost().toString().equals(info.info.getHost().toString()))
                return;
        }

        mServiceList.add(info);

        Log.v("TESTRX", ">>>>>>>>> info is added to list");

        ListItem item = new ListItem();

        Log.v("TESTRX", ">>>>>>>>> new list item is created");
        item.createChanel(info.info.getHost(), info.info.getPort());
        item.getChanel().getSender().onNext(new JSONObject());

        mList.add(item);
        notifyItemInserted(mServiceList.size()-1);
    }

    @Override
    public void removeServer(ServiceInfo info) {

        // find server by IP
        String ip = info.info.getHost().toString();
        int index = -1;

        for(ServiceInfo service: mServiceList){
            if(service.info.getHost().toString().equals(ip)){
                index = mServiceList.indexOf(service);
                break;
            }
        }

        if(index < 0){
            // strange... service doesn't exist
            return;
        }

        mList.get(index).getChanel().getSender().onComplete();
        mList.remove(index);

        mServiceList.remove(index);
        notifyItemRemoved(index);
    }



    public class ListItem{

        String ownerName;
        String playerCount;

        private IChannel_v2 mChanel;

        public void createChanel(final InetAddress ip, final int port){

            Log.v("TESTRX", ">>>>>>>>> ListItem - createChanel");

            Channel_v2 ch = new ChanelForList();

            Log.v("TESTRX", ">>>>>>>>> ListItem - createChanel. created");

            ch.setReceiver(new Consumer<JSONObject>() {
                @Override
                public void accept(JSONObject jsonObject) throws Exception {

                    Log.v("TESTRX", ">>>>>>>>> ListItem - createChanel. receiver is called");

                    try {
                        ownerName = jsonObject.getString("owner");
                        playerCount = jsonObject.getString("playerCount");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    AdapterServerList.this.notifyItemChanged(mList.indexOf(ListItem.this));
                }
            });

            Log.v("TESTRX", ">>>>>>>>> ListItem - createChanel. receiver is set");
            ch.addSocket(ip, port);

            mChanel = ch;
        }

        public IChannel_v2 getChanel(){
            return mChanel;
        }
    }


    public class ChanelForList extends Channel_v2{

        @Override
        public void addSocket() {

        }

        @Override
        public void addSocket(InetAddress ip, int port) {
            try {
                Socket s = new Socket(ip, port);
                Log.v("TESTRX", ">>>>>>>>> Chanel for list. socket created");
                onSocketAdded(s);
                Log.v("TESTRX", ">>>>>>>>> Chanel for list. socket added to channel");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
