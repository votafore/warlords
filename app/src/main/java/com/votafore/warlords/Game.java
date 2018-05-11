//package com.votafore.warlords;
//
//
//import android.content.Context;
//import android.view.MotionEvent;
//
//import com.votafore.warlords.net.ConnectionChanel;
//import com.votafore.warlords.net.IChanel;
//import com.votafore.warlords.test.MotionHandlerJoystick;
//import com.votafore.warlords.game.EndPoint;
//import com.votafore.warlords.game.Instance;
//import com.votafore.warlords.glsupport.GLRenderer;
//import com.votafore.warlords.glsupport.GLShader;
//import com.votafore.warlords.glsupport.GLView;
//import com.votafore.warlords.glsupport.GLWorld;
//
///**
// * класс содержит объект, которые необходимы для игрового процесса
// * и больше ничего
// */
//public class Game {
//
//    public Game(){
//
//    }
//
//
//
//    /*************************************************************************************************/
//    /*********************************** ОСНОВНЫЕ ОБЪЕКТЫ СИСТЕМЫ ************************************/
//    /*************************************************************************************************/
//
//    private EndPoint          mClient;
//    private EndPoint          mServer;
//
//    public void setClient(EndPoint client){
//
//        mClient         = client;
//        mClientChanel   = client.getChanel();
//    }
//
//    public void setServer(EndPoint server){
//
//        mServer         = server;
//        mServerChanel   = server.getChanel();
//    }
//
//
//
//
//    private GLView mSurfaceView;
//
//    public GLView getSurfaceView(){
//        return mSurfaceView;
//    }
//
//
//
//
//    private IChanel mClientChanel;
//    private IChanel mServerChanel;
//
//    /*************************************************************************************************/
//    /****************************************** доп. раздел ******************************************/
//    /*************************************************************************************************/
//
//
//    public void start(Context context){
//
//        GLWorld     mWorld;
//        GLShader    mShader;
//        GLRenderer  mRenderer;
//
//        mWorld = new GLWorld();
//        mWorld.camMove(GLWorld.AXIS_Y, 3f);
//
//        mShader    = new GLShader(context, R.raw.shader_vertex, R.raw.shader_fragment);
//        mRenderer  = new GLRenderer(mWorld, (Instance)mClient, mShader);
//
//        mSurfaceView = new GLView(context, mWorld, mRenderer) {
//            @Override
//            protected void init() {
//
//                mHandler = new MotionHandlerJoystick(mContext);
//
//                mHandler.setChanel(mClientChanel);
//            }
//
//            private MotionHandlerJoystick mHandler;
//
//            @Override
//            public boolean onTouchEvent(MotionEvent event) {
//                return mHandler.onHandleEvent(event);
//            }
//        };
//
//        ((Instance)mClient).start();
//        ((Instance)mClient).setCamera(mWorld);
//    }
//}