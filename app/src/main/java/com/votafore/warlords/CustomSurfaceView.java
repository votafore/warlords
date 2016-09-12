package com.votafore.warlords;

import android.content.Context;
import android.opengl.GLES20;
import android.view.MotionEvent;

import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLView;

import java.util.HashMap;


public class CustomSurfaceView extends GLView {

    public CustomSurfaceView(Context context) {
        super(context);

        mHandler = new MotionHandler(context, mCamera);
    }

    @Override
    protected GLShader.IGLShaderCreator getShaderCreator() {
        return new GLShader.IGLShaderCreator() {

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
    }

    @Override
    protected void setShaderSources() {

        resVertexShader     = R.raw.shader_vertex;
        resFragmentShader   = R.raw.shader_fragment;
    }

    @Override
    protected void setWorldParams() {

        mWorld.setBaseColor(new float[]{1f,0.5f,0.1f, 1f});
    }

    ////////////////////////////////////////
    // обработка касаний

    private MotionHandler mHandler;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mHandler.onHandleEvent(event);
    }
}
