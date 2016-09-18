package com.votafore.warlords.glsupport;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.votafore.warlords.Constants;

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

    /**
     * Cодержит такие же методы как и в интерфейсе Renderer.
     * Jни и вызываются в этих методах. Реализация этого интерфейса (класс) делает все
     * необходимые установки и настройки рендерера и других объектов, ответственных за
     * отображение объектов.
     */
    public interface ICallback{
        void onSurfaceCreated();
        void onSurfaceChanged(int width, int height);
        void onDrawFrame();
    }

    private ICallback    mCallbackHandler;

    public GLRenderer(ICallback callback) {

        mCallbackHandler    = callback;
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {

        float[] mBaseColor = Constants.base_color;

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearColor(mBaseColor[0],mBaseColor[1],mBaseColor[2],mBaseColor[3]);

        mCallbackHandler.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        glViewport(0, 0, width, height);

        mCallbackHandler.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 arg0) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mCallbackHandler.onDrawFrame();
    }
}
