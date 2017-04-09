package com.niqiu.interfaces;


import com.niqiu.data.ChatMessage;

/**
 * 接收消息监听的listener接口
 * @author ccf
 *
 */
public interface ReceiveMsgListener {
	public boolean receive(ChatMessage msg);

}
