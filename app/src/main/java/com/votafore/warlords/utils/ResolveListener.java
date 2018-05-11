package com.votafore.warlords.utils;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;


import com.votafore.warlords.constant.Log;

import static com.votafore.warlords.constant.Constants.TAG_SCAN_START;


/**
 * @author Vorafore
 * Created on 13.01.2018.
 */

public class ResolveListener implements NsdManager.ResolveListener {

    /**
     * mostly these methods are just stub (заглушка)
     * inherited classes just implement necessary methods
     *
     */



    @Override
    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.d3(TAG_SCAN_START, "CONN OBSERVABLE", "FOUND", "RESOLVE", "onResolveFailed");
    }

    @Override
    public void onServiceResolved(NsdServiceInfo serviceInfo) {
        Log.d3(TAG_SCAN_START, "CONN OBSERVABLE", "FOUND", "RESOLVE", "onServiceResolved");
    }
}
