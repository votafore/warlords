package com.votafore.warlords.support;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.votafore.warlords.GameFactory;
import com.votafore.warlords.R;
import com.votafore.warlords.net.IConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<ListItem> mItems;

    private HashMap<String, ListItem> mHostItem = new HashMap<>();

    public ListAdapter(){
        mItems = new ArrayList<>();
    }




    private GameFactory.ClickListener mListener;

    public void setListener(GameFactory.ClickListener listener){
        mListener = listener;
    }


    /**************************************************************************************/
    /******************************** RecyclerView.Adapter ********************************/
    /**************************************************************************************/

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = View.inflate(parent.getContext(), R.layout.item_found_game, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {

        ListItem item = mItems.get(position);

        holder.mImageView.setImageResource(item.mResMap);
        holder.mOwnerName.setText(item.mCreatorName);
        holder.mPlayerCount.setText("undefined");
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView mImageView;
        public TextView  mOwnerName;
        public TextView  mPlayerCount;

        public ViewHolder(View itemView) {
            super(itemView);

            mImageView   = (ImageView) itemView.findViewById(R.id.map_thumbnail);
            mOwnerName   = (TextView) itemView.findViewById(R.id.owner_name);
            mPlayerCount = (TextView) itemView.findViewById(R.id.player_count);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            if(mListener == null)
                return;

            mListener.onClick(getAdapterPosition());
        }
    }




    /**************************************************************************************/
    /**************************** дополнительные возможности ******************************/
    /**************************************************************************************/

    public void addItem(ListItem item){

        //Log.v(GameManager.TAG + "_1", "ListAdapter - addItem.");

        // добавляем только если такого хоста у нас еще нет
        for (ListItem curItem : mItems) {

            //Log.v(GameManager.TAG, "ListAdapter - addItem. проверка хостов... " + item.mHost + " -- " + curItem.mHost);

            if(item.mHost.equals(curItem.mHost)){
                return;
            }
        }

        mItems.add(item);
        notifyItemInserted(mItems.size()-1);

        //Log.v(GameManager.TAG, "ListAdapter - addItem. Количество элементов: " + String.valueOf(mItems.size()));
    }

    public boolean isExist(String host){

        for (ListItem curItem : mItems) {

            //Log.v(GameManager.TAG, "ListAdapter - isExist. проверка хостов... " + item.mHost + " -- " + curItem.mHost);

            if(curItem.mHost.equals(host)){
                return true;
            }
        }

        return false;
    }

    public ListItem getItemByPosition(int position){
        return mItems.get(position);
    }

    public void deleteItemByConnection(IConnection connection){

        for (ListItem curItem : mItems) {

            if(curItem.mConnection != connection)
                continue;

            mItems.remove(curItem);
            break;
        }
    }

    public static class ListItem{

        public IConnection  mConnection;
        public int          mResMap;
        public int          mCreator;
        public String       mCreatorName;
        public String       mHost;
    }
}
