//package com.votafore.warlords.v3.glsupport;
//
//import android.content.Context;
//import android.opengl.GLES20;
//
//import com.votafore.warlords.glutil.ShaderUtils;
//
//import java.util.HashMap;
//
//
///**
// * @author Votafore
// * Created on 05.09.2016.
// */
//public class GLShader {
//
//    /**
//     * интерфейс для изменяемой части механизма создания
//     * и настройки программы (создаваемой из шейдеров). Имеет всего
//     * 1 метод для получения списка параметров: populateParameters(int, HashMap)
//     * @see #populateParameters(int, HashMap)
//     */
//    public interface IGLShaderCreator{
//
//        /**
//         * Метод, в котором будет заполнен список параметров шейдеров
//         * @param programID (int) ID программы созданной из шейдеров
//         *                  из которой надо получить ссылки на параметры
//         * @param params (HashMap<String, Integer>) список параметров шейдеров.
//         *               Заполняется как-то вроде:<br>
//         *               params.put("ParamName", GLES20.glGetUniformLocation(programID, "ParamName"));
//         *               <br>для каждого параметра.
//         */
//        void populateParameters(int programID, HashMap<String, Integer> params);
//    }
//
//    private Context mContext;
//
//    /**
//     * ID программы, которая создается из шейдеров
//     */
//    public int programID;
//
//    /**
//     * ссылка на объект, реализующий интерфейс IGLShaderCreator
//     * @see IGLShaderCreator
//     */
//    private IGLShaderCreator shaderCreator;
//
//    /**
//     * ссылка на ресурс вершинного шейдера
//     */
//    private int resVertexShader;
//
//    /**
//     * ссылка на ресурс фрагментарного шейдера
//     */
//    private int resFragmentShader;
//
//    /**
//     * список, хранящий параметры шейдеров
//     * и благодаря ему обеспечивается доступ к ним по имени
//     * (точнее по ключу)
//     */
//    public HashMap<String, Integer> mParams;
//
//    public GLShader(Context context, int resShaderVertex, int resShaderFragment){
//
//        mContext            = context;
//        mParams             = new HashMap<>();
//
//        resVertexShader     = resShaderVertex;
//        resFragmentShader   = resShaderFragment;
//    }
//
//    public void createShader(){
//
//        int vertexShaderId      = ShaderUtils.createShader(mContext, GLES20.GL_VERTEX_SHADER   , resVertexShader);
//        int fragmentShaderId    = ShaderUtils.createShader(mContext, GLES20.GL_FRAGMENT_SHADER , resFragmentShader);
//
//        programID = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
//
//        GLES20.glUseProgram(programID);
//
//        mParams.put("u_Matrix"       , GLES20.glGetUniformLocation(programID, "u_Matrix"));
//        mParams.put("u_color"        , GLES20.glGetUniformLocation(programID, "u_color"));
//
//        mParams.put("a_position"     , GLES20.glGetAttribLocation(programID, "a_position"));
//        mParams.put("a_normal"       , GLES20.glGetAttribLocation(programID, "a_normal"));
//
//        // параметры fragment шейдера
//        mParams.put("u_camera"       , GLES20.glGetUniformLocation(programID, "u_camera"));
//        mParams.put("u_lightPosition", GLES20.glGetUniformLocation(programID, "u_lightPosition"));
//    }
//}
