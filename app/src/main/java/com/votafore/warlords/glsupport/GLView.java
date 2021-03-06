package com.votafore.warlords.glsupport;

import android.content.Context;
import android.opengl.GLSurfaceView;


/**
 * @author Votafore
 * Created on 11.09.2016.
 */

/**
 * С помьщью этого класса создан "скелет". Тут содержится код,
 * который уже не будет меняться. Весь изменяемый код
 * вынесен в отдельный метод init.
 * Там происходит установка всех "особенных" параметров.
 * @see #init()
 */

public abstract class GLView extends GLSurfaceView {

    /**
     * интерфейс управления камерой.
     * Его реализует класс GLWorld т.к. имеет необходимые
     * объекты для формирования матрицы вида
     * @see com.votafore.warlords.glsupport.GLWorld
     */
    public interface ICamera{

        /**
         * реализация метода устанавливает параметры, которые берутся для
         * формирования матрицы проекции
         */
        void setScope(float width, float height, float near, float far);

        /**
         * реализация метода "передвигает" камеру в пространстве
         */
        void camMove(@GLWorld.Axis int axe, float value);

        /**
         * реализация метода "поворачивает" камеру в пространстве
         */
        void camRotate(@GLWorld.Axis int axe, float value);
    }

    protected Context mContext;

    protected GLWorld mWorld;

    /**
     * объект, реализующий интерфейс ICamera
     * @see ICamera
     */
    protected ICamera mCamera;

    /**
     * <b>resVertexShader</b> - ссылка на ресурс вершинного шейдера.
     * <br>Предполагается что он будет находится в каталоге ресурсов raw
     * в виде файла *.glsl
     */
    protected int resVertexShader;

    /**
     * <b>resFragmentShader</b> - ссылка на ресурс фрагментарного шейдера.
     * <br>Предполагается что он будет находится в каталоге ресурсов raw
     * в виде файла *.glsl
     */
    protected int resFragmentShader;

    /**
     * <b>mShaderCreator</b> - объект, реализующий интерфейс <b>GLShader.IGLShaderCreator</b>.
     * Является ли он экземпляром класса или это анонимный объект - не важно
     * @see com.votafore.warlords.glsupport.GLShader.IGLShaderCreator
     */
    protected GLShader.IGLShaderCreator mShaderCreator;

    public GLView(final Context context) {
        super(context);

        mContext = context;

        setEGLContextClientVersion(2);

        mWorld = GLWorld.getInstance(mContext);

        init();

        GLRenderer renderer = mWorld.getRenderer();

        if(renderer == null){
            renderer = new GLRenderer(context, resVertexShader, resFragmentShader);
            mWorld.setRenderer(renderer);
        }

        // устанавливаем загрузчик шейдера
        // и получение его параметров
        renderer.getShader().setShaderCreator(mShaderCreator);

        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        mCamera = (ICamera)mWorld;
    }


    /**
     * в методе инициализируются переменные необходимые для дальнейшей
     * работы программы.<br>
     * <b>Обязательно должны быть инициализированы переменные:</b>
     * <ul>
     * <li>resVertexShader
     * <li>resFragmentShader
     * <li>mShaderCreator
     * </ul> <br>
     * через переменную mWorld можно получить доступ к другим настройкам, но это не обязательная часть.
     * @see #resVertexShader
     * @see #resFragmentShader
     * @see com.votafore.warlords.glsupport.GLShader.IGLShaderCreator
     */
    protected abstract void init();
}
