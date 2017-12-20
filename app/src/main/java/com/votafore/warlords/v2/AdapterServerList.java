package com.votafore.warlords.v2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.votafore.warlords.R;
import com.votafore.warlords.support.ListAdapter;
import com.votafore.warlords.v2.test.Chanel;
import com.votafore.warlords.v2.test.IChanel;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;


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

        ListItem item = new ListItem();
        item.createChanel(info.info.getHost(), info.info.getPort());
        item.getProcessor().onNext(new JSONObject());

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

        mList.get(index).closeChanel();
        mList.remove(index);

        mServiceList.remove(index);
        notifyItemRemoved(index);
    }



    public class ListItem{

        public String ownerName;
        public String playerCount;



        private IChanel mChanel;

        private PublishProcessor<JSONObject> pp;

        public void createChanel(InetAddress ip, int port){

            pp = PublishProcessor.create();
            pp.observeOn(Schedulers.io());

            mChanel = new Chanel(pp, new Consumer<JSONObject>() {
                @Override
                public void accept(JSONObject jsonObject) throws Exception {

                    ownerName = jsonObject.getString("owner");
                    playerCount = jsonObject.getString("playerCount");

                    AdapterServerList.this.notifyItemChanged(mList.indexOf(ListItem.this));
                }
            });

            mChanel.connect(ip, port);
        }

        public PublishProcessor<JSONObject> getProcessor(){
            return pp;
        }

        public void closeChanel(){
            mChanel.disconnect();
            pp.onComplete();
        }
    }
}