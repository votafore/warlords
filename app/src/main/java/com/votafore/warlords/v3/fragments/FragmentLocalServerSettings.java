//package com.votafore.warlords.v3.fragments;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.design.widget.TextInputEditText;
//import android.support.v4.app.Fragment;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import com.votafore.warlords.R;
//import com.votafore.warlords.v3.App;
//import com.votafore.warlords.v3.IServer;
//
///**
// * @author Votafore
// * Created on 09.01.2018.
// *
// * represents page with settings of local server
// */
//
//public class FragmentLocalServerSettings extends Fragment {
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        View v = View.inflate(container.getContext(), R.layout.fragment_local_server, null);
//
//        // TODO: 10.01.2018 get title
//        TextInputEditText title = v.findViewById(R.id.some_title);
//
//        Button create_btn = v.findViewById(R.id.start_game);
//
//        create_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
//
//        return v;
//    }
//
//
//
//
//
//    /**************** misc ****************/
//
//    private App mApp;
//
//
//}
