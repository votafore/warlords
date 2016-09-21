package com.votafore.warlords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivityTestSocket extends AppCompatActivity implements View.OnClickListener{

    private ServerSocket serverSocket;

    Handler updateConversationHandler;

    Thread serverThread = null;

    private TextView text;

    public static final int SERVERPORT = 6000;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_socket);
        text = (TextView) findViewById(R.id.text2);

        updateConversationHandler = new Handler();


        Log.v("MSOCKET", "создаем поток");
        this.serverThread = new Thread(new ServerThread());

        Log.v("MSOCKET", "запускаем поток");
        this.serverThread.start();

        Button btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(this);

        Button btn_response = (Button) findViewById(R.id.response);
        btn_response.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btn_close:

                try {
                    Log.v("MSOCKET", "пытаемся закрыть сокет");
                    serverSocket.close();
                    Log.v("MSOCKET", "сокет закрыт");
                } catch (IOException e) {
                    Log.v("MSOCKET", "Закрытие сокета: исключение");
                    e.printStackTrace();
                }

                Log.v("MSOCKET", "пытаемся остановить поток");
                serverThread.interrupt();
                Log.v("MSOCKET", "поток остановлен");

                break;
            case R.id.response:

        }
    }

    class ServerThread implements Runnable {

        public void run() {

            Log.v("MSOCKET", "поток запущен");

            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
                Log.v("MSOCKET", "ServerSocket: создан");
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("MSOCKET", "ServerSocket: исключение");
                return;
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    Log.v("MSOCKET", "начинаем ждать входящего подключения");
                    socket = serverSocket.accept();

                    Log.v("MSOCKET", "есть подключение");

                    Log.v("MSOCKET", "передаем сокет на обработку сообщений (создаем отдельный поток)");
                    CommunicationThread commThread = new CommunicationThread(socket);

                    Log.v("MSOCKET", "запускаем поток обработки сокета");
                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            Log.v("MSOCKET", "CommunicationThread создан");

            this.clientSocket = clientSocket;

            try {

                Log.v("MSOCKET", "попытка получения входящего потока (getInputStream)");

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                Log.v("MSOCKET", "вх. поток получен");

            } catch (IOException e) {
                e.printStackTrace();
                Log.v("MSOCKET", "вх. поток не получен");
            }
        }

        public void run() {

            Log.v("MSOCKET", "поток сокета запущен");

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    Log.v("MSOCKET", "отправляем ответ сервера");
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
                    out.println("hello from server");

                    Log.v("MSOCKET", "пытаемся прочитать строку");
                    String read = input.readLine();

                    if(read == null){
                        //Thread.currentThread().interrupt();
                        continue;
                    }

                    Log.v("MSOCKET", "данные прочитаны ("+read+"). Отправляем в поток UI");
                    updateConversationHandler.post(new updateUIThread(read));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class updateUIThread implements Runnable {

        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            text.setText(text.getText().toString()+"Client Says: "+ msg + "\n");
        }
    }
}
