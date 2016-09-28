package com.votafore.warlords;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

public class ActivityStart extends AppCompatActivity {

    GameManager manager;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);



        manager = GameManager.getInstance(this);

        RecyclerView serverList = (RecyclerView) findViewById(R.id.list_servers);

        serverList.setHasFixedSize(true);
        serverList.setItemAnimator(new DefaultItemAnimator());
        serverList.setLayoutManager(new LinearLayoutManager(this));

        serverList.setAdapter(manager.getAdapter());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gamelist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.refresh:

                manager.discoverServers(this);
                break;

            case R.id.new_game:

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
