package com.example.think.myapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity
{

    private Button ForWard;
    private Button BackWard;
    private Button TurnLeft;
    private Button TurnRight;
    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button add_speed;
    private Button slow_speed;
    private Button clean;
    private Button stop;
    private TextView speed ;
    URL videoUrl;
    Bitmap bmp;
    private boolean isConnecting = false;
    private Thread mThreadClient = null;
    private Socket mSocketClient ;
    static PrintWriter mPrintWriterClient = null;
    static BufferedReader mBufferedReaderClient	= null;
    public static String CameraIp;
    private String[] str = {"0","0","0","4","0","\n"};
    MySurfaceView r;
    private Handler handler;
    private Handler handler1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//隐去标题（应用的名字必须要写在setContentView之前，否则会有异常）
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        ForWard= (Button)findViewById(R.id.ForWard);
        TurnLeft= (Button)findViewById(R.id.TurnLeft);
        TurnRight= (Button)findViewById(R.id.TurnRight);
        BackWard= (Button)findViewById(R.id.BackWard);

        button1= (Button)findViewById(R.id.Button1);
        button1.setOnClickListener(button6ClickListener);

        button6= (Button)findViewById(R.id.Button6);
        button6.setOnClickListener(button1ClickListener);

        clean = (Button) findViewById(R.id.clean);
        stop = (Button) findViewById(R.id.stop);
        add_speed = (Button) findViewById(R.id.add_speed);
        slow_speed = (Button) findViewById(R.id.slow_speed);

        speed =  (TextView) findViewById(R.id.speed_text);
        speed.setText("当前速度: " + str[3]);
        handler1 =  new Handler(){
            @Override
            public void handleMessage(Message msg) {
                int speedNum = msg.what;
                speed.setText("当前速度："+ speedNum);
            }
        };
        //加速
        add_speed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int speed = Integer.parseInt(str[3]);
                if(speed < 7){
                    speed++;
                }
                str[3] = String.valueOf(speed);
                String order1 = "";
                for(int i = 0;i < str.length; i++){
                    order1 += str[i];
                }
                //mPrintWriterClient.print(order1);
                //mPrintWriterClient.flush();
                Bundle bundle1 = new Bundle();
                bundle1.putString("order",order1);
                Message message1 = new Message();
                message1.setData(bundle1);
                handler.sendMessage(message1);
                Message message2 = new Message();
                message2.what = Integer.parseInt(str[3]);
                handler1.sendMessage(message2);
            }
        });
        //减速
        slow_speed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int speed = Integer.parseInt(str[3]);
                if(speed > 1){
                    speed--;
                }
                str[3] = String.valueOf(speed);
                String order1 = "";
                for(int i = 0;i < str.length; i++){
                    order1 += str[i];
                }
                //mPrintWriterClient.print(order1);
                //mPrintWriterClient.flush();
                Bundle bundle1 = new Bundle();
                bundle1.putString("order",order1);
                Message message1 = new Message();
                message1.setData(bundle1);
                handler.sendMessage(message1);

                Message message2 = new Message();
                message2.what = Integer.parseInt(str[3]);
                handler1.sendMessage(message2);
            }
        });
        //打扫
        clean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                str[4] = "1";
                String order1 = "";
                for(int i = 0;i < str.length; i++){
                    order1 += str[i];
                }
                //mPrintWriterClient.print(order1);
                //mPrintWriterClient.flush();
                Bundle bundle1 = new Bundle();
                bundle1.putString("order",order1);
                Message message1 = new Message();
                message1.setData(bundle1);
                handler.sendMessage(message1);
            }
        });
        //停止
        stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                str[4] = "0";
                String order1 = "";
                for(int i = 0;i < str.length; i++){
                    order1 += str[i];
                }
                //mPrintWriterClient.print(order1);
                //mPrintWriterClient.flush();
                Bundle bundle1 = new Bundle();
                bundle1.putString("order",order1);
                Message message1 = new Message();
                message1.setData(bundle1);
                handler.sendMessage(message1);
            }
        });
        r=(MySurfaceView)findViewById(R.id.mySurfaceView1);
       /*
       *  ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE ,Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.ACCESS_WIFI_STATE }, 1);
       *
       * */
        CameraIp ="http://192.168.1.1:8080/?action=snapshot";
        //rHolder.setFixedSize(dm.widthPixels,dm.heightPixels);
        r.GetCameraIP(CameraIp);

        //mThreadClient = new Thread(mRunnable);
        //mThreadClient.run();
        ConnectThread thread = new ConnectThread();
        thread.start();
        handler = thread.handler;
        mSocketClient = thread.socket;

        Log.e("Boomerr--test2", String.valueOf(mSocketClient));
        ForWard.setOnTouchListener(new View.OnTouchListener()
                                   {
                                       public boolean onTouch(View v, MotionEvent event) {
                                           int action = event.getAction();
                                           switch(action)
                                           {
                                               case MotionEvent.ACTION_DOWN:
                                                   str[1] = "F";
                                                   String order1 = "";
                                                   for(int i = 0;i < str.length; i++){
                                                        order1 += str[i];
                                                   }
                                                   //mPrintWriterClient.print(order1);
                                                   //mPrintWriterClient.flush();
                                                   Bundle bundle1 = new Bundle();
                                                   bundle1.putString("order",order1);
                                                   Message message1 = new Message();
                                                   message1.setData(bundle1);
                                                   handler.sendMessage(message1);
                                                   break;
                                               case MotionEvent.ACTION_UP:
                                                   str[1] = "0";
                                                   String order2 = "";
                                                   for(int i = 0;i < str.length; i++){
                                                       order2 += str[i];
                                                   }
                                                 //  mPrintWriterClient.print(order2);
                                                   //mPrintWriterClient.flush();
                                                   Bundle bundle2 = new Bundle();
                                                   bundle2.putString("order",order2);
                                                   Message message2 = new Message();
                                                   message2.setData(bundle2);
                                                   handler.sendMessage(message2);
                                           }
                                           return false;
                                       }
                                   }
        );
        //后退
        BackWard.setOnTouchListener(new View.OnTouchListener()
                                    {
                                        public boolean onTouch(View v, MotionEvent event) {
                                            int action = event.getAction();
                                            switch(action)
                                            {
                                                case MotionEvent.ACTION_DOWN:
                                                    str[1] = "B";
                                                    String order1 = "";
                                                    for(int i = 0;i < str.length; i++){
                                                        order1 += str[i];
                                                    }
                                                    Bundle bundle1 = new Bundle();
                                                    bundle1.putString("order",order1);
                                                    Message message1 = new Message();
                                                    message1.setData(bundle1);
                                                    handler.sendMessage(message1);
                                                    break;
                                                case MotionEvent.ACTION_UP:
                                                    str[1] = "0";
                                                    String order2 = "";
                                                    for(int i = 0;i < str.length; i++){
                                                        order2 += str[i];
                                                    }
                                                    Bundle bundle2 = new Bundle();
                                                    bundle2.putString("order",order2);
                                                    Message message2 = new Message();
                                                    message2.setData(bundle2);
                                                    handler.sendMessage(message2);
                                            }
                                            return false;
                                        }

                                    }
        );
        //右转
        TurnRight.setOnTouchListener(new View.OnTouchListener()
                                     {
                                         public boolean onTouch(View v, MotionEvent event) {
                                             int action = event.getAction();
                                             switch(action)
                                             {
                                                 case MotionEvent.ACTION_DOWN:
                                                     str[2] = "R";
                                                     String order1 = "";
                                                     for(int i = 0;i < str.length; i++){
                                                         order1 += str[i];
                                                     }
                                                     Bundle bundle1 = new Bundle();
                                                     bundle1.putString("order",order1);
                                                     Message message1 = new Message();
                                                     message1.setData(bundle1);
                                                     handler.sendMessage(message1);
                                                     break;
                                                 case MotionEvent.ACTION_UP:
                                                     str[2] = "0";
                                                     String order2 = "";
                                                     for(int i = 0;i < str.length; i++){
                                                         order2 += str[i];
                                                     }
                                                     Bundle bundle2 = new Bundle();
                                                     bundle2.putString("order",order2);
                                                     Message message2 = new Message();
                                                     message2.setData(bundle2);
                                                     handler.sendMessage(message2);
                                             }
                                             return false;
                                         }
                                     }
        );
        //左转
        TurnLeft.setOnTouchListener(new View.OnTouchListener()
                                    {
                                        public boolean onTouch(View v, MotionEvent event) {
                                            int action = event.getAction();
                                            switch(action)
                                            {
                                                case MotionEvent.ACTION_DOWN:
                                                    str[2] = "L";
                                                    String order1 = "";
                                                    for(int i = 0;i < str.length; i++){
                                                        order1 += str[i];
                                                    }
                                                    Bundle bundle1 = new Bundle();
                                                    bundle1.putString("order",order1);
                                                    Message message1 = new Message();
                                                    message1.setData(bundle1);
                                                    handler.sendMessage(message1);
                                                    break;
                                                case MotionEvent.ACTION_UP:
                                                    str[2] = "0";
                                                    String order2 = "";
                                                    for(int i = 0;i < str.length; i++){
                                                        order2 += str[i];
                                                    }
                                                    Bundle bundle2 = new Bundle();
                                                    bundle2.putString("order",order2);
                                                    Message message2 = new Message();
                                                    message2.setData(bundle2);
                                                    handler.sendMessage(message2);
                                            }
                                            return false;
                                        }
                                    }
        );
    }
    //关闭
    private OnClickListener button1ClickListener = new OnClickListener() {
        public void onClick(View arg0) {
            str[0] = "0";
            String order1 = "";
            for(int i = 0;i < str.length; i++){
                order1 += str[i];
            }
            Bundle bundle1 = new Bundle();
            bundle1.putString("order",order1);
            Message message1 = new Message();
            message1.setData(bundle1);
            handler.sendMessage(message1);
        }
    };
    //吸尘
    private OnClickListener button6ClickListener = new OnClickListener() {
        public void onClick(View arg0) {
            str[0] = "1";
            String order1 = "";
            for(int i = 0;i < str.length; i++){
                order1 += str[i];
            }
            Bundle bundle1 = new Bundle();
            bundle1.putString("order",order1);
            Message message1 = new Message();
            message1.setData(bundle1);
            handler.sendMessage(message1);
        }
    };

    private Runnable	mRunnable	= new Runnable()
    {
        public void run()
        {
            //String msgText ="192.168.1.1:2001";
            //String msgText = "192.168.1.1:8080";
            //int start = msgText.indexOf(":");

           // String sIP = msgText.substring(0, start);
            //String sPort = msgText.substring(start+1);
            String sPort = "2001";
            String sIP = "192.168.1.1 ";
            int port = Integer.parseInt(sPort);

            Log.d("gjz", "IP:"+ sIP + ":" + port);

            try
            {
                Log.e("Boomerr---test",sIP+"    "+port);
                //连接服务器

                mSocketClient = new Socket(sIP, port);	//portnum
                if(mSocketClient.isConnected()){
                    Toast.makeText(MainActivity.this,"connect",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this," not connect",Toast.LENGTH_SHORT).show();
                }
                //取得输入、输出流
                mBufferedReaderClient = new BufferedReader(new InputStreamReader(mSocketClient.getInputStream()));

                mPrintWriterClient = new PrintWriter(mSocketClient.getOutputStream(), true);


                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
                //break;
            }
            catch (SocketException e)
            {
                e.printStackTrace();
                Log.e("Boomerr---test","1");
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
                return;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            char[] buffer = new char[256];
            while (isConnecting)
            {
                try
                {
                    //if ( (recvMessageClient = mBufferedReaderClient.readLine()) != null )
                    if((mBufferedReaderClient.read(buffer))>0)
                    {
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }
                }
                catch (Exception e)
                {
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            }
        }
    };

    Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

        }
    };

    public void onDestroy() {
        super.onDestroy();
        if (isConnecting)
        {
            isConnecting = false;
            try {
                if(mSocketClient!=null)
                {
                    mSocketClient.close();
                    mSocketClient = null;

                    mPrintWriterClient.close();
                    mPrintWriterClient = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mThreadClient.interrupt();
        }

    }
}

