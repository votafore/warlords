package com.votafore.warlords.glsupport;


import android.opengl.Matrix;
import android.support.annotation.IntDef;

import com.votafore.warlords.GameManager;

import java.util.ArrayList;
import java.util.List;

public class GLWorld implements GLView.ICamera {


    private GameManager mManager;

    public GLWorld(GameManager manager) {

        mManager    = manager;

        mPositionMatrix     = new float[16];
        Matrix.setIdentityM(mPositionMatrix, 0);

        mOrientationMatrix     = new float[16];
        Matrix.setIdentityM(mOrientationMatrix, 0);

        mObjects = new ArrayList<>();

        initCamera();
    }

    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    // РАЗДЕЛ НАСТРОЕК ПРОСТРАНСТВА GLSpace
    // матрицы вида, проекции, базовый цвет
    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////

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

        setScope(0.055f, 0.055f, 0.1f, 75f);

        camMove(GLWorld.AXIS_Y, 1f);
        camMove(GLWorld.AXIS_Z, 5f);
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

    public volatile List<GLUnit> mObjects;


    public void attachObject(GLUnit unit){
        mObjects.add(unit);
    }

    public void deleteObject(GLUnit unit){
        mObjects.remove(unit);
    }

}
