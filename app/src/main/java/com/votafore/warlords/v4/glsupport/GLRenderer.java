package com.votafore.warlords.v4.glsupport;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.votafore.warlords.test.Constants;
import com.votafore.warlords.v4.World;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glViewport;

/**
 * старался реализовать его так, что бы он выполнял свои функции
 * (отрисовка) без каких либо изменений кода. Т.е. что бы
 * сюда больше не приходилось залазить.
 * <p>
 * <b>есть 1 момент:</b> в функции onDrawFrame устанавливается
 * матрица. Т.е. предполагается что в вершинном шейдере есть переменная
 * <b>uniform mat4 u_Matrix</b>. Поэтому если будет использоваться шейдер
 * в котором нет такой переменной, то надо будет редактировать код
 * рендерера.
 */

public  class GLRenderer implements Renderer {

    private GLWorld mWorld;
    private World    mGaveWorld;
    private GLShader mShader;

    public GLRenderer(GLWorld GLworld, World world, GLShader shader) {

        mWorld      = GLworld;
        mGaveWorld  = world;
        mShader     = shader;
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {

        float[] mBaseColor = Constants.base_color;

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearColor(mBaseColor[0],mBaseColor[1],mBaseColor[2],mBaseColor[3]);

        mWorld.positionChanged();
        mShader.createShader();
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {

        glViewport(0, 0, width, height);

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
    public void onDrawFrame(GL10 arg0) {

        mWorld.onNewFrame();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUniform3f(mShader.mParams.get("u_lightPosition")   , 0f  , 3f  , 6f);
        GLES20.glUniform4f(mShader.mParams.get("u_color")           , 0.3f, 0.9f, 0f, 1.0f);

        float[] tmpCamPosition = new float[4];
        System.arraycopy(GLWorld.position_vec, 0, tmpCamPosition, 0,4);
        Matrix.multiplyMV(tmpCamPosition, 0, GLWorld.mPositionMatrix, 0, tmpCamPosition, 0);
        GLES20.glUniform3f(mShader.mParams.get("u_camera"), tmpCamPosition[0], tmpCamPosition[1], tmpCamPosition[2]);

        float[] mat = new float[16];
        Matrix.setIdentityM(mat, 0);

        Matrix.multiplyMM(mat, 0, mWorld.mProjectionMatrix, 0, mWorld.mViewMatrix, 0);

        GLES20.glUniformMatrix4fv(mShader.mParams.get("u_Matrix"), 1, false, mat, 0);

        mGaveWorld.draw(mShader);
    }
}
