//package com.votafore.warlords.v3;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.opengl.GLSurfaceView;
//import android.view.MotionEvent;
//
//import com.votafore.warlords.R;
//import com.votafore.warlords.v3.glsupport.GLRenderer;
//import com.votafore.warlords.v3.glsupport.GLShader;
//import com.votafore.warlords.v3.glsupport.GLView;
//import com.votafore.warlords.v3.glsupport.GLWorld;
//
//import org.json.JSONObject;
//
//import io.reactivex.functions.Consumer;
//
///**
// * @author Vorafore
// * Created on 28.12.2017.
// */
//
//public class Game {
//
//    /****************** Game ******************/
//
//    private Context mContext;
//
//    public Game(Context c){
//        mContext = c;
//    }
//
//    @SuppressLint("WrongConstant")
//    public void start(){
//
//        GLWorld    mGLWorld;
//        GLShader   mGLShader;
//        GLRenderer mGLRenderer;
//
//        mGLWorld = new GLWorld();
//        mGLWorld.camMove(GLWorld.AXIS_Y, 3f);
//
//        mICamera = mGLWorld;
//
//        mGameWorld = new World(mContext);
//
//        mGLShader    = new GLShader(mContext, R.raw.shader_vertex, R.raw.shader_fragment);
//        mGLRenderer  = new GLRenderer(mGLWorld, mGameWorld, mGLShader);
//
//        mSurfaceView = new GLView(mContext, mGLWorld, mGLRenderer) {
//            @Override
//            protected void init() {
//
//                mHandler = new MotionHandlerJoystick(mContext);
//
//                mHandler.setCameraListener(new MotionHandlerJoystick.ICameraListener() {
//                    @Override
//                    public void onCamMove(float deltaX, float deltaY) {
//
//                    }
//
//                    @Override
//                    public void onCamRotate(float deltaX, float deltaY) {
//
//                    }
//
//                    @Override
//                    public void setRotationDelta(float deltaX, float deltaY) {
//                        mServer.send(Queries.getQueryCamRotate(deltaX, deltaY));
//                    }
//
//                    @Override
//                    public void setMovingDelta(float deltaX, float deltaY) {
//                        mServer.send(Queries.getQueryCamMove(deltaX, deltaY));
//                    }
//                });
//            }
//
//            private MotionHandlerJoystick mHandler;
//
//            @Override
//            public boolean onTouchEvent(MotionEvent event) {
//                return mHandler.onHandleEvent(event);
//            }
//        };
//    }
//
//
//
//    /****************** for activity ******************/
//
//    private GLSurfaceView mSurfaceView;
//
//    public GLSurfaceView getSurfaceView(){
//        return mSurfaceView;
//    }
//
//
//
//
//
//
//    private World mGameWorld;
//
//
//
//
//    /****************** GL + network ******************/
//
//    private GLView.ICamera mICamera;
//
//
//    /****************** interaction with network ******************/
//
//    private IServer mServer;
//
//    public void setServer(IServer server){
//        mServer = server;
//
//        mServer.setReceiver(new Consumer<JSONObject>() {
//            @Override
//            public void accept(JSONObject object) throws Exception {
//                handleServerData(object);
//            }
//        });
//    }
//
//    /**
//     * everything that comes from server calculated here
//     * @param data
//     */
//    private void handleServerData(JSONObject data)throws Exception{
//
//
//        // TODO: 11.01.2018 analize data and make appropriate actions
//        switch(data.getString("command")){
//            case "camMove":
//
//                mICamera.setMovingDelta(GLWorld.AXIS_X, Float.valueOf(data.getString("deltaX")));
//                mICamera.setMovingDelta(GLWorld.AXIS_Z, Float.valueOf(data.getString("deltaY")));
//
//                break;
//            case "camRotate":
//
//                mICamera.setRotationDelta(GLWorld.AXIS_Y, Float.valueOf(data.getString("deltaX")));
//                mICamera.setRotationDelta(GLWorld.AXIS_X, Float.valueOf(data.getString("deltaY")));
//
//                break;
//        }
//
//    }
//}
