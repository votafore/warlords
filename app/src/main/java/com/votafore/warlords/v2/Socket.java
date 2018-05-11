//package com.votafore.warlords.v2;
//
//import android.util.Log;
//
//import com.votafore.warlords.v2.IDataListener;
//import com.votafore.warlords.v2.ISocket;
//
//import org.json.JSONObject;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.InetAddress;
//
//
///**
// * @author Votafore
// * Created on 20.12.2017.
// *
// * class represents a socket
// */
//
//public class Socket implements ISocket {
//
//    public DataOutputStream output;
//    public DataInputStream input;
//
//    public java.net.Socket mSocket;
//
//    public Socket(InetAddress ip, int port) throws IOException{
//
//        mSocket = new java.net.Socket(ip, port);
//        init();
//    }
//
//    public Socket(java.net.Socket s) throws IOException{
//        mSocket = s;
//        init();
//    }
//
//    private void init() throws IOException{
//
//        output = new DataOutputStream(mSocket.getOutputStream());
//        input = new DataInputStream(mSocket.getInputStream());
//
//        while(!mSocket.isConnected()){
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//
//
//
//
//    /*********** ISocket **************/
//
//    @Override
//    public void send(JSONObject data)throws IOException {
//        output.writeUTF(data.toString());
//    }
//
//    @Override
//    public void close() throws IOException {
//        mSocket.close();
//        Log.v("TESTRX", "SOCKET.    closed");
//    }
//
//    @Override
//    public void setDataListener(final IDataListener<String> listener) {
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                while(!mSocket.isClosed()){
//
//                    String data = "";
//
//                    try {
//                        data = null;
//                        data = input.readUTF();
//                    } catch (IOException exception) {
//                        exception.printStackTrace();
//                    }
//
//                    listener.onDataReceived(data);
//                }
//
//            }
//        }).start();
//    }
//
//
//
//
//
//
//
//
//    /************* miscellaneous ************/
//
//    @Override
//    public String toString() {
//        return mSocket.toString();
//    }
//}
