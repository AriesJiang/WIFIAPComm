package com.niqiu.net.tcp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.niqiu.contant.Global;
import com.niqiu.interfaces.TcpFileReceiveListener;
import com.niqiu.util.IpMessageConst;
import com.niqiu.util.IpMessageProtocol;
import com.niqiu.util.UsedConst;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;

/**
 * Tcp接收文件线程类
 *
 * @author ccf
 *         <p/>
 *         2012/2/28
 */
public class NetTcpFileReceiveThread implements Runnable {
    private final static String TAG = "NetTcpFileReceiveThread";

    private String[] fileInfos;    //文件信息字符数组
    private String senderIp;
    private long packetNo;    //包编号
    private File savePath;    //文件保存路径

    private String selfName;
    private String selfGroup;

    private Socket socket;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    BufferedOutputStream fbos;
    private byte[] readBuffer = new byte[4 * 1024];
    Handler handler;
    TcpFileReceiveListener receiveListener = null;
    Bundle bundle;
    private Handler mainHandler = null;
//    private final Executor mResponsePoster;

    public NetTcpFileReceiveThread(String packetNo, String senderIp, String[] fileInfos, Handler handler, TcpFileReceiveListener receiveListener) {
        this.packetNo = Long.valueOf(packetNo);
        this.fileInfos = fileInfos;
        this.senderIp = senderIp;
        this.handler = handler;
        this.receiveListener = receiveListener;
        mainHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                if (NetTcpFileReceiveThread.this.receiveListener != null) {

                    int[] sendedPer = (int[]) msg.obj;    //得到信息
                    Bundle bundle = msg.getData();
                    boolean isSuccessOne = bundle.getBoolean("isSuccessOne");
                    Log.d("NetTcpFileReceiveThread", "---mainHandler-handleMessage-"+sendedPer[1]);
                    NetTcpFileReceiveThread.this.receiveListener.onTcpFileReceiveListener(sendedPer[0], sendedPer[1], isSuccessOne, NetTcpFileReceiveThread.this.senderIp);
                }
                super.handleMessage(msg);
            }
        };
