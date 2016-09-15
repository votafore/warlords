package com.votafore.warlords;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.votafore.warlords.glsupport.GLShader;
import com.votafore.warlords.glsupport.GLUnit;
import com.votafore.warlords.glsupport.GLWorld;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @author Votafore
 * Created on 13.09.2016.
 */
public class MeshMapTest2 extends GLUnit{

    public MeshMapTest2(Context mContext) {
        super(mContext);
    }

    FloatBuffer[] vertexBuffers;
    float[][]     vertices;

    FloatBuffer[] normalBuffers;
    float[][]     normals;

    @Override
    public void init() {

        // читаем карту высот
        Bitmap bit_map = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.testmap);

        int middleColor = Color.blue(Color.parseColor("#00007f"));

        int[][] pixel_arr = new int[bit_map.getHeight()][bit_map.getWidth()];

        int bm_width  = bit_map.getWidth();
        int bm_height = bit_map.getHeight();

        bm_width  = 50;
        bm_height = 50;

        for (int row = 0; row < bm_height; row++) {
            for (int col = 0; col < bm_width; col++) {

                // т.к. изображение чернобелое, то содержание цветов будет одинаковым
                // для определения "количества" цвета берем любую составляющую

                pixel_arr[row][col] = Color.blue(bit_map.getPixel(row, col)) - middleColor;
                pixel_arr[row][col] = pixel_arr[row][col] / 25;
            }
        }

        bit_map.recycle();

        // создаем массив вершин
        float step    = 1.0f;
        float delitel = 2f;

        float width   = step * bm_width;
        float height  = step * bm_height;



        // создаем буферы
        vertexBuffers = new FloatBuffer[bm_height-1];
        vertices      = new float[bm_height-1]
                [
                (bm_width-1) * 2 // количество треугольников в ряду
                        * 3      // количество вершин для треугольника
                        * 3      // количество координат на вершину
                ];

        normalBuffers = new FloatBuffer[bm_height - 1];
        normals       = new float[bm_height-1][(bm_width-1) * 2 * 3];

        // служебные (временые) переменные
        float[] vertex1;
        float[] vertex2;
        float[] vertex3;

        float[] curNormal;

