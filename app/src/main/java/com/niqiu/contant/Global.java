package com.niqiu.contant;

import android.os.Environment;

import java.io.File;

/**
 * 
 * @Description: TODO(Constant) 
 * @author Snail  (Zhanghf QQ:651555765)
 * @date 2014-4-25 下午4:56:23 
 * @version V1.0
 */
public class Global {
	
	public static String PASSWORD = "niqiu123456";
	public static String HOTPOT_NAME_Head = "niqiu_";
	
	public static int PORT = 1207;
	public static String SITE = "192.168.43.1";
	public static String selfName = "秋秋热点";
	public static String selfGroup = "秋秋";
	
	public static String INT_SERVER_FAIL = "INTSERVER_FAIL";
    public static String INT_SERVER_SUCCESS = "INTSERVER_SUCCESS";
    public static String INT_CLIENT_FAIL = "INTCLIENT_FAIL";
    public static String INT_CLIENT_SUCCESS = "INTCLIENT_SUCCESS";

	/**
	 * SD卡的存放路径
	 */
	public static final String SD_PATH = "NiqiuFile/";
	public static final File FILE_SDCARD = Environment
			.getExternalStorageDirectory();
	public static final File SAVEPATH = new File(FILE_SDCARD,
			Global.SD_PATH);
}
