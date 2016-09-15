package com.votafore.warlords;

import android.content.Context;
import android.opengl.GLES20;
import android.view.MotionEvent;

import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLView;
import com.votafore.warlords.glsupport.GLWorld;

import java.util.HashMap;


public class CustomSurfaceView extends GLView {

    public CustomSurfaceView(Context context) {
        super(context);

        mHandler = new MotionHandler(context, mCamera);

        mCamera.camMove(GLWorld.AXIS_Y, 3f);
    }

    @Override
    protected void init(){

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

        mWorld.setBaseColor(new float[]{1f,0.5f,0.1f, 3f});
    }

    ////////////////////////////////////////
    // обработка касаний

    private MotionHandler mHandler;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mHandler.onHandleEvent(event);
    }
}
