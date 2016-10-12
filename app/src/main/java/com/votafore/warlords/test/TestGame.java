package com.votafore.warlords.test;


import android.content.Context;
import android.view.MotionEvent;

import com.votafore.warlords.MotionHandlerJoystick;
import com.votafore.warlords.R;
import com.votafore.warlords.game.EndPoint;
import com.votafore.warlords.game.Instance;
import com.votafore.warlords.glsupport.GLRenderer;
import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLView;
import com.votafore.warlords.glsupport.GLWorld;

/**
 * класс содержит объект, которые необходимы для игрового процесса
 * и больше ничего
 */
public class TestGame {

    public TestGame(){

    }



    /*************************************************************************************************/
    /*********************************** ОСНОВНЫЕ ОБЪЕКТЫ СИСТЕМЫ ************************************/
    /*************************************************************************************************/

    private EndPoint          mClient;
    private EndPoint          mServer;

    public void setClient(EndPoint client){
        mClient = client;
    }

    public void setServer(EndPoint server){
        mServer = server;
    }




    private Instance    mInstance;
    private GLView      mSurfaceView;

    public GLView getSurfaceView(){
        return mSurfaceView;
    }




    /*************************************************************************************************/
    /****************************************** доп. раздел ******************************************/
    /*************************************************************************************************/


    public void start(Context context){

        GLWorld     mWorld;
        GLShader    mShader;
        GLRenderer  mRenderer;

        mWorld = new GLWorld();
        mWorld.camMove(GLWorld.AXIS_Y, 3f);

        mShader    = new GLShader(context, R.raw.shader_vertex, R.raw.shader_fragment);
        mRenderer  = new GLRenderer(mWorld, mInstance, mShader);

        mSurfaceView = new GLView(context, mWorld, mRenderer) {
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
}