//package com.votafore.warlords.v3;
//
//import android.content.Context;
//
//import com.votafore.warlords.v3.glsupport.GLShader;
//import com.votafore.warlords.v3.glsupport.GLUnit;
//import com.votafore.warlords.v3.gltests.MeshMapTest;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Vorafore
// * Created on 11.01.2018.
// *
// * represents a world of game... all objects that will be shown on the GL scene
// * are stored and controlled here
// */
//
//public class World {
//
//
//
//
//
//
//
//
//
//
//
//    /**
//     * drawing objects
//     */
//    public void draw(GLShader shader){
//
//        for(GLUnit object: objects){
//            object.draw(shader);
//        }
//
//        // первым делом отрисовываем карту
//        // TODO: 29.12.2017 вернуть
//        //mInstance.getMap().draw(mShader);
//
//        // и базу
//        // TODO: 29.12.2017 вернуть
//        //mInstance.mBase.draw(mShader);
//
//
//        // потом все остальные объекты сцены
////        List<GLUnit> objects = mInstance.getObjects();
////
////        for (GLUnit curr_obj : objects) {
////            curr_obj.draw(mShader);
////        }
//
//    }
//
//
//    /*************** tests *******************/
//
//    List<GLUnit> objects;
//
//    public World(Context context){
//
//        objects = new ArrayList<>();
//
//        GLUnit map = new MeshMapTest(context);
//        map.init();
//
//        objects.add(map);
//    }
//}
