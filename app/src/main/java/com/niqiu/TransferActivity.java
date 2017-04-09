package com.niqiu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.niqiu.Adapter.MyTranWIFIUserAdapter;
import com.niqiu.contant.Global;
import com.niqiu.data.TransmissionBean;
import com.niqiu.data.User;
import com.niqiu.interfaces.TcpFileReceiveListener;
import com.niqiu.net.tcp.NetTCPThreadPoolExecutor;
import com.niqiu.net.tcp.NetTcpFileReceiveThread;
import com.niqiu.net.tcp.NetTcpFileSendThread;
import com.niqiu.net.udp.NetThreadHelper;
import com.niqiu.util.IpMessageConst;
import com.niqiu.util.IpMessageProtocol;
import com.niqiu.util.UsedConst;
import com.niqiu.util.WifiUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.ExFilePickerParcelObject;

public class TransferActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    //    Button Test;
    public final String TAG = "TransferActivity";
    private static final int EX_FILE_PICKER_RESULT = 0;

    protected TextView curStatue, curHostIp, savePath;
    protected Button upDataIP;
    protected ListView listView;
    private int notification_id = 9786970;
    private NotificationManager mNotManager;
    private Notification mNotification;

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (WifiUtils.isWifiApEnabled()) {
                    mBuilder.show();
                    return true;
                } else {
                    Log.d(TAG, "我下线啦！！！！！！！！！！");
                    netThreadHelper.noticeOffline();    //通知下线，并且停止监听
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    long curtime;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case IpMessageConst.IPMSG_SENDMSG | IpMessageConst.IPMSG_FILEATTACHOPT: {
                    //收到发送文件请求
                    final String[] extraMsg = (String[]) msg.obj;    //得到附加文件信息,字符串数组，分别放了  IP，附加文件信息,发送者名称，包ID
                    for (int i = 0; i < extraMsg.length; i++) {
                        Log.e("receive file....", "receive info:" + extraMsg[i]);
                    }
                    Log.d("receive file....", "receive file from :" + extraMsg[2] + "(" + extraMsg[0] + ")");
                    Log.d("receive file....", "receive file info:" + extraMsg[1]);
                    byte[] bt = {0x07};        //用于分隔多个发送文件的字符
                    String splitStr = new String(bt);
                    final String[] fileInfos = extraMsg[1].split(splitStr);    //使用分隔字符进行分割

                    Log.d("feige", "收到文件传输请求,共有" + fileInfos.length + "个文件");

                    String infoStr = "发送者IP:\t" + extraMsg[0] + "\n" +
                            "发送者名称:\t" + extraMsg[2] + "\n" +
                            "文件总数:\t" + fileInfos.length + "个";

                    new AlertDialog.Builder(TransferActivity.this)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle("收到文件传输请求")
                            .setMessage(infoStr)
                            .setCancelable(false)
                            .setPositiveButton("接收",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
//                                            NetTCPThreadPoolExecutor.getInstance().execute(new NetTcpFileReceiveThread(extraMsg[3], extraMsg[0], fileInfos, handler, null));
                                            NetTCPThreadPoolExecutor.getInstance().execute(new NetTcpFileReceiveThread(extraMsg[3], extraMsg[0], fileInfos, handler, new TcpFileReceiveListener() {
                                                @Override
                                                public void onTcpFileReceiveListener(int fileCount, int percent, boolean isSuccessOne, String senderIp) {
                                                    if (!isSuccessOne) {
                                                        //遍历上线用户
                                                        for (TransmissionBean item : wifiData) {
                                                            if (TextUtils.equals(item.getUser().getIp(), senderIp)) {
                                                                item.setFileCount(fileCount);
                                                                item.setPercent(percent);
                                                                item.getFileText().setText(fileCount + "/" + item.getFileSize());
                                                                item.getPercentText().setText(percent + "%");
                                                                item.getProgressBar().setVisibility(View.VISIBLE);
                                                                item.getProgressBar().setProgress(percent);
                                                            }
                                                        }
                                                        mNotification.contentView.setProgressBar(R.id.pd_download, 100, percent, false);
                                                        mNotification.contentView.setTextViewText(R.id.fileRec_info, "文件" + fileCount + "接收中:" + percent + "%");
                                                        showNotification();    //显示notification
                                                    } else {
                                                        mNotification.contentView.setTextViewText(R.id.fileRec_info, "第" + fileCount + "个文件接收成功");
//                                                        makeTextShort("第" + successNum[0] + "个文件接收成功");
                                                        long during = 0;
                                                        mNotification.contentView.setTextViewText(R.id.fileRec_info, "所有文件接收成功");
                                                        during = Math.abs(System.currentTimeMillis() - curtime);
                                                        makeTextShort("所有文件接收成功" + during / 1000 + "秒");
                                                        showNotification();
                                                        //遍历上线用户
                                                        for (TransmissionBean item : wifiData) {
                                                            if (TextUtils.equals(item.getUser().getIp(), senderIp) && item.getFileCount() == (item.getFileSize() - 1)) {
                                                                item.getFileText().setText(item.getFileSize() + "/" + item.getFileSize());
                                                                item.getPercentText().setText((during / 1000) + "秒");
                                                                item.getProgressBar().setVisibility(View.GONE);
                                                                item.setFileSize(0);
                                                            }
                                                        }
                                                    }
                                                }
                                            }));

                                            Toast.makeText(TransferActivity.this, "开始接收文件", Toast.LENGTH_SHORT).show();
                                            curtime = System.currentTimeMillis();
                                            TransferActivity.this.showNotification();    //显示notification
                                        }
                                    })
                            .setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //发送拒绝报文
                                            //构造拒绝报文
                                            IpMessageProtocol ipmsgSend = new IpMessageProtocol();
                                            ipmsgSend.setVersion("" + IpMessageConst.VERSION);    //拒绝命令字
                                            ipmsgSend.setCommandNo(IpMessageConst.IPMSG_RELEASEFILES);
                                            ipmsgSend.setSenderName("android飞鸽");
                                            ipmsgSend.setSenderHost("android");
                                            ipmsgSend.setAdditionalSection(extraMsg[3] + "\0");    //附加信息里是确认收到的包的编号

                                            InetAddress sendAddress = null;
                                            try {
                                                sendAddress = InetAddress.getByName(extraMsg[0]);
                                            } catch (UnknownHostException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                            netThreadHelper.sendUdpData(ipmsgSend.getProtocolString(), sendAddress, IpMessageConst.PORT, false);

                                        }
                                    }).show();

                    //遍历上线用户
                    for (TransmissionBean item : wifiData) {
                        if (TextUtils.equals(item.getUser().getIp(), extraMsg[0])) {
                            item.setFileSize(fileInfos.length);
                            item.getFileText().setText("0/" + fileInfos.length);
                            item.getProgressBar().setVisibility(View.VISIBLE);
                        }
                    }

                }
                break;

                //接收文件tcp发送handler
                case UsedConst.FILERECEIVEINFO: {    //更新接收文件进度条
                    int[] sendedPer = (int[]) msg.obj;    //得到信息
                    Bundle bundle = msg.getData();
                    String senderIp = bundle.getString("senderIp");
                    //遍历上线用户
                    for (TransmissionBean item : wifiData) {
                        if (TextUtils.equals(item.getUser().getIp(), senderIp)) {
                            item.setFileCount(sendedPer[0]);
                            item.setPercent(sendedPer[1]);
                            item.getFileText().setText(sendedPer[0] + "/" + item.getFileSize());
                            item.getPercentText().setText(sendedPer[1] + "%");
                            item.getProgressBar().setVisibility(View.VISIBLE);
                            item.getProgressBar().setProgress(sendedPer[1]);
                        }
                    }

                    mNotification.contentView.setProgressBar(R.id.pd_download, 100, sendedPer[1], false);
                    mNotification.contentView.setTextViewText(R.id.fileRec_info, "文件" + (sendedPer[0] + 1) + "接收中:" + sendedPer[1] + "%");

                    showNotification();    //显示notification
                }
                break;

                case UsedConst.FILERECEIVESUCCESS: {    //文件接收成功
                    int[] successNum = (int[]) msg.obj;

                    mNotification.contentView.setTextViewText(R.id.fileRec_info, "第" + successNum[0] + "个文件接收成功");
//                    makeTextShort("第" + successNum[0] + "个文件接收成功");
                    long during = 0;
                    if (successNum[0] == successNum[1]) {
                        mNotification.contentView.setTextViewText(R.id.fileRec_info, "所有文件接收成功");
                        during = Math.abs(System.currentTimeMillis() - curtime);
                        makeTextShort("所有文件接收成功" + during / 1000 + "秒");
                    }
                    showNotification();

                    Bundle bundle = msg.getData();
                    String senderIp = bundle.getString("senderIp");
                    //遍历上线用户
                    for (TransmissionBean item : wifiData) {
                        if (TextUtils.equals(item.getUser().getIp(), senderIp) && item.getFileCount() == (item.getFileSize() - 1)) {
                            item.getFileText().setText(item.getFileSize() + "/" + item.getFileSize());
                            item.getPercentText().setText((during / 1000) + "秒");
                            item.getProgressBar().setVisibility(View.GONE);
                            item.setFileSize(0);
                        }
                    }
                }
                break;
