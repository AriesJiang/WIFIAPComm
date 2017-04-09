package com.niqiu.interfaces;

/**
 * Created by 毅东 on 2015/11/15.
 */
public interface TcpFileReceiveListener {
    public void onTcpFileReceiveListener(int fileCount, int percent, boolean isSuccessOne, String senderIp);
}
