package com.votafore.warlords;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.votafore.warlords.game.Instance;
import com.votafore.warlords.glsupport.GLRenderer;
import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLView;
import com.votafore.warlords.glsupport.GLWorld;


/**
 * @author Votafore
 * Created on 17.09.2016.
 */
public class GameManager {

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
}
