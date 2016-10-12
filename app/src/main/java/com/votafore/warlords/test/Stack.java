package com.votafore.warlords.test;

/**
 * класс для стека сообщений
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

        mCapacity = capacity;

        mStack = new String[mCapacity];

        mPutIndex = 0;
        mGetIndex = 0;
    }





    public void put(String msg){

        synchronized (mStackLock) {
            if (mStack[mPutIndex].isEmpty())
                mStack[mPutIndex] = msg;

            mPutIndex++;
            mPutIndex = mPutIndex % mCapacity;

        }
    }

    public String get(){

        String result;

        synchronized (mStackLock) {
            result = mStack[mGetIndex];
            mStack[mGetIndex] = "";


            mGetIndex++;
            mGetIndex = mGetIndex % mCapacity;

        }

        return result;
    }

    public boolean hasNext(){

        boolean result;

        synchronized (mStackLock){
            result = !mStack[mGetIndex].isEmpty();
        }

        return result;
    }
}
