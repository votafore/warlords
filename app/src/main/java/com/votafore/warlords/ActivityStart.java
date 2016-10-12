package com.votafore.warlords;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.votafore.warlords.test.GameFactory;


public class ActivityStart extends AppCompatActivity {

    private GameFactory   mFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);



        RecyclerView serverList = (RecyclerView) findViewById(R.id.list_servers);

        serverList.setHasFixedSize(true);
        serverList.setItemAnimator(new DefaultItemAnimator());
        serverList.setLayoutManager(new LinearLayoutManager(this));

        mFactory = GameFactory.getInstance(this);

        mFactory.onActivityCreate(this);

        serverList.setAdapter(mFactory.getAdapter());

//        manager.getAdapter().setListener(new GameManager.ClickListener() {
//            @Override
//            public void onClick(int position) {
//
//                //manager.handleServerListActivityStoped();
//
//                manager.startGame(ActivityStart.this);
//
//                Intent i = new Intent(ActivityStart.this, ActivityMain.class);
//                startActivity(i);
//
//            }
//        });
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.v(GameManager.TAG, "ActivityStart: onResume().");
        mFactory.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.v(GameManager.TAG, "ActivityStart: onPause().");
        mFactory.onActivityPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gamelist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.new_game:

                mFactory.createServer(this);

                break;
            case R.id.start_game:

                mFactory.startGame(this);

                break;
            case R.id.call_someFunc:

                mFactory.someFunc();

                break;
            case R.id.exit:

                mFactory.exit();
        }

        return super.onOptionsItemSelected(item);
    }
}
