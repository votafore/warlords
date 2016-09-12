package com.votafore.warlords.glsupport;

import android.content.Context;
import android.opengl.GLES20;


import com.votafore.warlords.glutil.ShaderUtils;

import java.util.HashMap;


/**
 * Created by admin on 05.09.2016.
 */
public class GLShader {

    public interface IGLShaderCreator{
        void populateParameters(int programID, HashMap<String, Integer> params);
    }

    private Context mContext;

    public int programID;

    private IGLShaderCreator shaderCreator;

    private int resVertexShader;
    private int resFragmentShader;

    public HashMap<String, Integer> mParams;

    public GLShader(Context context, int resShaderVertex, int resShaderFragment){
        mContext = context;
        mParams = new HashMap<>();

        resVertexShader = resShaderVertex;
        resFragmentShader = resShaderFragment;
    }

    public void setShaderCreator(IGLShaderCreator shaderCreator) {
        this.shaderCreator = shaderCreator;
    }

    public void createShader(){

        int vertexShaderId      = ShaderUtils.createShader(mContext, GLES20.GL_VERTEX_SHADER   , resVertexShader);
        int fragmentShaderId    = ShaderUtils.createShader(mContext, GLES20.GL_FRAGMENT_SHADER , resFragmentShader);

        programID = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);

        GLES20.glUseProgram(programID);

        shaderCreator.populateParameters(programID, mParams);
    }
}
