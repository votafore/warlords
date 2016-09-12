package com.votafore.warlords.glsupport;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glViewport;

public  class GLRenderer implements Renderer {

    public interface ICallback{
        void onSurfaceCreated();
        void onSurfaceChanged(int width, int height);
        void onDrawFrame(GLShader shader);
    }

    private GLShader mShader;

    private GLWorld mWorld;
    private ICallback mCallbackHandler;


    public GLRenderer(Context context, int resVertexShader, int resFragmentShader) {

        mWorld = GLWorld.getInstance(context);
        mCallbackHandler = (ICallback)mWorld;

        mShader = new GLShader(context, resVertexShader, resFragmentShader);
    }

    public GLShader getShader(){
        return mShader;
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {

        mCallbackHandler.onSurfaceCreated();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mShader.createShader();
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        glViewport(0, 0, width, height);

        mCallbackHandler.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 arg0) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float[] mat = new float[16];
        Matrix.setIdentityM(mat, 0);

        Matrix.multiplyMM(mat, 0, mWorld.mProjectionMatrix, 0, mWorld.mViewMatrix, 0);

        GLES20.glUniformMatrix4fv(mShader.mParams.get("u_Matrix"), 1, false, mat, 0);

        mCallbackHandler.onDrawFrame(mShader);
    }
}
