package com.votafore.warlords;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.votafore.warlords.glsupport.*;
import com.votafore.warlords.glsupport.GLView;

/**
 * @author Votafore
 * Created on 12.09.2016.
 */
public class MotionHandler {

    private Context mContext;

    private GLView.ICamera mCamera;


    // обрабатываем всего 2 нажатия одновременно
    // - управление перемещением
    // - управление направлением камеры

    private int mPositionHandler;
    private int mOrientationHandler;



    // хранят последние обработанные значения для позиции
    private float positionX;
    private float positionY;

    // хранят последние обработанные значения для ориентации
    private float orientationX;
    private float orientationY;




    private int mWidth;
    private int mHeight;

    public MotionHandler(Context context, GLView.ICamera camera) {

        mContext    = context;
        mCamera     = camera;

        mWidth = 0;
        mHeight = 0;

        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        Point p = new Point();
        display.getSize(p);

        mWidth  = p.x;
        mHeight = p.y;

        mPositionHandler = -1;
        mOrientationHandler = -1;
    }

    protected boolean onHandleEvent(MotionEvent event) {

        switch(MotionEventCompat.getActionMasked(event)){
            case MotionEvent.ACTION_DOWN:
                setID(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                setID(event);
                break;
            case MotionEvent.ACTION_UP:
                releaseID(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                releaseID(event);
                break;
            case MotionEvent.ACTION_MOVE:

                for (int i = 0; i < event.getPointerCount(); i++) {

                    float deltaX;
                    float deltaY;

                    if (event.getPointerId(i) == mPositionHandler) {

                        deltaX = event.getX(i) - positionX;
                        deltaY = positionY - event.getY(i);

                        Log.v("TEST","pointer:"+String.valueOf(i)
                                +" historyX: "+String.valueOf(positionX)
                                +" X: "+String.valueOf(event.getX(i))
                                +" deltaX: " + String.valueOf(deltaX));

                        Log.v("TEST","pointer:"+String.valueOf(i)
                                +" historyY: "+String.valueOf(positionY)
                                +" Y: "+String.valueOf(event.getY(i))
                                +" deltaY: " + String.valueOf(deltaY));

                        Log.v("TEST", "delta position:");

                        positionX = event.getX(i);
                        positionY = event.getY(i);

                        mCamera.camMove(GLWorld.AXIS_X, deltaX / 30);
                        mCamera.camMove(GLWorld.AXIS_Z, deltaY / 30);
                    }

                    if (event.getPointerId(i) == mOrientationHandler) {

                        deltaX = orientationX - event.getX(i);
                        deltaY = orientationY - event.getY(i);

                        Log.v("TEST","pointer:"+String.valueOf(i)
                                +" historyX: "+String.valueOf(orientationX)
                                +" X: "+String.valueOf(event.getX(i))
                                +" deltaX: " + String.valueOf(deltaX));

                        Log.v("TEST","pointer:"+String.valueOf(i)
                                +" historyY: "+String.valueOf(orientationY)
                                +" Y: "+String.valueOf(event.getY(i))
                                +" deltaY: " + String.valueOf(deltaY));

                        Log.v("TEST", "delta orientation:");

                        orientationX = event.getX(i);
                        orientationY = event.getY(i);

                        // при вождении по горизонтали надо вращать по оси Y
                        mCamera.camRotate(GLWorld.AXIS_Y, deltaX / 5);

                        // при вождении по вертикали - ось X
                        mCamera.camRotate(GLWorld.AXIS_X, deltaY / 5);

                    }
                }

        }

        return true;
    }


    /**
     * определяем область касания и устанавливаем соответствующий
     * ИД для управления
     * @param event
     */
    private void setID(MotionEvent event){

        Log.v("TEST","x: " + String.valueOf(event.getX(event.getActionIndex())));

        Log.v("TEST","установка ИДа");

        if(event.getX(event.getActionIndex()) > mWidth/2){
            // это область управления ориентацией
            if(mOrientationHandler == -1){

                // т.к. переменная не занята, то это первое событие
                // к которому надо "привязаться"
                mOrientationHandler = event.getPointerId(event.getActionIndex());

                orientationX = event.getX(event.getActionIndex());
                orientationY = event.getY(event.getActionIndex());

                Log.v("TEST","установили ИД для управления ориентацией (" + String.valueOf(mOrientationHandler) + ")");
            }
        }else{

            // это область управления положением
            // проверим есть ли касание для управления положением
            if(mPositionHandler == -1){

                // нет "управляющего" касания
                mPositionHandler = event.getPointerId(event.getActionIndex());

                positionX = event.getX(event.getActionIndex());
                positionY = event.getY(event.getActionIndex());

                Log.v("TEST","установили ИД для управления положением (" + String.valueOf(mPositionHandler) + ")");
            }
        }
    }

    /**
     * при убирании польца надо проверить: вдруг пора сбросить ИД
     * @param event
     */
    private void releaseID(MotionEvent event){

        Log.v("TEST","пробуем сбросить ИД");

        if(event.getPointerId(event.getActionIndex()) == mPositionHandler && mPositionHandler != -1){

            mPositionHandler = -1;
            Log.v("TEST","ИД управления положением сброшен");
        }

        if(event.getPointerId(event.getActionIndex()) == mOrientationHandler && mOrientationHandler != -1){

            mOrientationHandler = -1;
            Log.v("TEST","ИД управления ориентацией сброшен");
        }
    }
}
