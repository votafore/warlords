package com.votafore.warlords.test;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;



public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {



    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
