package com.votafore.warlords.v4.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.votafore.warlords.R;
import com.votafore.warlords.v2.Constants;
import com.votafore.warlords.v2.ServiceInfo;
import com.votafore.warlords.v3.Log;
import com.votafore.warlords.v4.App;
import com.votafore.warlords.v4.network.ISearchingListener;
import com.votafore.warlords.v4.network.ServerLocal;
import com.votafore.warlords.v4.network.ServerRemote;
import com.votafore.warlords.v4.test.DiscoveryListener;
import com.votafore.warlords.v4.test.ResolveListener;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static com.votafore.warlords.v2.Constants.LVL_ADAPTER;
import static com.votafore.warlords.v2.Constants.TAG_SCAN;
import static com.votafore.warlords.v2.Constants.TAG_SCAN_START;

public class ActivityStart extends AppCompatActivity {

    private App mApp;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        mApp = (App) getApplication();

        Button create_btn = findViewById(R.id.create_btn);
        Button connect_btn = findViewById(R.id.connect_btn);

        final Intent local = new Intent(this, ActivityGameLocal.class);
        final Intent remote = new Intent(this, ActivityGameRemote.class);

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApp.setServer(new ServerLocal());
                startActivity(local);
            }
        });

        mDialog = new ProgressDialog(this);
        mDialog.setTitle("searching...");
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mApp.getServer().setSearchingListener(null);
                mApp.getServer().stopSearching();
            }
        });

        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mApp.setServer(new ServerRemote());
                mApp.getServer().setSearchingListener(new ISearchingListener() {
                    @Override
                    public void onSearchingStart() {

                    }

                    @Override
                    public void onSearchingEnd() {
                        mDialog.dismiss();
                        mApp.getServer().setSearchingListener(null);
                        startActivity(remote);
                    }
                });

                mDialog.show();

                mApp.getServer().startSearching(ActivityStart.this);

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDialog.dismiss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isSearching", mDialog.isShowing());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState.getBoolean("isSearching"))
            mDialog.show();
    }
}
