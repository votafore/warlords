package com.votafore.warlords.test;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.votafore.warlords.glsupport.GLView;
import com.votafore.warlords.net.IChanel;
import com.votafore.warlords.support.Queries;


/**
 * @author Votafore
 * Created on 12.09.2016.
 */
public class MotionHandlerJoystick {


    // обрабатываем всего 2 нажатия одновременно
    // - управление перемещением
    // - управление направлением камеры

    private int mPositionHandler;
    private int mOrientationHandler;


    private int mWidth;
    private int mHeight;

    public MotionHandlerJoystick(Context context) {

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

    public boolean onHandleEvent(MotionEvent event) {

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

                        deltaX = event.getX(i) - mWidth/4;
                        deltaY = event.getY(i) - mHeight/2;

                        deltaX = Math.min(deltaX, mWidth/4);

                        deltaX /= 650;
                        deltaY /= 650;

                        mChanel.sendCommand(Queries.getQueryCamMove(deltaX, deltaY));
                    }

                    if (event.getPointerId(i) == mOrientationHandler) {

                        deltaX = event.getX(i) - mWidth/4*3;
                        deltaY = mHeight/2 - event.getY(i);

                        deltaX = Math.max(deltaX, -mWidth/4);

                        deltaX /= 350;
                        deltaY /= 350;

                        mChanel.sendCommand(Queries.getQueryCamRotate(-deltaX, deltaY));

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

                Log.v("TEST","установили ИД для управления ориентацией (" + String.valueOf(mOrientationHandler) + ")");
            }
        }else{

            // это область управления положением
            // проверим есть ли касание для управления положением
            if(mPositionHandler == -1){

                // нет "управляющего" касания
                mPositionHandler = event.getPointerId(event.getActionIndex());

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









    /************************** в разработке *************/

    IChanel mChanel;

    public void setChanel(IChanel chanel){

        mChanel = chanel;
    }
}