//                ----------------------文件处理--------------
                case IpMessageConst.IPMSG_RELEASEFILES: { //拒绝接受文件,停止发送文件线程
                    Log.d("IPMSG_RELEASEFILES", "拒绝接受文件,停止发送文件线程11111");
                    NetTcpFileSendThread sendThread = mSendHashMap.get(receiverIp);
                    Log.d("IPMSG_RELEASEFILES", "拒绝接受文件,停止发送文件线程" + sendThread);
                    if (sendThread != null && sendThread.server != null) {
                        try {
                            sendThread.server.close();
                            mSendHashMap.remove(receiverIp);
                            Log.d("IPMSG_RELEASEFILES", "拒绝接受文件,停止发送文件线程22222");
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                break;
                case UsedConst.FILESENDSUCCESS: {    //文件发送成功
                    makeTextShort("文件发送成功");
                }
                break;
                case IpMessageConst.IPMSG_BR_ENTRY:
                case IpMessageConst.IPMSG_BR_EXIT:
                case IpMessageConst.IPMSG_ANSENTRY:
                case IpMessageConst.IPMSG_SENDMSG:
                    refreshList();
                    break;
                default:
                    refreshList();
                    break;
            }
        }

    };

    public void makeTextShort(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void makeTextLong(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    protected void showNotification() {
        mNotManager.notify(notification_id, mNotification);
    }

    NetThreadHelper netThreadHelper;

    AlertDialog.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_tran_file);
        WifiUtils.mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        listView = (ListView) findViewById(R.id.listView);
        curStatue = (TextView) findViewById(R.id.curStatue);
        curHostIp = (TextView) findViewById(R.id.curHostIp);
        savePath = (TextView) findViewById(R.id.savePath);
        upDataIP = (Button) findViewById(R.id.upDataIP);
        if (WifiUtils.isWifiApEnabled()) {
            curStatue.setText("已创建传送热点:" + Global.HOTPOT_NAME_Head + WifiUtils.getLocalHostName());
        } else {
            curStatue.setText("当前热点:" + WifiUtils.getSSID());
        }
        curHostIp.setText("当前IP:" + WifiUtils.getLocalIPAddress());
        savePath.setText("文件存储路径:" + Global.SAVEPATH);
        upDataIP.setOnClickListener(this);

        //建立notification
        mNotManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotification = new Notification(android.R.drawable.stat_sys_download, "接收文件", System.currentTimeMillis());
        mNotification.contentView = new RemoteViews(getPackageName(), R.layout.file_download_notification);
        mNotification.contentView.setProgressBar(R.id.pd_download, 100, 0, false);
        Intent notificationIntent = new Intent(this, TransferActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mNotification.contentIntent = contentIntent;

        netThreadHelper = new NetThreadHelper(handler);
        netThreadHelper.connectSocket();    //开始监听数据
        netThreadHelper.noticeOnline();    //广播上线

        mBuilder = new AlertDialog.Builder(TransferActivity.this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("退出提示")
                .setMessage("退出将停止传输！！")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("TAG", "我下线啦！！！！！！！！！！");
                                netThreadHelper.noticeOffline();    //通知下线，并且停止监听
                                finish();
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //发送拒绝报文
                                //构造拒绝报文
                            }
                        });
