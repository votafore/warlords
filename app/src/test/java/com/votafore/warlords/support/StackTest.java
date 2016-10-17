package com.votafore.warlords.support;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;



@RunWith(MockitoJUnitRunner.class)
public class StackTest {

    Stack mStack;

    @Before
    public void setUp() throws Exception {

        mStack = new Stack(5);
    }

    @Test
    public void putOneValue(){

        // в начале список пуст
        assertEquals(0, mStack.size());
        assertFalse(mStack.hasNext());

        // добавили одно значение
        mStack.put("val 1");
        assertEquals(1, mStack.size());
        assertTrue(mStack.hasNext());


        // прочитали значение
        assertEquals("val 1", mStack.get());
        assertEquals(0, mStack.size());
        assertFalse(mStack.hasNext());
    }

    @Test
    public void fullStack(){

        // в начале список пуст
        assertEquals(0, mStack.size());
        assertFalse(mStack.hasNext());


        // добавяем значения
        mStack.put("val 1");
        assertEquals(1, mStack.size());

        mStack.put("val 2");
        assertEquals(2, mStack.size());

        mStack.put("val 3");
        assertEquals(3, mStack.size());

        mStack.put("val 4");
        assertEquals(4, mStack.size());

        mStack.put("val 5");
        assertEquals(5, mStack.size());


        // пробуем переполнить стек
        // его размер не должен увеличиться
        // т.е. все сообщения приходящие пока стек заполнен игнорируются
        mStack.put("val 6");
        assertEquals(5, mStack.size());

        mStack.put("val 7");
        assertEquals(5, mStack.size());

        mStack.put("val 8");
        assertEquals(5, mStack.size());


        // читаем значения
        assertEquals("val 1", mStack.get());
        assertEquals(4, mStack.size());

        assertEquals("val 2", mStack.get());
        assertEquals(3, mStack.size());

        assertEquals("val 3", mStack.get());
        assertEquals(2, mStack.size());

        assertEquals("val 4", mStack.get());
        assertEquals(1, mStack.size());

        assertEquals("val 5", mStack.get());
        assertEquals(0, mStack.size());

        assertFalse(mStack.hasNext());
    }

    @Test
    public void noValues(){

        // в начале список пуст
        assertEquals(0, mStack.size());
        assertFalse(mStack.hasNext());

        // пробуем прочитать значение из пустого стека
        // при этом не важно сколько раз мы будем пытаться прочитать значение
        // пока его там нет нам возвращается пустая строка
        assertEquals("", mStack.get());
        assertEquals("", mStack.get());
        assertEquals("", mStack.get());
    }

}