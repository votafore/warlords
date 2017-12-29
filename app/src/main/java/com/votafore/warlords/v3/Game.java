package com.votafore.warlords.v3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.votafore.warlords.R;
import com.votafore.warlords.test.MeshUnit;
import com.votafore.warlords.test.MotionHandlerJoystick;
import com.votafore.warlords.v3.glsupport.GLRenderer;
import com.votafore.warlords.v3.glsupport.GLShader;
import com.votafore.warlords.v3.glsupport.GLUnit;
import com.votafore.warlords.v3.glsupport.GLView;
import com.votafore.warlords.v3.glsupport.GLWorld;

/**
 * @author Vorafore
 * Created on 28.12.2017.
 */

public class Game {

    private Context mContext;

    private GLSurfaceView mSurfaceView;

    public Game(Context c){
        mContext = c;
    }

//    @SuppressLint("WrongConstant")
//    public void start(Context context){
//
//        GLWorld mWorld;
//        GLShader mShader;
//        GLRenderer mRenderer;
//
//        mWorld = new GLWorld();
//        mWorld.camMove(GLWorld.AXIS_Y, 3f);
//
//        mShader    = new GLShader(context, R.raw.shader_vertex, R.raw.shader_fragment);
//        mRenderer  = new GLRenderer(mWorld, this, mShader);
//
//        mSurfaceView = new GLView(context, mWorld, mRenderer) {
//            @Override
//            protected void init() {
//
//                //mHandler = new MotionHandlerJoystick(mContext);
//
//                //mHandler.setChanel(mClientChanel);
//            }
//
//            private MotionHandlerJoystick mHandler;
//
//            @Override
//            public boolean onTouchEvent(MotionEvent event) {
//                return true; //mHandler.onHandleEvent(event);
//            }
//        };
//
//        //((Instance)mClient).start();
//        //((Instance)mClient).setCamera(mWorld);
//    }
//
//
//
//
//
//    /**************** for renderer *********************/
//
//    public GLUnit mBase;
//    private GLUnit mMap;
//
//    public void setMap(GLUnit map){
//        mMap = map;
//    }
//
//    public GLUnit getMap(){
//        return mMap;
//    }
//
//    public void start(){
//
//        mBase = new MeshUnit(mContext);
//        mBase.init();
//
//        mMap.init();
//    }
}
