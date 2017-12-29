package com.votafore.warlords.v3.glsupport;

import android.content.Context;

import java.nio.FloatBuffer;

/**
 * @author Votafore
 * Created on 06.09.2016.
 */
public abstract class GLUnit {

    protected Context mContext;

    protected FloatBuffer buffer;

    protected float[] mVertices;

    protected int textureSlot;
    protected int textureTarget;
    protected int texture;
    protected int textureResID;

    public GLUnit(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * реализация метода должна:
     * - подготовить данные вершин текущего объекта (создать массив или сразу буфер).
     * Будет ли эта информация загружаться из файлов, баз данных
     * или прописываться в конструкторе объекта - не важно
     * - загрузить любую другую необходимую информацию (текстуры, цвета вершин и т.д.)
     */
    public abstract void init();

    /**
     * метод, в котором происходит отрисовка объекта
     * @param shader
     */
    public abstract void draw(GLShader shader);
}