//        mResponsePoster = new Executor() {
//            @Override
//            public void execute(Runnable command) {
//                mainHandler.post(command);
//            }
//        };


        bundle = new Bundle();
        bundle.putString("senderIp", senderIp);

        selfName = Global.selfName;
        selfGroup = Global.selfGroup;
        savePath = Global.SAVEPATH;

        //判断接收文件的文件夹是否存在，若不存在，则创建
        File fileDir = savePath;
        if (!fileDir.exists()) {    //若不存在
            fileDir.mkdir();
        }

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        for (int i = 0; i < fileInfos.length; i++) {    //循环接受每个文件
            //注意，这里暂时未处理文件名包含冒号的情况，飞鸽协议规定中若文件名包含冒号，则用双冒号替代。需做处理，这里暂时没做
            String[] fileInfo = fileInfos[i].split(":");    //使用:分隔得到文件信息数组
            for (int j = 0; j < fileInfo.length; j++) {
                Log.e("receive file....", "每一个文件的信息receive info:" + fileInfo[j]);
            }
            //先发送一个指定获取文件的包
            IpMessageProtocol ipmsgPro = new IpMessageProtocol();
            ipmsgPro.setVersion(String.valueOf(IpMessageConst.VERSION));
            ipmsgPro.setCommandNo(IpMessageConst.IPMSG_GETFILEDATA);
            ipmsgPro.setSenderName(selfName);
            ipmsgPro.setSenderHost(selfGroup);
            String additionStr = Long.toHexString(packetNo) + ":" + i + ":" + "0:";
            ipmsgPro.setAdditionalSection(additionStr);

            try {
                socket = new Socket(senderIp, IpMessageConst.PORTNEW);
                Log.d(TAG, "已连接上发送端");
                bos = new BufferedOutputStream(socket.getOutputStream());

                //发送收取文件飞鸽命令
                byte[] sendBytes = ipmsgPro.getProtocolString().getBytes("gbk");
                bos.write(sendBytes, 0, sendBytes.length);
                bos.flush();
                Log.d(TAG, "通过TCP发送接收指定文件命令。命令内容是：" + ipmsgPro.getProtocolString());
                //接收文件
                File receiveFile = new File(savePath, fileInfo[1]);
                if (receiveFile.exists()) {    //若对应文件名的文件已存在，则删除原来的文件
                    receiveFile.delete();
                }
                fbos = new BufferedOutputStream(new FileOutputStream(receiveFile));
                Log.d(TAG, "准备开始接收文件....");
                bis = new BufferedInputStream(socket.getInputStream());
                int len = 0;
                long sended = 0;    //已接收文件大小
                long total = Long.parseLong(fileInfo[2], 16);    //文件总大小

                int temp = 0;
                while ((len = bis.read(readBuffer)) != -1) {
                    fbos.write(readBuffer, 0, len);
                    fbos.flush();

                    sended += len;    //已接收文件大小
                    int sendedPer = (int) (sended * 100 / total);    //接收百分比
                    if (sendedPer - temp > 2) {    //每增加一个百分比，发送一个message
                        if (receiveListener != null) {
//                            receiveListener.onTcpFileReceiveListener(i,sendedPer,false,senderIp);
//                            mResponsePoster.execute(new ResponseDeliveryRunnable(i, sendedPer, false, senderIp));
                            int[] msgObj = {i, sendedPer};
                            Message msg = new Message();
                            msg.what = UsedConst.FILERECEIVEINFO;
                            msg.obj = msgObj;
                            bundle.putBoolean("isSuccessOne", false);
                            msg.setData(bundle);
                            mainHandler.sendMessage(msg);
                            temp = sendedPer;
                        } else {
                            int[] msgObj = {i, sendedPer};
                            Message msg = new Message();
                            msg.what = UsedConst.FILERECEIVEINFO;
                            msg.obj = msgObj;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            temp = sendedPer;
                        }

                    }
                    // 发送完成
                    if (len < readBuffer.length && sended == total) {
                        if (receiveListener != null) {
//                            receiveListener.onTcpFileReceiveListener(i,sendedPer,false,senderIp);
//                            mResponsePoster.execute(new ResponseDeliveryRunnable(i, sendedPer, false, senderIp));
                            int[] msgObj = {i, 100};
                            Message msg = new Message();
                            msg.what = UsedConst.FILERECEIVEINFO;
                            msg.obj = msgObj;
                            bundle.putBoolean("isSuccessOne", false);
                            msg.setData(bundle);
                            mainHandler.sendMessage(msg);
                            break;
                        } else {
                            int[] msgObj = {i, 100};
                            Message msg = new Message();
                            msg.what = UsedConst.FILERECEIVEINFO;
                            msg.obj = msgObj;
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            break;
                        }
                    }
                }

                Log.i(TAG, "第" + (i + 1) + "个文件接收成功，文件名为" + fileInfo[1]);
                Log.i(TAG, "第" + (i + 1) + "个文件接收成功，文件名大小为" + total);
                if (receiveListener != null) {

//                    receiveListener.onTcpFileReceiveListener(i,100,true,senderIp);
//                    mResponsePoster.execute(new ResponseDeliveryRunnable(i, 100, false, senderIp));

                    int[] success = {i + 1, 100};
                    Message msg4success = new Message();
                    msg4success.what = UsedConst.FILERECEIVESUCCESS;
                    msg4success.obj = success;
                    bundle.putBoolean("isSuccessOne",true);
                    msg4success.setData(bundle);
                    mainHandler.sendMessage(msg4success);
                } else {
                    int[] success = {i + 1, fileInfos.length};
                    Message msg4success = new Message();
                    msg4success.what = UsedConst.FILERECEIVESUCCESS;
                    msg4success.obj = success;
                    msg4success.setData(bundle);
                    handler.sendMessage(msg4success);
                }

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "....系统不支持GBK编码");
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "远程IP地址错误");
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "文件创建失败");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "发生IO错误");
            } finally {    //处理

                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    bos = null;
                }

                if (fbos != null) {
                    try {
                        fbos.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    fbos = null;
                }

                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    bis = null;
                }

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    socket = null;
                }
            }
        }
    }

    private class ResponseDeliveryRunnable implements Runnable {

        int fileCount;
        int percent;
        boolean isSuccessOne;
        String senderIp;
        boolean isExecutor = false;

        public ResponseDeliveryRunnable(int fileCount, int percent, boolean isSuccessOne, String senderIp) {
            this.fileCount = fileCount;
            this.percent = percent;
            this.isSuccessOne = isSuccessOne;
            this.senderIp = senderIp;
        }


        @Override
        public void run() {
            if (!isExecutor&&receiveListener != null) {
                isExecutor = true;
                Log.d("ResponseDeliveryRunnable", "-----ResponseDeliveryRunnable----updata percent---" + percent);
                receiveListener.onTcpFileReceiveListener(fileCount, percent, isSuccessOne, senderIp);
            }
        }
    }

}
