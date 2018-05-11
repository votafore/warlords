//package com.votafore.warlords.v3.glsupport;
//
//import android.content.Context;
//import android.opengl.GLSurfaceView;
//
//
///**
// * @author Votafore
// * Created on 11.09.2016.
// */
//
///**
// * С помьщью этого класса создан "скелет". Тут содержится код,
// * который уже не будет меняться. Весь изменяемый код
// * вынесен в отдельный метод init.
// * Там происходит установка всех "особенных" параметров.
// * @see #init()
// */
//
//public abstract class GLView extends GLSurfaceView {
//
//    /**
//     * интерфейс управления камерой.
//     * Его реализует класс GLWorld т.к. имеет необходимые
//     * объекты для формирования матрицы вида
//     * @see GLWorld
//     */
//    public interface ICamera{
//
//        /**
//         * реализация метода устанавливает параметры, которые берутся для
//         * формирования матрицы проекции
//         */
//        void setScope(float width, float height, float near, float far);
//
//        /**
//         * реализация метода "передвигает" камеру в пространстве
//         */
//        void camMove(@GLWorld.Axis int axe, float value);
//
//        /**
//         * реализация метода "поворачивает" камеру в пространстве
//         */
//        void camRotate(@GLWorld.Axis int axe, float value);
//
//
//        /*********** TESTS ************/
//
//        /**
//         * реализация метода устанавливает значение с которым "поворачивается" камера при каждом кадре
//         */
//        void setRotationDelta(@GLWorld.Axis int axe, float value);
//
//        /**
//         * реализация метода устанавливает значение с которым "передвигаетcя" камера при каждом кадре
//         */
//        void setMovingDelta(@GLWorld.Axis int axe, float value);
//
//        // TODO: 11.01.2018 доделать методы для установки постоянных значений изменения
//        // т.е. с какой скоростью меняется значение при каждом кадре
//    }
//
//    protected Context mContext;
//
//    protected GLWorld mWorld;
//
//    /**
//     * объект, реализующий интерфейс ICamera
//     * @see ICamera
//     */
//    protected ICamera mCamera;
//
//
//    public GLView(final Context context, GLWorld world, GLRenderer renderer) {
//        super(context);
//
//        mContext    = context;
//        mWorld      = world;
//        mCamera     = mWorld;
//
//        setEGLContextClientVersion(2);
//
//        init();
//
//        setRenderer(renderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//    }
//
//
//    /**
//     * в методе инициализируются переменные необходимые для дальнейшей
//     * работы программы.<br>
//     */
//    protected abstract void init();
//}
