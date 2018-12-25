package com.example.think.myapplication;

import android.util.Log;

import java.io.PrintWriter;

public class SendThread extends Thread{
    private PrintWriter printWriter;
    private String order;
    public SendThread(PrintWriter printWriter,String order){
        this.printWriter = printWriter;
        this.order = order;
    }
    @Override
    public void run() {
        printWriter.print(order);
        printWriter.flush();
        Log.e("Boomerr---test5",order);
    }
}
