package com.votafore.warlords;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.votafore.warlords.net.wifi.CMWifiForClient;
import com.votafore.warlords.net.wifi.CMWifiForServer;
import com.votafore.warlords.net.IClient;
import com.votafore.warlords.net.IConnection;

public class ActivityTestSocket extends AppCompatActivity implements View.OnClickListener{

//    private ServerSocket serverSocket;
//
//    Handler updateConversationHandler;
//
//    Thread serverThread = null;

    private TextView text;

    public static final int SERVERPORT = 6000;

    Button btn_close;
    Button btn_response;

    IConnection mConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_socket);
        text = (TextView) findViewById(R.id.text2);
//
//        updateConversationHandler = new Handler();
//
//
//        Log.v("MSOCKET", "создаем поток");
//        this.serverThread = new Thread(new ServerThread());
//
//        Log.v("MSOCKET", "запускаем поток");
//        this.serverThread.start();
//
        btn_close               = (Button) findViewById(R.id.btn_close);
        btn_response            = (Button) findViewById(R.id.response);
        Button btn_startClient  = (Button) findViewById(R.id.btn_start_client);
        Button btn_startServer  = (Button) findViewById(R.id.btn_start_server);


        btn_close.setOnClickListener(this);
        btn_response.setOnClickListener(this);
        btn_startServer.setOnClickListener(this);
        btn_startClient.setOnClickListener(this);

        btn_close.setEnabled(false);
        btn_response.setEnabled(false);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btn_start_server:

                btn_close.setEnabled(true);
                btn_response.setEnabled(true);

                mConnection = new CMWifiForServer(new IClient() {
                    @Override
                    public void onMessageReceive(int ID) {

                        Toast.makeText(getApplicationContext(), "message received for server", Toast.LENGTH_SHORT).show();
                    }
                });

                mConnection.init();

                break;
            case R.id.btn_start_client:

                btn_close.setEnabled(true);

                mConnection = new CMWifiForClient(new IClient() {
                    @Override
                    public void onMessageReceive(int ID) {

                        Log.v("MSOCKET", "message received for client");
                    }
                });

                mConnection.init();

                break;
            case R.id.btn_close:

                if(mConnection != null)
                    mConnection.release();

                btn_close.setEnabled(false);
                btn_response.setEnabled(false);

                break;
            case R.id.response:

                mConnection.sendMessage();
        }
    }

//    class ServerThread implements Runnable {
//
//        public void run() {
//
//            Log.v("MSOCKET", "поток запущен");
//
//            Socket socket = null;
//            try {
//                serverSocket = new ServerSocket(SERVERPORT);
//                Log.v("MSOCKET", "ServerSocket: создан");
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.v("MSOCKET", "ServerSocket: исключение");
//                return;
//            }
//            while (!Thread.currentThread().isInterrupted()) {
//
//                try {
//                    Log.v("MSOCKET", "начинаем ждать входящего подключения");
//                    socket = serverSocket.accept();
//
//                    Log.v("MSOCKET", "есть подключение");
//
//                    Log.v("MSOCKET", "передаем сокет на обработку сообщений (создаем отдельный поток)");
//                    CommunicationThread commThread = new CommunicationThread(socket);
//
//                    Log.v("MSOCKET", "запускаем поток обработки сокета");
//                    new Thread(commThread).start();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    class CommunicationThread implements Runnable {
//
//        private Socket clientSocket;
//
//        private BufferedReader input;
//
//        public CommunicationThread(Socket clientSocket) {
//
//            Log.v("MSOCKET", "CommunicationThread создан");
//
//            this.clientSocket = clientSocket;
//
//            try {
//
//                Log.v("MSOCKET", "попытка получения входящего потока (getInputStream)");
//
//                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
//                Log.v("MSOCKET", "вх. поток получен");
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.v("MSOCKET", "вх. поток не получен");
//            }
//        }
//
//        public void run() {
//
//            Log.v("MSOCKET", "поток сокета запущен");
//
//            while (!Thread.currentThread().isInterrupted()) {
//
//                try {
//
//                    Log.v("MSOCKET", "отправляем ответ сервера");
//                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
//                    out.println("hello from server");
//
//                    Log.v("MSOCKET", "пытаемся прочитать строку");
//                    String read = input.readLine();
//
//                    if(read == null){
//                        //Thread.currentThread().interrupt();
//                        continue;
//                    }
//
//                    Log.v("MSOCKET", "данные прочитаны ("+read+"). Отправляем в поток UI");
//                    updateConversationHandler.post(new updateUIThread(read));
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    class updateUIThread implements Runnable {
//
//        private String msg;
//
//        public updateUIThread(String str) {
//            this.msg = str;
//        }
//
//        @Override
//        public void run() {
//            text.setText(text.getText().toString()+"Client Says: "+ msg + "\n");
//        }
//    }
}
