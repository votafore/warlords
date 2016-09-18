package com.votafore.warlords;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.game.Instance;

public class ActivityStart extends AppCompatActivity implements View.OnClickListener {

    GameManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button btn = (Button) findViewById(R.id.btn_start);

        btn.setOnClickListener(this);





        manager = GameManager.getInstance(getApplicationContext());

        // предполагается что будет возможность либо создать игру, либо присоединиться к созданной
        // пока что создаем игру

        Instance inst = new Instance(getApplicationContext(), 0);

        // типа мы выбрали карту
        inst.setMap(new MeshMapTest(getApplicationContext()));

        // сказали что "... вот такие параметры боя..."
        manager.setInstance(inst);
    }

    @Override
    public void onClick(View v) {

        manager.startGame();

        // наступил момент, когда статус "Готов к бою" установили все
        Intent i = new Intent(this, ActivityMain.class);
        startActivity(i);
    }
}
