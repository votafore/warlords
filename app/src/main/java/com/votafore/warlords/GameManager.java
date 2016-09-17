package com.votafore.warlords;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.votafore.warlords.game.Instance;
import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLView;
import com.votafore.warlords.glsupport.GLWorld;

import java.util.HashMap;

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

    private GameManager(Context context){

        mContext = context;

        mSurfaceView = new GLView(mContext) {
            @Override
            protected void init() {

                /////////////////////////////
                // обязательная часть

                resVertexShader     = R.raw.shader_vertex;
                resFragmentShader   = R.raw.shader_fragment;

                mShaderCreator = new GLShader.IGLShaderCreator() {

                    @Override
                    public void populateParameters(int programID, HashMap<String, Integer> params) {

                        // параметры vertex шейдера
                        params.put("u_Matrix"       , GLES20.glGetUniformLocation(programID, "u_Matrix"));
                        params.put("u_color"        , GLES20.glGetUniformLocation(programID, "u_color"));

                        params.put("a_position"     , GLES20.glGetAttribLocation(programID, "a_position"));
                        params.put("a_normal"       , GLES20.glGetAttribLocation(programID, "a_normal"));

                        // параметры fragment шейдера
                        params.put("u_camera"       , GLES20.glGetUniformLocation(programID, "u_camera"));
                        params.put("u_lightPosition", GLES20.glGetUniformLocation(programID, "u_lightPosition"));
                    }
                };


                /////////////////////////////
                // не обязательная часть

                mWorld.setBaseColor(Constants.base_color);

                mHandler = new MotionHandlerJoystick(mContext, mCamera);

                mCamera.camMove(GLWorld.AXIS_Y, 3f);
            }

            private MotionHandlerJoystick mHandler;

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return mHandler.onHandleEvent(event);
            }
        };
    }





    Instance mInstance;

    public void setInstance(Instance instance){
        mInstance = instance;
    }



    private GLView mSurfaceView;

    public GLSurfaceView getSurfaceView(){
        return mSurfaceView;
    }
}
