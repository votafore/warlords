package com.votafore.warlords;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.v2.AdapterServerList;
import com.votafore.warlords.v2.App;

public class ActivityGame extends AppCompatActivity {

    private App mApp;

    private AdapterServerList.ListItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mApp = (App) getApplication();
        mItem = mApp.getSelected();

        Button startGame = findViewById(R.id.start_game);

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.startGame();

                startActivity(new Intent(ActivityGame.this, ActivityMain.class));

                finish();
            }
        });
    }







    // TODO: 25.12.2017 implement IDataChange listener in current environment
}
