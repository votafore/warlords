//package com.votafore.warlords;
//
//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//public class ActivityGameOver extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_game_over);
//
//        Button finish = findViewById(R.id.finish);
//
//        finish.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                backToStart();
//            }
//        });
//    }
//
//
//    @Override
//    public void onBackPressed() {
//
//        backToStart();
//    }
//
//    private void backToStart(){
//        finish();
//
//        Intent i = new Intent(this, Activity1.class);
//        startActivity(i);
//    }
//}
