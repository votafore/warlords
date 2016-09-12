package com.votafore.warlords.glsupport;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.IntDef;

import java.util.ArrayList;
import java.util.List;

public class GLWorld implements GLRenderer.ICallback, GLView.ICamera {

    private static GLWorld mThis;

    private Context mContext;

    private GLWorld(Context mContext) {
        this.mContext = mContext;

        mPositionMatrix     = new float[16];
        Matrix.setIdentityM(mPositionMatrix, 0);

        mOrientationMatrix     = new float[16];
        Matrix.setIdentityM(mOrientationMatrix, 0);

        mObjects = new ArrayList<>();

        attachObjects();

        initCamera();
    }

    public static GLWorld getInstance(Context context){

        if(mThis == null)
            mThis = new GLWorld(context);

        return mThis;
    }


    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    // РАЗДЕЛ НАСТРОЕК ПРОСТРАНСТВА GLSpace
    // матрицы вида, проекции, базовый цвет
    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////

    /**
     * mBaseColor - базовый цвет
     * в который очищается пространство при создании рундерера
     */
    private float[] mBaseColor = new float[]{0.5f, 0.5f, 0.5f, 1f};

    /**
     * mProjectionMatrix - матрица проекции.
     */
    public float[] mProjectionMatrix   = new float[16];

    /**
     * mViewMatrix - матрица вида
     * изменяется из потока пользователя (управление камерой)
     * читается в потоке рендерера
     */
    public volatile float[] mViewMatrix   = new float[16];



    public void setBaseColor(float[] baseColor) {

        for (int i = 0; i < baseColor.length; i++) {
            mBaseColor[i] = baseColor[i];
        }
    }



    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    // РАЗДЕЛ ОБРАБОТКИ СОБЫТИЙ РЕНДЕРЕРА
    // GLRender.ICallback
    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////


    // реализация интерфейса
    @Override
    public void onSurfaceCreated(){

        GLES20.glClearColor(mBaseColor[0],mBaseColor[1],mBaseColor[2],mBaseColor[3]);

        // подготовка матрицы вида
        positionChanged();
    }

    @Override
    public void onSurfaceChanged(int width, int height){

        float left      = -mWidth/2;
        float right     =  mWidth/2;
        float bottom    = -mHeight/2;
        float top       =  mHeight/2;
        float near      =  mNear;
        float far       =  mFar;

        float ratio = (float) height / width;
        bottom  *= ratio;
        top     *= ratio;

        if (width > height) {

            bottom  = -mHeight/2;
            top     =  mHeight/2;

            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        }

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GLShader shader) {

        for (GLUnit unit : mObjects) {

            unit.draw(shader);
        }
    }

    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    // РАЗДЕЛ НАСТРОЕК КАМЕРЫ
    // типа GLCamera
    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////

    // начальное положение камеры - всегда в начале системы координат
    public static final float[] position_vec = new float[]{0f,0f,0f,1f};
    // направление камеры всегда паралельно оси Z (в сторону уменьшения)
    public static final float[] lookAt_vec = new float[]{0f,0f,-1f,1f};
    // камера выровняна по оси Y (т.е. она никак не наклонена\повернута)
    public static final float[] orientation_vec = new float[]{0f,1f,0f,1f};

    @IntDef({AXIS_X, AXIS_Y, AXIS_Z})
    public @interface Axis{}
    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 2;

    // определения области видимости камеры
    public float mWidth   = 0.05f;
    public float mHeight  = 0.05f;
    public float mNear    = 0.1f;
    public float mFar     = 15f;


    // определяет текущее положение камеры в пространстве
    public static float[] mPositionMatrix;

    // определяет направление "взгляда" камеры
    public static float[] mOrientationMatrix;



    private void initCamera(){

        setScope(0.055f, 0.055f, 0.1f, 25f);

        camMove(GLWorld.AXIS_Y, 1f);
        camMove(GLWorld.AXIS_Z, 3f);
    }

    @Override
    public void setScope(float width, float height, float near, float far){

        mWidth      = width;
        mHeight     = height;
        mNear       = near;
        mFar        = far;
    }

    @Override
    public void camMove(@Axis int axe, float value) {

        switch (axe){
            case AXIS_X:
                Matrix.translateM(mPositionMatrix, 0, value, 0f, 0f);
                break;
            case AXIS_Y:
                Matrix.translateM(mPositionMatrix, 0, 0f, value, 0f);
                break;
            case AXIS_Z:
                Matrix.translateM(mPositionMatrix, 0, 0f, 0f, value);
        }

        positionChanged();
    }

    @Override
    public void camRotate(@Axis int axe, float value) {

        switch (axe){
            case AXIS_X:
                Matrix.rotateM(mOrientationMatrix, 0, value, 1f, 0f, 0f);
                break;
            case AXIS_Y:
                Matrix.rotateM(mPositionMatrix, 0, value, 0f, 1f, 0f);
                break;
            case AXIS_Z:
                Matrix.rotateM(mPositionMatrix, 0, value, 0f, 0f, 1f);
        }

        positionChanged();
    }



    // служебные переменные
    private float[] tmpMatrix = new float[16];

    public void positionChanged(){

        float[] position    = new float[4];
        float[] lookAt      = new float[4];
        float[] orientation = orientation_vec;

        Matrix.multiplyMV(position, 0, mPositionMatrix, 0, position_vec, 0);

        Matrix.setIdentityM(tmpMatrix, 0);
        Matrix.multiplyMM(tmpMatrix, 0, mPositionMatrix, 0, mOrientationMatrix, 0);

        Matrix.multiplyMV(lookAt  , 0, tmpMatrix, 0, lookAt_vec, 0);

        Matrix.setLookAtM(mViewMatrix, 0,
                position[0]     , position[1]   , position[2],
                lookAt[0]       , lookAt[1]     , lookAt[2],
                orientation[0]  , orientation[1], orientation[2]);

    }



    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    // РАЗДЕЛ УПРАВЛЕНИЯ СЦЕНОЙ
    // типа GLScene
    // список отображаемых объектов, передача вызовов подготовки и отрисовки
    // объектам
    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////

    public List<GLUnit> mObjects;

    private void attachObjects(){

        // позже тут будет фоновая загрузка данных объектов

        // загрузка объектов сцены
        // пока что тестовый вариант

//        MeshUnit unit = new MeshUnit(mContext);
//        unit.init();
//
//        mObjects.add(unit);
    }


    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    // РАЗДЕЛ ВСЯЧИНА
    // будем хранить ссылку на рендерер т.к. он будет существовать пока
    // существует объект GLWorld
    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////

    public GLRenderer mRenderer;

    public GLRenderer getRenderer() {
        return mRenderer;
    }

    public void setRenderer(GLRenderer mRenderer) {
        this.mRenderer = mRenderer;
    }


}
