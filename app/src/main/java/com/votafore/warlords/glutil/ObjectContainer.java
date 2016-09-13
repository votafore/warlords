package com.votafore.warlords.glutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * класс, который хранит в себе распарсенное содержимое файла
 */
public class ObjectContainer {

    public List<Model> list;

    public ObjectContainer(){

        list = new ArrayList();
    }

    public void loadFile(InputStream is) throws IOException {

        BufferedReader  reader;
        Model           model;

        // ?
        float[]         point;
        int             index;

        boolean         readFaces = false;

        //-----------------------
        reader = new BufferedReader(new InputStreamReader(is));
        model = new Model();

        while(reader.ready()) {

            String line = reader.readLine();
            if (line == null)
                break;

            StringTokenizer tok = new StringTokenizer(line);
            String cmd = null;
            try {
                cmd = tok.nextToken();
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                continue;
            }

            if (cmd.equals("v")
                    || cmd.equals("vt")
                    || cmd.equals("vn")) {

                // если мы читаем строку с даными и это не face (а только что был face),
                // то скорее всего пора создавать новый объект модели
                if (readFaces) {
                    list.add(model);
                    model = new Model();

                    readFaces = false;
                }


                point = new float[tok.countTokens()];

                index = 0;
                while (tok.hasMoreTokens()) {
                    point[index] = Float.parseFloat(tok.nextToken());
                    index++;
                }

                switch (cmd) {
                    case "v":
                        model.arr_v.add(point);
                        break;
                    case "vt":
                        model.arr_vt.add(point);
                        break;
                    case "vn":
                        model.arr_vn.add(point);
                }

                continue;
            }

            if (cmd.equals("f")) {

                int[][] face = new int[tok.countTokens()][3];

                int count1 = 0;

                while (tok.hasMoreTokens()) {

                    StringTokenizer face_tok = new StringTokenizer(tok.nextToken(), "/");


                    int count2 = 0;

                    face[count1] = new int[face_tok.countTokens()];

                    face[count1][count2] = Integer.parseInt(face_tok.nextToken());
                    count2++;

                    if (face_tok.hasMoreTokens()) {
                        face[count1][count2] = Integer.parseInt(face_tok.nextToken());
                        count2++;
                    }

                    if (face_tok.hasMoreTokens()) {
                        face[count1][count2] = Integer.parseInt(face_tok.nextToken());
                        count2++;
                    }

                    count1++;
                }

                model.faces.add(face);

                readFaces = true;
            }
        }

        list.add(model);


        ////////////////////////////////////////
        // файл прочитан
        // создаем буферы

        for (Model cur_model :list) {

            boolean isTexture = cur_model.arr_vt.size() > 0;
            boolean isNormals = cur_model.arr_vn.size() > 0;

            cur_model.v_size = cur_model.faces.size() * 3;

            // задаем размеры буфера
            cur_model.v  = ByteBuffer.allocateDirect(cur_model.v_size * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

            if(isTexture)
                cur_model.vt = ByteBuffer.allocateDirect(cur_model.v_size * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

            if(isNormals)
                cur_model.vn = ByteBuffer.allocateDirect(cur_model.v_size * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();


            int index_v;
            int index_vt;
            int index_vn;

            for (int i = 0; i < cur_model.faces.size(); i++) {

                // буфер вершин
                int[][] face = cur_model.faces.get(i);

                for (int numVertex = 0; numVertex < face.length; numVertex++) {

                    int[] facevertex = face[numVertex];

                    int counter = 0;

                    index_v = facevertex[counter];
                    if(index_v < 0){

                        cur_model.v.put(cur_model.arr_v.get(-index_v-1)[0]);
                        cur_model.v.put(cur_model.arr_v.get(-index_v-1)[1]);
                        cur_model.v.put(cur_model.arr_v.get(-index_v-1)[2]);

                    }else{

                        cur_model.v.put(getVertexValueByGlobalIndex(index_v,0));
                        cur_model.v.put(getVertexValueByGlobalIndex(index_v,1));
                        cur_model.v.put(getVertexValueByGlobalIndex(index_v,2));

                    }

                    counter++;

                    if(isTexture) {
                        index_vt = facevertex[counter];
                        if(index_vt < 0){

                            cur_model.vt.put(cur_model.arr_vt.get(-index_vt-1)[0]);
                            cur_model.vt.put(cur_model.arr_vt.get(-index_vt-1)[1]);

                        }else{

                            cur_model.vt.put(getTextureValueByGlobalIndex(index_vt, 0));
                            cur_model.vt.put(getTextureValueByGlobalIndex(index_vt, 1));

                        }

                        counter++;
                    }

                    if(isNormals) {
                        index_vn = facevertex[counter];

                        if(index_vn < 0){

                            cur_model.vn.put(cur_model.arr_vn.get(-index_vn-1)[0]);
                            cur_model.vn.put(cur_model.arr_vn.get(-index_vn-1)[1]);
                            cur_model.vn.put(cur_model.arr_vn.get(-index_vn-1)[2]);

                        }else{

                            cur_model.vn.put(getNormalValueByGlobalIndex(index_vn, 0));
                            cur_model.vn.put(getNormalValueByGlobalIndex(index_vn, 1));
                            cur_model.vn.put(getNormalValueByGlobalIndex(index_vn, 2));

                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param globalIndex глобальный индекс вершины
     * @param x_y_z 0 - x; 1 - y; 2 - z.
     * @return
     */
    private float getVertexValueByGlobalIndex(int globalIndex, int x_y_z) {

        float value;

        int count = 0;

        while (true) {

            Model model = list.get(count);

            if (globalIndex > model.arr_v.size()) {

                globalIndex = globalIndex - model.arr_v.size();
                count++;
                continue;
            }

            value = model.arr_v.get(globalIndex - 1)[x_y_z];
            break;
        }

        return value;
    }

    private float getTextureValueByGlobalIndex(int globalIndex, int x_y){

        float value;

        int count = 0;

        while(true){

            Model model = list.get(count);

            if(globalIndex > model.arr_vt.size()) {

                globalIndex = globalIndex - model.arr_vt.size();
                count++;
                continue;
            }

            value = model.arr_vt.get(globalIndex-1)[x_y];
            break;
        }

        return value;
    }

    private float getNormalValueByGlobalIndex(int globalIndex, int x_y_z){

        float value;

        int count = 0;

        while(true){

            Model model = list.get(count);

            if(globalIndex > model.arr_vn.size()){

                globalIndex = globalIndex - model.arr_vn.size();
                count++;
                continue;
            }

            value = model.arr_vn.get(globalIndex-1)[x_y_z];
            break;
        }

        return value;
    }





    public static class Model{

        public FloatBuffer v;
        public FloatBuffer vt;
        public FloatBuffer vn;

        public List<float[]> arr_v;
        public List<float[]> arr_vt;
        public List<float[]> arr_vn;

        public List<int[][]> faces;

        public int v_size;

        public Model(){

            arr_v   = new ArrayList<>();
            arr_vt  = new ArrayList<>();
            arr_vn  = new ArrayList<>();

            faces   = new ArrayList<>();

            v_size  = 0;
        }
    }
}
