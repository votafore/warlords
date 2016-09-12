package com.votafore.warlords.glsupport;

import android.content.Context;
import android.opengl.GLSurfaceView;


/**
 * Created by admin on 11.09.2016.
 */
public abstract class GLView extends GLSurfaceView {

//    public interface IMotionEventReceiver{
//        void camMove(@GLWorld.Axis int axe, float value);
//        void camRotate(@GLWorld.Axis int axe, float value);
//    }

    public interface ICamera{
        void setScope(float width, float height, float near, float far);
        void camMove(@GLWorld.Axis int axe, float value);
        void camRotate(@GLWorld.Axis int axe, float value);
    }

    protected Context mContext;

    protected GLWorld mWorld;
    protected ICamera mCamera;

    protected int resVertexShader;
    protected int resFragmentShader;

    public GLView(final Context context) {
        super(context);

        mContext = context;

        setEGLContextClientVersion(2);

        setShaderSources();

        mWorld = GLWorld.getInstance(mContext);

        GLRenderer renderer = mWorld.getRenderer();

        if(renderer == null){
            renderer = new GLRenderer(context, resVertexShader, resFragmentShader);
            mWorld.setRenderer(renderer);
        }

        // устанавливаем загрузчик шейдера
        // и получение его параметров
        renderer.getShader().setShaderCreator(getShaderCreator());

        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        mCamera = (ICamera)mWorld;

        setWorldParams();
    }



    /**
     * в методе задается реализация интерфейса, которая создает
     * программу шейдера
     * и заполняет список параметров
     * @return
     */
    protected abstract GLShader.IGLShaderCreator getShaderCreator();

    /**
     * метод, в котором присваиваются значения переменных:
     * - resVertexShader
     * - resFragmentShader
     */
    protected abstract void setShaderSources();

    /**
     * метод для установки первоначальных параметров мира
     */
    protected abstract void setWorldParams();
}
