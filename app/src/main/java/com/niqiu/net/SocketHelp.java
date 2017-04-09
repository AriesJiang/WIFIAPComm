package com.niqiu.net;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by JC001 on 2015/10/23.
 */
public class SocketHelp implements Runnable {

    private static SocketHelp mSocketHelp;
    private static byte[] lock = new byte[0]; // 特殊的instance变量

    public static SocketHelp newInstance() {
        if (mSocketHelp == null)
            synchronized (lock) {
                if (mSocketHelp == null) {
                    mSocketHelp = new SocketHelp();
                }
            }
        return mSocketHelp;
    }

    // 声明一个ServerSocket对象
    ServerSocket serverSocket = null;
    private Thread tcpThread = null;

    public void ServerReceviedByTcp() {
        Log.e("SocketHelp", "***ServerReceviedByTcp***");
        try {
            // 创建一个ServerSocket对象，并让这个Socket在1989端口监听
            serverSocket = new ServerSocket(2013);
            if (tcpThread == null) {
                tcpThread = new Thread(this);
                tcpThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectServerWithTCPSocket(final Context context) {
    new Thread(){
        @Override
        public void run() {
            super.run();
            Socket socket;
            try {// 创建一个Socket对象，并指定服务端的IP及端口号
//                socket = new Socket("255.255.255.255", 2013);
                socket = new Socket(getGateWayIP(context), 2013);

//            // 创建一个InputStream用户读取要发送的文件。
//            InputStream inputStream = new FileInputStream("e://a.txt");
//            // 获取Socket的OutputStream对象用于发送数据。
//            OutputStream outputStream = socket.getOutputStream();
//            // 创建一个byte类型的buffer字节数组，用于存放读取的本地文件
//            byte buffer[] = new byte[4 * 1024];
//            int temp = 0;
//            // 循环读取文件
//            while ((temp = inputStream.read(buffer)) != -1) {
//                // 把数据写入到OuputStream对象中
//                outputStream.write(buffer, 0, temp);
//            }
//            // 发送读取的数据到服务端
//            outputStream.flush();

                /** 或创建一个报文，使用BufferedWriter写入,看你的需求 **/
                String socketData = "[2143213;21343fjks;213]";
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream()));
                writer.write(socketData.replace("\n", " ") + "\n");
                writer.flush();
                /************************************************/
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }.start();
    }

    public void stopTcpThread() {
        if (tcpThread != null) {
            tcpThread.interrupt();
        }
    }

    /**
     * 得到网关的IP地址，即路由器ip地址
     *
     * @param context
     * @return
     */
    public String getGateWayIP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ipStr = int2string(wifiManager.getDhcpInfo().gateway);
        Log.e("SocketHelp", "***getGateWayIP***"+ipStr);
        return ipStr;
    }

    /**
     * 得到wifi连接的IP地址
     *
     * @param context
     * @return
     */
    public String getWifiIP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddr = wifiInfo.getIpAddress();
        String ipStr = int2string(ipAddr);
        return ipStr;
    }

    /**
     * 输入int 得到String类型的ip地址
     *
     * @param i
     * @return
     */
    private static String int2string(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    @Override
    public void run() {
        try {// 创建一个Socket对象，并指定服务端的IP及端口号
            // 调用ServerSocket的accept()方法，接受客户端所发送的请求，
            // 如果客户端没有发送数据，那么该线程就停滞不继续
            Log.e("SocketHelp", "***serverSocket.accept()***00000");
            Socket socket = serverSocket.accept();
            Log.e("SocketHelp", "***serverSocket.accept()***11111");
            // 从Socket当中得到InputStream对象
            InputStream inputStream = socket.getInputStream();
            Log.e("SocketHelp", "***socket.getInputStream()***");
            byte buffer[] = new byte[1024 * 4];
            int temp = 0;
            // 从InputStream当中读取客户端所发送的数据
            while ((temp = inputStream.read(buffer)) != -1) {
                System.out.println(new String(buffer, 0, temp));
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
