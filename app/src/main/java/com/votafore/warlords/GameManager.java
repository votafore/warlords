package com.votafore.warlords;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.votafore.warlords.game.Instance;
import com.votafore.warlords.glsupport.GLRenderer;
import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLUnit;
import com.votafore.warlords.glsupport.GLView;
import com.votafore.warlords.glsupport.GLWorld;

import java.util.List;

/**
 * @author Votafore
 * Created on 17.09.2016.
 */
public class GameManager implements GLRenderer.ICallback {

    private static GameManager mThis;

    private Context mContext;

    public static GameManager getInstance(Context context){

        if(mThis == null)
            mThis = new GameManager(context);

        return mThis;
    }

    private GameManager(Context context)throws RuntimeException{

        mContext    = context;

        mShader     = new GLShader(mContext, R.raw.shader_vertex, R.raw.shader_fragment);
        mWorld      = new GLWorld(this);
        mRenderer   = new GLRenderer(this);

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

        mWorld.camMove(GLWorld.AXIS_Y, 3f);
    }





    private Instance mInstance;

    public void setInstance(Instance instance){
        mInstance = instance;
    }

    public Instance getcurrentInstance(){
        return mInstance;
    }


    private GLView mSurfaceView;

    public GLSurfaceView getSurfaceView(){

        return mSurfaceView;
    }




    private GLWorld     mWorld;

    public GLWorld getWorld(){
        return mWorld;
    }



    private GLRenderer mRenderer;

    private GLShader mShader;




    @Override
    public void onSurfaceCreated(){

        mWorld.positionChanged();
        mShader.createShader();
    }

    @Override
    public void onSurfaceChanged(int width, int height){

        float left      = -mWorld.mWidth/2;
        float right     =  mWorld.mWidth/2;
        float bottom    = -mWorld.mHeight/2;
        float top       =  mWorld.mHeight/2;
        float near      =  mWorld.mNear;
        float far       =  mWorld.mFar;

        float ratio = (float) height / width;
        bottom  *= ratio;
        top     *= ratio;

        if (width > height) {

            bottom  = -mWorld.mHeight/2;
            top     =  mWorld.mHeight/2;

            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        }

        Matrix.frustumM(mWorld.mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame() {

        float[] mat = new float[16];
        Matrix.setIdentityM(mat, 0);

        Matrix.multiplyMM(mat, 0, mWorld.mProjectionMatrix, 0, mWorld.mViewMatrix, 0);

        GLES20.glUniformMatrix4fv(mShader.mParams.get("u_Matrix"), 1, false, mat, 0);

        // первым делом отрисовываем карту
        mInstance.getMap().draw(mShader);


        // потом все остальные объекты сцены
        List<GLUnit> objects = mInstance.getObjects();

        for (GLUnit curr_obj : objects) {
            curr_obj.draw(mShader);
        }
    }
}