//        Test = (Button) findViewById(R.id.Test);
//        Test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), ru.bartwell.exfilepicker.ExFilePickerActivity.class);
//                intent.putExtra(ExFilePicker.ENABLE_QUIT_BUTTON, true);
//                if (((CheckBox) findViewById(R.id.only_one_item)).isChecked()) intent.putExtra(ExFilePicker.SET_ONLY_ONE_ITEM, true);
//                if (((CheckBox) findViewById(R.id.filter_listed)).isChecked()) intent.putExtra(ExFilePicker.SET_FILTER_LISTED, new String[] { "jpg", "jpeg" });
//                if (((CheckBox) findViewById(R.id.filter_exclude)).isChecked()) intent.putExtra(ExFilePicker.SET_FILTER_EXCLUDE, new String[] { "jpg" });
//                if (((CheckBox) findViewById(R.id.disable_new_folder_button)).isChecked()) intent.putExtra(ExFilePicker.DISABLE_NEW_FOLDER_BUTTON, true);
//                if (((CheckBox) findViewById(R.id.disable_sort_button)).isChecked()) intent.putExtra(ExFilePicker.DISABLE_SORT_BUTTON, true);
//                if (((CheckBox) findViewById(R.id.enable_quit_button)).isChecked()) intent.putExtra(ExFilePicker.ENABLE_QUIT_BUTTON, true);
//                int checkedChoiceRadio = ((RadioGroup) findViewById(R.id.choice_type)).getCheckedRadioButtonId();
//                if (checkedChoiceRadio == R.id.choice_type_files) intent.putExtra(ExFilePicker.SET_CHOICE_TYPE, ExFilePicker.CHOICE_TYPE_FILES);
//                if (checkedChoiceRadio == R.id.choice_type_directories) intent.putExtra(ExFilePicker.SET_CHOICE_TYPE, ExFilePicker.CHOICE_TYPE_DIRECTORIES);
//                startActivityForResult(intent, EX_FILE_PICKER_RESULT);
//            }
//        });
    }

    //    List<User> wifiData = new ArrayList<User>();
    List<TransmissionBean> wifiData = new ArrayList<TransmissionBean>();
    MyTranWIFIUserAdapter myTranWIFIUserAdapter;

    //更新数据和UI显示
    private void refreshList() {
        //清空数据
        wifiData.clear();
        Map<String, User> currentUsers = new HashMap<String, User>();
        currentUsers.putAll(netThreadHelper.getUsers());
        //遍历currentUsers,更新strGroups和children
        Iterator<String> iterator = currentUsers.keySet().iterator();
        while (iterator.hasNext()) {
            TransmissionBean transmissionBean = new TransmissionBean();
            transmissionBean.setUser(currentUsers.get(iterator.next()));
            wifiData.add(transmissionBean);
        }
        if (myTranWIFIUserAdapter == null) {
            myTranWIFIUserAdapter = new MyTranWIFIUserAdapter(this, wifiData);
            listView.setAdapter(myTranWIFIUserAdapter);
            listView.setOnItemClickListener(this);
        } else {
            myTranWIFIUserAdapter.setList(wifiData);
            myTranWIFIUserAdapter.notifyDataSetChanged();
        }

    }

    private String receiverIp;            //要接收本activity所发送的消息的用户IP
    HashMap<String, NetTcpFileSendThread> mSendHashMap = new HashMap<String, NetTcpFileSendThread>();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EX_FILE_PICKER_RESULT) {
            listView.setClickable(true);
            if (data != null) {
                ExFilePickerParcelObject object = (ExFilePickerParcelObject) data.getParcelableExtra(ExFilePickerParcelObject.class.getCanonicalName());
                if (object.count > 0) {
                    //发送的文件
                    StringBuffer buffer = new StringBuffer();
                    String[] filePathArray = new String[object.count];
                    for (int i = 0; i < object.count; i++) {
                        filePathArray[i] = object.path + object.names.get(i);
                        buffer.append(object.names.get(i));
                        if (i < object.count - 1) buffer.append(", ");
                    }

                    //监听2425端口，准备接受TCP连接请求
                    NetTcpFileSendThread mFileSendThread = new NetTcpFileSendThread(filePathArray, handler);
                    NetTCPThreadPoolExecutor.getInstance().execute(mFileSendThread);
                    mSendHashMap.put(receiverIp, mFileSendThread);

                    //发送传送文件UDP数据报
                    IpMessageProtocol sendPro = new IpMessageProtocol();
                    sendPro.setVersion("" + IpMessageConst.VERSION);
                    sendPro.setCommandNo(IpMessageConst.IPMSG_SENDMSG | IpMessageConst.IPMSG_FILEATTACHOPT);
                    sendPro.setSenderName(Global.selfName);
                    sendPro.setSenderHost(Global.selfGroup);
                    sendPro.setSenderHostPort(Integer.toOctalString(IpMessageConst.PORTNEW));
                    String msgStr = "";    //发送的消息

                    StringBuffer additionInfoSb = new StringBuffer();    //用于组合附加文件格式的sb
                    for (String path : filePathArray) {
                        File file = new File(path);
                        additionInfoSb.append("0:");
                        additionInfoSb.append(file.getName() + ":");
                        additionInfoSb.append(Long.toHexString(file.length()) + ":");        //文件大小十六进制表示
                        additionInfoSb.append(Long.toHexString(file.lastModified()) + ":");    //文件创建时间，现在暂时已最后修改时间替代
                        additionInfoSb.append(IpMessageConst.IPMSG_FILE_REGULAR + ":");
                        byte[] bt = {0x07};        //用于分隔多个发送文件的字符
                        String splitStr = new String(bt);
                        additionInfoSb.append(splitStr);
                        Log.e(TAG, "file.length():::::" + file.length());
                        Log.e(TAG, "additionInfoSb:::::" + additionInfoSb);
                        Log.e(TAG, "path:::::" + path);
                    }

                    sendPro.setAdditionalSection(msgStr + "\0" + additionInfoSb.toString() + "\0");

                    InetAddress sendto = null;
                    try {
                        sendto = InetAddress.getByName(receiverIp);
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        Log.e(TAG, "发送地址有误");
                    }
                    if (sendto != null)
                        netThreadHelper.sendUdpData(sendPro.getProtocolString(), sendto, IpMessageConst.PORT, false);

//                    NetTCPThreadPoolExecutor.getInstance().execute(mFileSendThread);
//                    ((TextView) findViewById(R.id.result)).setText("Count: " + object.count + "\n" + "Path: " + object.path + "\n" + "Selected: " + buffer.toString());
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.listView:
                if (parent.isClickable()) {
                    MyTranWIFIUserAdapter myTranWIFIUserAdapter = (MyTranWIFIUserAdapter) parent.getAdapter();
                    List<TransmissionBean> List = myTranWIFIUserAdapter.getList();
                    User user = List.get(position).getUser();
                    if (TextUtils.equals(user.getIp(), WifiUtils.getLocalIPAddress())) {
                        makeTextLong("不能给自己发文件");
                    } else {
                        synchronized (this) {
                            parent.setClickable(false);
                            receiverIp = user.getIp();
                            Intent intent = new Intent(getApplicationContext(), ru.bartwell.exfilepicker.ExFilePickerActivity.class);
                            intent.putExtra(ExFilePicker.ENABLE_QUIT_BUTTON, true);
                            startActivityForResult(intent, EX_FILE_PICKER_RESULT);
                        }
                    }
                }


                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upDataIP:
                curHostIp.setText("当前IP:" + WifiUtils.getLocalIPAddress());
                if (netThreadHelper == null) {
                    netThreadHelper = new NetThreadHelper(handler);
                    netThreadHelper.connectSocket();    //开始监听数据
                }
                netThreadHelper.noticeOnline();    //广播上线
                break;
        }
    }
}