        for (int row = 0; row < bm_height-1; row++) {

            for (int col = 0; col < bm_width-1; col++) {

                int pos = col * 18;

                int add = 0;

                // координаты вершин треугольника

                // 0-0
                vertices[row][pos+add] = col                   * step - width/2;
                ++add;
                vertices[row][pos+add] = pixel_arr[row][col]   * (step/delitel);
                ++add;
                vertices[row][pos+add] = row                   * step - height/2;
                ++add;

                //1-0
                vertices[row][pos+add] = col                   * step - width/2;
                ++add;
                vertices[row][pos+add] = pixel_arr[row+1][col] * (step/delitel);
                ++add;
                vertices[row][pos+add] = (row+1)               * step - height/2;
                ++add;

                // 0-1
                vertices[row][pos+add] = (col+1)               * step - width/2;
                ++add;
                vertices[row][pos+add] = pixel_arr[row][col+1] * (step/delitel);
                ++add;
                vertices[row][pos+add] = row                   * step - height/2;
                ++add;


                // расчет нормалей треугольника
                vertex1 = new float[]{vertices[row][pos]  ,vertices[row][pos+1],vertices[row][pos+2]};
                vertex2 = new float[]{vertices[row][pos+3],vertices[row][pos+4],vertices[row][pos+5]};
                vertex3 = new float[]{vertices[row][pos+6],vertices[row][pos+7],vertices[row][pos+8]};

                curNormal = getNormal(vertex1, vertex2, vertex3);

                normals[row][col*6]   = curNormal[0];
                normals[row][col*6+1] = curNormal[1];
                normals[row][col*6+2] = curNormal[2];





                // координаты вершин 2-го треугольника

                // 0-1
                vertices[row][pos+add] = (col+1)                 * step - width/2;
                ++add;
                vertices[row][pos+add] = pixel_arr[row][col+1]   * (step/delitel);
                ++add;
                vertices[row][pos+add] = row                     * step - height/2;
                ++add;

                // 1-0
                vertices[row][pos+add]  = col                     * step - width/2;
                ++add;
                vertices[row][pos+add] = pixel_arr[row+1][col]   * (step/delitel);
                ++add;
                vertices[row][pos+add] = (row+1)                 * step - height/2;
                ++add;

                // 1-1
                vertices[row][pos+add] = (col+1)                 * step - width/2;
                ++add;
                vertices[row][pos+add] = pixel_arr[row+1][col+1] * (step/delitel);
                ++add;
                vertices[row][pos+add] = (row+1)                 * step - height/2;
                ++add;


                // расчет нормалей треугольника
                vertex1 = new float[]{vertices[row][pos+9] ,vertices[row][pos+10],vertices[row][pos+11]};
                vertex2 = new float[]{vertices[row][pos+12],vertices[row][pos+13],vertices[row][pos+14]};
                vertex3 = new float[]{vertices[row][pos+15],vertices[row][pos+16],vertices[row][pos+17]};

                curNormal = getNormal(vertex1, vertex2, vertex3);

                normals[row][col*6+3] = curNormal[0];
                normals[row][col*6+4] = curNormal[1];
                normals[row][col*6+5] = curNormal[2];
            }

            vertexBuffers[row] = ByteBuffer.allocateDirect(vertices[row].length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

            vertexBuffers[row].put(vertices[row]);

            normalBuffers[row] = ByteBuffer.allocateDirect(normals[row].length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

            normalBuffers[row].put(normals[row]);
        }
    }

    private float[] getNormal(float[] v1, float[] v2, float[] v3){

        float[] edge1;
        float[] edge2;

        edge1 = new float[]{v2[0] - v1[0],v2[1] - v1[1],v2[2] - v1[2]};
        edge2 = new float[]{v3[0] - v1[0],v3[1] - v1[1],v3[2] - v1[2]};

        float[] normal = new float[]{
                edge1[1] * edge2[2] - edge1[2] * edge2[1],
                edge1[2] * edge2[0] - edge1[0] * edge2[2],
                edge1[0] * edge2[1] - edge1[1] * edge2[0]
        };

        // нормализую длину
        float d = (float) Math.sqrt(normal[0]*normal[0] + normal[1]*normal[1] + normal[2]*normal[2]);

        normal[0] = normal[0] / d;
        normal[1] = normal[1] / d;
        normal[2] = normal[2] / d;

        return normal;
    }

    @Override
    public void draw(GLShader shader) {

        int location_a_Position      = shader.mParams.get("a_position");
        int location_a_Normal        = shader.mParams.get("a_normal");
        int location_u_Color         = shader.mParams.get("u_color");

        int location_u_Camera        = shader.mParams.get("u_camera");
        int location_u_lightPosition = shader.mParams.get("u_lightPosition");

        GLES20.glUniform4f(location_u_Color, 0f, 0.9f, 0f, 1.0f);

        float[] tmpCamPosition = new float[4];
        System.arraycopy(GLWorld.position_vec, 0, tmpCamPosition, 0,4);

        Matrix.multiplyMV(tmpCamPosition, 0, GLWorld.mPositionMatrix, 0, tmpCamPosition, 0);

        GLES20.glUniform4f(location_u_Camera, tmpCamPosition[0], tmpCamPosition[1], tmpCamPosition[2], 1.0f);
        GLES20.glUniform4f(location_u_lightPosition, 0f, 3f, 6f, 1.0f);

        for (int i = 0; i < vertexBuffers.length; i++) {

            vertexBuffers[i].position(0);
            GLES20.glVertexAttribPointer(location_a_Position, 3, GLES20.GL_FLOAT, false, 0, vertexBuffers[i]);
            GLES20.glEnableVertexAttribArray(location_a_Position);

            normalBuffers[i].position(0);
            GLES20.glVertexAttribPointer(location_a_Normal, 3, GLES20.GL_FLOAT, false, 0, normalBuffers[i]);
            GLES20.glEnableVertexAttribArray(location_a_Normal);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices[i].length/3);
        }
    }
}
