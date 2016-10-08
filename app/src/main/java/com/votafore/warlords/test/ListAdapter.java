package com.votafore.warlords.test;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.votafore.warlords.R;
import com.votafore.warlords.net.IConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<ListItem> mItems;

    public ListAdapter(){
        mItems = new ArrayList<>();
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView  mOwnerName;
        public TextView  mPlayerCount;

        public ViewHolder(View itemView) {
            super(itemView);

            mImageView   = (ImageView) itemView.findViewById(R.id.map_thumbnail);
            mOwnerName   = (TextView) itemView.findViewById(R.id.owner_name);
            mPlayerCount = (TextView) itemView.findViewById(R.id.player_count);

            //itemView.setOnClickListener(this);
        }
    }





    /**************************************************************************************/
    /**************************** дополнительные возможности ******************************/
    /**************************************************************************************/

    public void addItem(ListItem item){
        mItems.add(item);
        notifyItemInserted(mItems.size()-1);
    }

    public void removeItem(IConnection connection){

        for (ListItem item : mItems) {

            if(item.mConnection == connection){

                int index = mItems.indexOf(item);

                mItems.remove(index);
                notifyItemRemoved(index);

                break;
            }
        }
    }

    public void clearList(){

        while(mItems.size() > 0) {
            mItems.get(0).mConnection.close();
            mItems.remove(0);

            notifyItemInserted(0);
        }
    }

    public static class ListItem{

        public IConnection  mConnection;
        public int          mResMap;
        public int          mCreator;
        public String       mCreatorName;
    }
}
