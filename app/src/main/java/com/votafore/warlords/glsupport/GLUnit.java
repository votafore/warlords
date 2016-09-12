package com.votafore.warlords.glsupport;

import android.content.Context;

import java.nio.FloatBuffer;

/**
 * Created by admin on 06.09.2016.
 */
public abstract class GLUnit {

    protected Context mContext;

    public FloatBuffer buffer;

    public float[] mVertices;

    public int textureSlot;
    public int textureTarget;
    public int texture;
    public int textureResID;

    public GLUnit(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * реализация метода должна:
     * - создать массив вершин текущего объекта
     * будет ли эта информация загружаться из файлов, баз данных
     * или прописываться в конструкторе объекта - не важно
     * - загрузить любую другую необходимую информацию (текстуры, цвета вершин и т.д.)
     */
    public abstract void init();

    /**
     *
     * @param shader
     */
    public abstract void draw(GLShader shader);
}
