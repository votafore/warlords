//package com.votafore.warlords;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//
//public class ActivityTest extends AppCompatActivity {
//
//    private ProgressDialog mDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//
//        Button start = findViewById(R.id.start);
//
//        start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialog();
//            }
//        });
//
//        mDialog = new ProgressDialog(ActivityTest.this);
//
//        mDialog.setTitle("searching...");
//        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mDialog.setCancelable(false);
//        mDialog.setButton(Dialog.BUTTON_NEGATIVE, "STOP", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putBoolean("showDialog", mDialog.isShowing());
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        if(savedInstanceState.getBoolean("showDialog"))
//            showDialog();
//    }
//
//    private void showDialog(){
//        mDialog.show();
//    }
//}
