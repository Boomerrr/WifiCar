package com.example.think.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ConnectThread extends  Thread {
    public Socket socket;
    public BufferedReader mBufferedReaderClient;
    public PrintWriter mPrintWriterClient;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String order = bundle.getString("order");
            Log.e("Boomerr--test",order);
                    try {
                        mBufferedReaderClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        mPrintWriterClient = new PrintWriter(socket.getOutputStream(), true);
                        SendThread sendThread = new SendThread(mPrintWriterClient,order);
                        sendThread.start();
                    } catch (IOException e) {
                        Log.e("Boomerr---test", "1");
                        e.printStackTrace();
                    }
            }
    };
    @Override
    public void run() {
        String sPort = "2001";
        String sIP = "192.168.1.1";
        int port = Integer.parseInt(sPort);

        Log.e("gjz", "IP:"+ sIP + ":" + port);

        try
        {
            Log.e("Boomerr---test",sIP+"    "+port);
            //连接服务器

            socket = new Socket(sIP, port);	//portnum
            Log.e("Boomerr---test1", String.valueOf(socket));
    }
        catch (UnknownHostException e) {
            Log.e("Boomerr---test","1");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Boomerr---test","2");
            e.printStackTrace();
        }

    }
}