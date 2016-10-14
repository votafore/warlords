package com.votafore.warlords.game;

import android.content.Context;
import android.util.Log;

import com.votafore.warlords.net.IConnection;

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



    @Override
    public void execute(IConnection connection, String command) {

        // принимаем и обрабатываем данные от сервера
        Log.v(GameManager.TAG, "Instance: execute(). получили команду от сервера");
    }

    public void someFunc(){

        //Log.v(GameManager.TAG, "Instance: someFunc(). посылаем тестовую команду");

        //mConnectionManager2.sendCommand("test command");

        mChanel.sendCommand("test command");
    }
}