//package com.votafore.warlords.v4.network;
//
//import android.content.Context;
//import android.net.nsd.NsdManager;
//import android.net.nsd.NsdServiceInfo;
//import android.os.Build;
//import android.os.Looper;
//
//
//import com.votafore.warlords.v4.constant.Constants;
//import com.votafore.warlords.v4.constant.Log;
//import com.votafore.warlords.v4.test.User;
//
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.SocketException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Cancellable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.processors.PublishProcessor;
//import io.reactivex.schedulers.Schedulers;
//
//import static com.votafore.warlords.v4.constant.Constants.LVL_LOCAL_SERVER;
//import static com.votafore.warlords.v4.constant.Constants.TAG_DATA_SEND;
//import static com.votafore.warlords.v4.constant.Constants.TAG_SOCKET;
//import static com.votafore.warlords.v4.constant.Constants.TAG_SRV_CRT;
//import static com.votafore.warlords.v4.constant.Constants.TAG_SRV_START;
//import static com.votafore.warlords.v4.constant.Constants.TAG_SRV_STOP;
//
///**
// * @author Votafore
// * Created on 26.12.2017.
// *
// * implementation for IServer (for local server)
// */
//
//public class ServerLocal implements IServer {
//
//    /************* IServer *******************/
//
//    /**
//     * object that send information
//     */
//    private PublishProcessor<JSONObject> sender;
//
//    /**
//     * object that accept socket connections
//     */
//    private Disposable dsp_sockets;
//
//
//    private ServerSocket mServerSocket;
//
//
//    @Override
//    public Disposable setReceiver(Consumer<JSONObject> receiver) {
//        Log.d1("", LVL_LOCAL_SERVER, "set client");
//        localReceiver = receiver;
//        return sender.subscribe(localReceiver);
//    }
//
//    @Override
//    public void send(final JSONObject data) {
//
//        Log.d1(TAG_DATA_SEND, LVL_LOCAL_SERVER, "sending data");
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                Looper.prepare();
//
//                try {
//                    handleRequest(null, data);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//
//    @Override
//    public void start() {
//
//        Log.d1(TAG_SRV_START, LVL_LOCAL_SERVER, "create ServerSocket");
//
//        try {
//            mServerSocket = new ServerSocket(0);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d1(TAG_SRV_START, LVL_LOCAL_SERVER, "ServerSocket not created");
//            return;
//        }
//
//        Log.d2(TAG_SRV_START, LVL_LOCAL_SERVER, "APPENDER", "create");
//
//        // appender of sockets to channel
//        dsp_sockets = Observable.create(new ObservableOnSubscribe<ISocket>() {
//            @Override
//            public void subscribe(ObservableEmitter<ISocket> e) throws Exception {
//
//                e.setCancellable(new Cancellable() {
//                    @Override
//                    public void cancel() throws Exception {
//                        Log.d2(TAG_SRV_STOP, LVL_LOCAL_SERVER, "APPENDER", "stopping serverSocket");
//                        mServerSocket.close();
//                    }
//                });
//
//                while(!mServerSocket.isClosed()){
//                    try{
//                        Log.d2(TAG_SRV_START, LVL_LOCAL_SERVER, "APPENDER", "waiting for connection");
//                        ISocket s = Socket.create(mServerSocket.accept());
//                        e.onNext(s);
//                    }catch(SocketException ex){
//                        ex.printStackTrace();
//                    }
//                }
//
//                Log.d2("", LVL_LOCAL_SERVER, "APPENDER", "serverSocket is not waiting for connections anymore");
//            }
//        })
//        .subscribeOn(Schedulers.newThread())
//        .subscribe(new Consumer<ISocket>() {
//            @Override
//            public void accept(final ISocket iSocket) throws Exception {
//
//                Log.d2(TAG_SOCKET, LVL_LOCAL_SERVER, "APPENDER", "set up socket in server");
//
//                Log.d2(TAG_SOCKET, LVL_LOCAL_SERVER, "APPENDER", "set socket as subscriber for sender");
//                iSocket.subscribeSocket(sender);
//
//                Log.d2(TAG_SOCKET, LVL_LOCAL_SERVER, "APPENDER", "subscribe server to incoming data");
//
//                map_dsp_receiver.put(iSocket, iSocket.setReceiver(new Consumer<JSONObject>() {
//                    @Override
//                    public void accept(JSONObject request) throws Exception {
//                        handleRequest(iSocket, request);
//                    }
//                }));
//            }
//        });
//
//    }
//
//    @Override
//    public void stop() {
//
//        Log.d1(TAG_SRV_STOP, LVL_LOCAL_SERVER, "unsubscribe server from socket emitter");
//        for(ISocket socket: map_dsp_receiver.keySet()){
//            map_dsp_receiver.get(socket).dispose();
//        }
//
//        Log.d1(TAG_SRV_STOP, LVL_LOCAL_SERVER, "sender.onComplete().. close sockets");
//        sender.onComplete();
//
//        Log.d1(TAG_SRV_STOP, LVL_LOCAL_SERVER, "stopping appender");
//        dsp_sockets.dispose();
//    }
//
//
//
//    @Override
//    public void startSearching(Context context) {
//        startBroadcast(context);
//    }
//
//    @Override
//    public void stopSearching() {
//        stopBroadcast();
//    }
//
//    @Override
//    public void setSearchingListener(ISearchingListener listener) {
//
//    }
//
//    /****************** ServerLocal ******************/
//
//
//    public ServerLocal(){
//
//        Log.d1(TAG_SRV_CRT, LVL_LOCAL_SERVER, "create sender");
//
//        sender = PublishProcessor.create();
//        sender.subscribeOn(Schedulers.io());
//
//        Log.d1(TAG_SRV_CRT, LVL_LOCAL_SERVER, "create maps");
//        map_dsp_receiver = new HashMap<>();
//
//        mUsers = new ArrayList<>();
//    }
//
//    synchronized private void handleRequest(ISocket socket, JSONObject data)throws Exception{
//
//        Log.d1(TAG_DATA_SEND, LVL_LOCAL_SERVER, "handleRequest");
//
//        if (data.get("type").equals("info")){
//
//            JSONObject response = new JSONObject();
//
//            if(data.get("data").equals("registration")) {
//
//                // handshaking (registration of new connection)
//
//                User user = new User();
//                user.ID = System.currentTimeMillis();
//
//                mUsers.add(user);
//
//                response.put("type"  , "response");
//                response.put("data"  , "registration");
//                response.put("userID", user.ID);
//
//            }else if(data.get("data").equals("CloseSocket")){
//
//                // client/socket disconnected
//
//                map_dsp_receiver.get(socket).dispose();
//                map_dsp_receiver.remove(socket);
//
//                return;
//
//            }else if(data.get("data").equals("state")){
//
//                // state of client has changed
//
//                // just register it
//
//                response.put("type", "notify");
//                response.put("data", "bla-bla-bla");
//            }
//
//            // specialty of this group is that response is sent only for request sender
//            if(socket != null){
//                socket.send(response);
//            }else{
//                localReceiver.accept(response);
//            }
//
//        }else if(data.get("type").equals("GlobalEvent")){
//
//            JSONObject response = new JSONObject();
//
//            if(data.get("event").equals("StartGame")){
//
//                response = data;
//            }
//
//            // specialty of this group is that response is sent for all
//            sender.onNext(response);
//
//        }else if(data.get("type").equals("data")){
//
//            sender.onNext(data);
//
//        }
//    }
//
//
//
//
//
//
//    /**************** misc ******************/
//
//    private Map<ISocket, Disposable> map_dsp_receiver;
//
//    @Override
//    public String toString(){
//        return "0.0.0.0";
//    }
//
//
//
//    /**************** tests ******************/
//
//
//    /*** searching ***/
//
//    private NsdManager.RegistrationListener mNSDListener;
//
//    private NsdManager mNSDManager;
//
//    private boolean isBroadcasting = false;
//
//    public void startBroadcast(Context context){
//
//        if(isBroadcasting)
//            return;
//
//        Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "startBroadcast");
//
//        mNSDManager  = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
//        mNSDListener = new NsdManager.RegistrationListener() {
//            @Override
//            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
//                Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "onRegistrationFailed");
//            }
//
//            @Override
//            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
//                Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "onUnregistrationFailed");
//            }
//
//            @Override
//            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
//                Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "onServiceRegistered");
//            }
//
//            @Override
//            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
//                Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "onServiceUnregistered");
//            }
//        };
//
//        NsdServiceInfo regInfo = new NsdServiceInfo();
//        regInfo.setServiceName(Constants.SERVICENAME);
//        regInfo.setServiceType(Constants.SERVICETYPE);
//        regInfo.setPort(mServerSocket.getLocalPort());
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            regInfo.setAttribute("ownerName" , "developer");
//            regInfo.setAttribute("message"   , "welcome !!! )");
//        }
//
//        Log.d2(TAG_SRV_START, LVL_LOCAL_SERVER, "BROADCASTER", "register service");
//        mNSDManager.registerService(regInfo, NsdManager.PROTOCOL_DNS_SD, mNSDListener);
//
//        isBroadcasting = true;
//    }
//
//    public void stopBroadcast(){
//
//        if(!isBroadcasting)
//            return;
//
//        Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "stopBroadcast");
//
//        Log.d2("", LVL_LOCAL_SERVER, "BROADCASTER", "unregister service");
//        mNSDManager.unregisterService(mNSDListener);
//
//        isBroadcasting = false;
//    }
//
//
//
//
//    /****** handshaking ******/
//
//    List<User> mUsers;
//
//    Consumer<JSONObject> localReceiver;
//}
