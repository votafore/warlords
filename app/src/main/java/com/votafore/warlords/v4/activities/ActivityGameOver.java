package com.votafore.warlords.v4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

//import com.votafore.warlords.Activity1;
import com.votafore.warlords.R;

public class ActivityGameOver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Button finish = findViewById(R.id.finish);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToStart();
            }
        });
    }


    @Override
    public void onBackPressed() {

        backToStart();
    }

    private void backToStart(){
        finish();

        // TODO: return
//        Intent i = new Intent(this, Activity1.class);
//        startActivity(i);
    }
}
