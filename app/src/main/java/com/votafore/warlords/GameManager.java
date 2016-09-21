package com.votafore.warlords;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.votafore.warlords.game.Instance;
import com.votafore.warlords.glsupport.GLRenderer;
import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLView;
import com.votafore.warlords.glsupport.GLWorld;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Votafore
 * Created on 17.09.2016.
 */
public class GameManager extends BroadcastReceiver{

    private static GameManager mThis;

    private Context mContext;

    public static GameManager getInstance(Context context){

        if(mThis == null)
            mThis = new GameManager(context);

        return mThis;
    }

    private GameManager(Context context)throws RuntimeException{

        mContext    = context;
        mWorld      = new GLWorld(this);

        mWorld.camMove(GLWorld.AXIS_Y, 3f);

        subscribeWifiStateChange();
    }





    private Instance mInstance;

    public void setInstance(Instance instance){
        mInstance = instance;
    }



    private GLView mSurfaceView;

    public GLSurfaceView getSurfaceView(){
        return mSurfaceView;
    }




    private GLWorld     mWorld;

    public GLWorld getWorld(){
        return mWorld;
    }







    public void startGame(){

        GLShader   mShader     = new GLShader(mContext, R.raw.shader_vertex, R.raw.shader_fragment);
        GLRenderer mRenderer   = new GLRenderer(mWorld, mInstance, mShader);

        mSurfaceView = new GLView(mContext, mWorld, mRenderer) {
            @Override
            protected void init() {

                mHandler = new MotionHandlerJoystick(mContext, mCamera);
            }

            private MotionHandlerJoystick mHandler;

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return mHandler.onHandleEvent(event);
            }
        };
    }



    //////////////////////////////////////
    // РАЗДЕЛ РАБОТЫ С WI-FI
    // реакция на изменение состояния
    //////////////////////////////////////

    private WifiP2pManager          mWifiP2pManager;
    private WifiP2pManager.Channel  mWifiChannel;

    /**
     * подписываемся на событие изменения статуса Wi-Fi
     */
    private void subscribeWifiStateChange(){

        IntentFilter intentFilter;

        // Indicates a change in the Wi-Fi P2P status.
        // Indicates a change in the list of available peers.
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        // Indicates this device's details have changed.

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mContext.registerReceiver(this, intentFilter);


        mWifiP2pManager     = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        mWifiChannel        = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                Log.v(TAG, "канал отключен");
            }
        });
    }


    private String TAG = "TEST_WIFI";

    /**
     * обработка изменения состояния wi-fi
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()){
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:

                Log.v(TAG, "изменилось состояние");

                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

                    mWifiP2pManager.discoverPeers(mWifiChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {

                            Log.v(TAG, "Запуск поиска пиров: успешно");
                        }

                        @Override
                        public void onFailure(int reason) {

                            Log.v(TAG, "Запуск поиска пиров: ошибка");
                        }
                    });
                }

                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:

                Log.v(TAG, "изменились пиры");

                mWifiP2pManager.requestPeers(mWifiChannel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {

                        int count = peers.getDeviceList().size();

                        if(count == 0)
                            return;

                        Log.v(TAG, String.format("найдены пиры: %s", count));

                        List<WifiP2pDevice> list_peers = new ArrayList();
                        list_peers.clear();
                        list_peers.addAll(peers.getDeviceList());

                        WifiP2pDevice  device = list_peers.get(0);

                        WifiP2pConfig config;

                        config = new WifiP2pConfig();
                        config.deviceAddress = device.deviceAddress;
                        config.wps.setup = WpsInfo.PBC;

                        Log.v(TAG, "попытка подключения к пиру");
                        mWifiP2pManager.connect(mWifiChannel, config, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {

                                Log.v(TAG, "подключение к пиру: успешно");
                            }

                            @Override
                            public void onFailure(int reason) {

                                Log.v(TAG, "подключение к пиру: fail");
                            }
                        });

                    }
                });

                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:

                Log.v(TAG, "изменилось подключение");

                mWifiP2pManager.requestConnectionInfo(mWifiChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {

                        InetAddress address = info.groupOwnerAddress;
                    }
                });

                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:

                Log.v(TAG, "изменились параметры уст-ва");
                //Toast.makeText(mContext, "изменились параметры уст-ва", Toast.LENGTH_SHORT).show();

                WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        }
    }
}
