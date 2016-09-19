package com.votafore.warlords;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.votafore.warlords.glutil.ObjectContainer;
import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLUnit;
import com.votafore.warlords.glsupport.GLWorld;

import java.io.IOException;


public class MeshUnit extends GLUnit {

    private ObjectContainer mContainer;

    public MeshUnit(Context mContext) {
        super(mContext);
    }

    @Override
    public void init() {

        mContainer = new ObjectContainer();

        try {
            mContainer.loadFile(mContext.getResources().openRawResource(R.raw.mymap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(GLShader shader) {

        int location_a_Position      = shader.mParams.get("a_position");
        int location_a_Normal        = shader.mParams.get("a_normal");

        //int location_u_Camera        = shader.mParams.get("u_camera");


//        float[] tmpCamPosition = new float[4];
//        System.arraycopy(GLWorld.position_vec, 0, tmpCamPosition, 0,4);
//
//        Matrix.multiplyMV(tmpCamPosition, 0, GLWorld.mPositionMatrix, 0, tmpCamPosition, 0);
//
//        GLES20.glUniform3f(location_u_Camera        , tmpCamPosition[0], tmpCamPosition[1], tmpCamPosition[2]);

        for (int i = 0; i < mContainer.list.size(); i++) {

            ObjectContainer.Model model = mContainer.list.get(i);

            model.v.position(0);
            GLES20.glVertexAttribPointer(location_a_Position, 3, GLES20.GL_FLOAT, false, 0, model.v);
            GLES20.glEnableVertexAttribArray(location_a_Position);


            model.vn.position(0);
            GLES20.glVertexAttribPointer(location_a_Normal, 3, GLES20.GL_FLOAT, false, 0, model.vn);
            GLES20.glEnableVertexAttribArray(location_a_Normal);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, model.faces.size()*3);
        }
    }
}
