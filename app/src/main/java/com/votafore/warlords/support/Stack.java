package com.votafore.warlords.support;

import android.util.Log;

/**
 * класс для стека сообщений (больше похоже на очередь чем на стек)
 * (собирает сообщения из рахных потоков в список
 * и позволяет последовательно их взять из стека)
 */

public class Stack {

    /**
     * здесь будет:
     * - массив для данных
     * - переменная хранящая указатель (индекс) элемента который будет взят следующим
     * - переменная хранящая указатель (индекс) элемента в который будет помещено значение
     */

    private String[]    mStack;
    private int         mPutIndex;
    private int         mGetIndex;
    private int         mCapacity;

    private final Object mStackLock = new Object();

    public Stack(){
        this(100);
    }

    public Stack(int capacity){

        Log.v("STACK", "создали стек (" + String.valueOf(capacity) + ")");

        mCapacity = capacity;

        mStack = new String[mCapacity];

        for (int i = 0; i < mCapacity; i++) {
            mStack[i] = "";
        }

        mPutIndex = 0;
        mGetIndex = 0;
    }





    public void put(String msg){

        // мы не можем добавить сообщение если нет свободных мест
        if(mPutIndex >= (mGetIndex + mCapacity)){
            return;

            // возможно стоит доработать стек что бы если он переполняется, то
            // создается новый (побольше) массив что бы сообщения вмещались
        }

        synchronized (mStackLock) {
            if (mStack[mPutIndex % mCapacity].isEmpty())
                mStack[mPutIndex % mCapacity] = msg;

            mPutIndex++;
        }
    }

    public String get(){

        String result = "";

        // мы не можем взять из стека если мы уже все взяли
        if(mGetIndex == mPutIndex){

            // что бы счетчики не доходили до запредельных числел
            // будем их сбрасывать как только они догоняют друг друга

            // как вариант это можно сделать и по достижении счетчиков определенного числа

            if(mGetIndex > mCapacity){

                mPutIndex = mPutIndex % mCapacity;
                mGetIndex = mGetIndex % mCapacity;

                Log.v("STACK", String.format("Сброс счетчиков. mPutIndex: %d; mGetIndex: %d", mPutIndex, mGetIndex));
            }

            return result;
        }

        synchronized (mStackLock) {
            result = mStack[mGetIndex % mCapacity];
            mStack[mGetIndex % mCapacity] = "";

            mGetIndex++;
        }

        return result;
    }

    public boolean hasNext(){

        boolean result;

        synchronized (mStackLock){
            result = !mStack[mGetIndex % mCapacity].isEmpty();
        }

        return result;
    }


    public int size(){
        return mPutIndex - mGetIndex;
    }
}
