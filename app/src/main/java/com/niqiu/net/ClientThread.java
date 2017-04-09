package com.niqiu.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by 毅东 on 2015/10/26.
 */
public class ClientThread implements Runnable {
    private Socket s;
    // 定义向UI线程发送消息的Handler对象
    private Handler handler;
    // 定义接收UI线程的消息的Handler对象
    public Handler revHandler;
    // 该线程所处理的Socket所对应的输入流
    BufferedReader br = null;
    OutputStream os = null;
    Context context;
    Thread clientChild;

    public ClientThread(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
    }

    public void run() {
        try {
            s = new Socket(SocketHelp.newInstance().getGateWayIP(context), 30000);
            br = new BufferedReader(new InputStreamReader(
                    s.getInputStream()));
            os = s.getOutputStream();
            // 启动一条子线程来读取服务器响应的数据
            new Thread() {
                @Override
                public void run() {
                    String content = null;
                    // 不断读取Socket输入流中的内容。
                    try {
                        while ((content = br.readLine()) != null) {
                            Log.e("ClientThread", "客户端接受===" + content);
                            // 每当读到来自服务器的数据之后，发送消息通知程序界面显示该数据
                            Message msg = new Message();
                            msg.what = 0x123;
                            msg.obj = content;
                            handler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            // 为当前线程初始化Looper
            Looper.prepare();
            // 创建revHandler对象
            revHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 接收到UI线程中用户输入的数据
                    if (msg.what == 0x345) {
                        // 将用户在文本框内输入的内容写入网络
                        try {
                            Log.e("ClientThread", "客户端发送===" + msg.obj.toString());
                            os.write((msg.obj.toString() + "\r\n")
                                    .getBytes("utf-8"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            // 启动Looper
            Looper.loop();
        } catch (SocketTimeoutException e1) {
            System.out.println("网络连接超时！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopTcpThread() {
        try{
            if(s != null){
                os.close();
            }
            if(s != null){
                s.close();
            }
            if (clientChild != null) {
                clientChild.interrupt();
            }
        }catch (IOException e){
            Log.e("ServerThread", "stopTcpThread  error "+ e.toString());
        }
    }
}
