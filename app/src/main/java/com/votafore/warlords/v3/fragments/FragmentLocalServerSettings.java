package com.votafore.warlords.v3.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.votafore.warlords.R;

/**
 * @author Votafore
 * Created on 09.01.2018.
 *
 * represents page with settings of local server
 */

public class FragmentLocalServerSettings extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = View.inflate(container.getContext(), R.layout.fragment_local_server, null);

        return v;
    }
}
