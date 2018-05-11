//package com.votafore.warlords.v4.test;
//
//import android.net.nsd.NsdManager;
//import android.net.nsd.NsdServiceInfo;
//
//import com.votafore.warlords.v4.constant.Log;
//
//import static com.votafore.warlords.v4.constant.Constants.TAG_SCAN_START;
//
///**
// * @author Vorafore
// * created on 13.01.2018
// */
//
//public class DiscoveryListener implements NsdManager.DiscoveryListener {
//
//    /**
//     * mostly these methods are just stub (заглушка)
//     * inherited classes just implement necessary methods
//     *
//     */
//
//
//    @Override
//    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
//        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onStartDiscoveryFailed");
//    }
//
//    @Override
//    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
//        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onStopDiscoveryFailed");
//    }
//
//    @Override
//    public void onDiscoveryStarted(String serviceType) {
//        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onDiscoveryStarted");
//    }
//
//    @Override
//    public void onDiscoveryStopped(String serviceType) {
//        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onDiscoveryStopped");
//    }
//
//    @Override
//    public void onServiceFound(NsdServiceInfo serviceInfo) {
//        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onServiceFound");
//    }
//
//    @Override
//    public void onServiceLost(NsdServiceInfo serviceInfo) {
//        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onServiceLost");
//    }
//}
