package com.gjf.danmutest;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by lenovo047 on 2017/2/17.
 */
public class ClinetThread implements Runnable {
    private Socket s;
    //定义UI线程发送消息的Handler对象
    private Handler handler;
    //定义接受UI线程的消息的Handler对象
    public  Handler revHandler;

    //该线程所处理的Socket所对应的输入流
    BufferedReader br = null;
    OutputStream os= null;


    public ClinetThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try{
            s= new Socket("139.199.78.22",23333);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = s.getOutputStream();
            //启动一条子线程来读取服务器相应的数据
            new Thread(){
                /**
                 * Calls the <code>run()</code> method of the Runnable object the receiver
                 * holds. If no Runnable is set, does nothing.
                 *
                 * @see Thread#start
                 */
                @Override
                public void run() {
                    String content  = null ;
                    //不断读取Socket输入流中的内容
                    try {
                        while((content = br.readLine())!=null){
                            //没当读到服务器的数据之后，发送消息通知
                            //程序界面显示该数据
                            Message msg = new Message();
                            msg.what = 0x123;
                            msg.obj = content;
                            handler.sendMessage(msg);
                        }
                    }catch (Exception e){

                    }
                }
            }.start();
            //为当前线程初始化Looper
            Looper.prepare();
            //创建revHandler对象
            revHandler = new Handler(){
                /**
                 * Subclasses must implement this to receive messages.
                 *
                 * @param msg
                 */
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0x345){
                        try {
                            //将用户在文本框内输入内容写入网络投
                            os.write((msg.obj.toString()+"\r\n").getBytes("utf-8"));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            };

            Looper.loop();

        }catch (SocketTimeoutException e){
            System.out.print("网络连接超时！");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
