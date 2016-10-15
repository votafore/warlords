package com.votafore.warlords.game;

import android.content.Context;
import android.util.Log;

import com.votafore.warlords.GameFactory;
import com.votafore.warlords.glsupport.GLUnit;
import com.votafore.warlords.glsupport.GLView;
import com.votafore.warlords.glsupport.GLWorld;
import com.votafore.warlords.net.IConnection;
import com.votafore.warlords.test.ConnectionChanel2;
import com.votafore.warlords.test.MeshUnit;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Votafore
 * Created on 17.09.2016.
 *
 * Это клиентская часть игры. Хранит объекты для отрисовки
 * способен общаться с сервером (точнее с представителем):
 * отправлять сообщения серверу и принимать их от него.
 *
 *
 *      Клиент (каждый клиент) хранит полный список объектов и всю информацию по каждому объекту
 *      т.к. это необходимо для отрисовки объекта(ов) на клиенте.
 *      Сервер содержит весь список объектов и измененные параметры объектов для
 *      передачи остальным клиентам (синхронизации).
 *      Т.е. списки отличаются содержанием информации.
 *
 * - иметь информацию об игроках инстанса (пока не известно в каком виде)
 *      уже начинаю думать что это не обязательно (пока достаточно только списка объектов)
 * - иметь информацию об объектах инстанса
 *      - для отрисовки (что построено\создано игроками)
 *      - чьи это объекты
 * - иметь механизм изменения параметров объектов сцены
 *      управляемый другими игроками (при многопользовательской игре)
 *      управляемый текущим игроком
 *
 * - реагировать на события управления (команды, onTouch и т.д.)
 *
 * Именно этот класс будет основным в треьтем Активити. GameManager передаст
 * его и в процессе игры (далее) будет учавствовать только этот он.
 */

public class Instance extends EndPoint {

    private Context mContext;

    public Instance(Context context){
        super();

        mContext = context;
        //Log.v(GameManager.TAG, "Instance");
    }



    /**************************************************************************************************/
    /************************************************* end point **************************************/
    /**************************************************************************************************/


    @Override
    public void execute(IConnection connection, String command) {

        // принимаем и обрабатываем данные от сервера
        //Log.v(GameFactory.TAG, "Instance: execute(). получили команду от сервера: " + command);

        try {
            JSONObject obj = new JSONObject(command);

            switch(obj.getString("command")){
                case "camMove":

                    mCamera.camMove(GLWorld.AXIS_X, Float.valueOf(obj.getString("deltaX")));
                    mCamera.camMove(GLWorld.AXIS_Z, Float.valueOf(obj.getString("deltaY")));

                    break;
                case "camRotate":

                    mCamera.camRotate(GLWorld.AXIS_Y, -Float.valueOf(obj.getString("deltaX")));
                    mCamera.camRotate(GLWorld.AXIS_X,  Float.valueOf(obj.getString("deltaY")));

                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void someFunc(){

        //Log.v(GameFactory.TAG, "Instance: someFunc(). посылаем тестовую команду");

        //mConnectionManager2.sendCommand("test command");

        mChanel.sendCommand("test command");
    }

    @Override
    public void stop(){

        mWorkerThread.quit();

        ((ConnectionChanel2)mChanel).clearObservers();
        ((ConnectionChanel2)mChanel).close();
    }


    /**************************************************************************************************/
    /************************************************* Instance  **************************************/
    /**************************************************************************************************/

    /************************** еще в разработке *********/

    public  GLUnit mBase;
    private GLUnit mMap;

    public void setMap(GLUnit map){
        mMap = map;
    }

    public GLUnit getMap(){
        return mMap;
    }

    public void start(){

        mBase = new MeshUnit(mContext);
        mBase.init();

        mMap.init();
    }



    private GLView.ICamera mCamera;

    public void setCamera(GLView.ICamera camera){
        mCamera = camera;
    }
}