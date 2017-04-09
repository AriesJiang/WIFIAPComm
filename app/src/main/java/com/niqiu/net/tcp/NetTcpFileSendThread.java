package com.niqiu.net.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

import com.niqiu.util.IpMessageConst;
import com.niqiu.util.IpMessageProtocol;
import com.niqiu.util.UsedConst;

/**
 * Tcp发送文件线程
 *
 * @author ccf
 *         <p/>
 *         2012/2/28
 */
public class NetTcpFileSendThread implements Runnable {
    private final String TAG = "NetTcpFileSendThread";
    private String[] filePathArray;    //保存发送文件路径的数组

    public ServerSocket server;
    private Socket socket;
    private byte[] readBuffer = new byte[4 * 1024];
    Handler handler;

    public NetTcpFileSendThread(String[] filePathArray, Handler handler){
        this.filePathArray = filePathArray;
        this.handler = handler;

        while (true) {
            try {
                IpMessageConst.PORTNEW++;
                Log.e(TAG, "tcp绑定新端口" + IpMessageConst.PORTNEW);
                server = new ServerSocket(IpMessageConst.PORTNEW);
                break;
            } catch (BindException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "tcp绑定端口" + IpMessageConst.PORTNEW + "失败");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "tcp绑定端口" + IpMessageConst.PORTNEW + "失败");
            }
        }


    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        for (int i = 0; i < filePathArray.length; i++) {
            try {
                socket = server.accept();
                Log.i(TAG, "与IP为" + socket.getInetAddress().getHostAddress() + "的用户建立TCP连接");
                BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
//				DataInputStream dis = new DataInputStream(bis);
//				String ipmsgStr = dis.readUTF();
                int mlen = bis.read(readBuffer);
                String ipmsgStr = new String(readBuffer, 0, mlen, "gbk");


                Log.d(TAG, "收到的TCP数据信息内容是：" + ipmsgStr);

                IpMessageProtocol ipmsgPro = new IpMessageProtocol(ipmsgStr);
                String fileNoStr = ipmsgPro.getAdditionalSection();
                String[] fileNoArray = fileNoStr.split(":");
                int sendFileNo = Integer.valueOf(fileNoArray[1]);

                Log.d(TAG, "本次发送的文件具体路径为" + filePathArray[sendFileNo]);
                File sendFile = new File(filePathArray[sendFileNo]);    //要发送的文件
                BufferedInputStream fbis = new BufferedInputStream(new FileInputStream(sendFile));

                int rlen = 0;
                long sum = 0;

                while ((rlen = fbis.read(readBuffer)) != -1) {
                    bos.write(readBuffer, 0, rlen);
                    sum = sum + rlen;
                    Log.i(TAG, "文件发送字节****" + sum);
                }
                bos.flush();
                Log.i(TAG, "文件发送成功");

                if (bis != null) {
                    bis.close();
                    bis = null;
                }

                if (fbis != null) {
                    fbis.close();
                    fbis = null;
                }

                if (bos != null) {
                    bos.close();
                    bos = null;
                }

                if (i == (filePathArray.length - 1)) {
                    handler.sendEmptyMessage(UsedConst.FILESENDSUCCESS);    //文件发送成功
                }
                Log.i(TAG, "文件发送成功-------回收完成");

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "接收数据时，系统不支持GBK编码");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "发生IO错误");
                Log.e(TAG, e.toString());
                break;
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
//                        socket.
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    socket = null;
                }
            }

        }
        Log.d(TAG, "------------发送所有文件完毕--关闭server---------");
        try {
            server.close();
            Log.d(TAG, "server 关闭成功");
        } catch (IOException e) {
            Log.d(TAG, "server关闭报错：：" + e.toString());
            e.printStackTrace();
        }
    }

}
