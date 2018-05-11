//package com.votafore.warlords;
//
//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//public class Activity1 extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_1);
//
//        Button create_btn = findViewById(R.id.create_btn);
//        Button connect_btn = findViewById(R.id.connect_btn);
//
//        create_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent i = new Intent(Activity1.this, ActivityGame.class);
//
//                startActivity(i);
//            }
//        });
//
//        connect_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent i = new Intent(Activity1.this, ActivityStart.class);
//                i.putExtra("TYPE", "connect");
//
//                startActivity(i);
//            }
//        });
//    }
//}
