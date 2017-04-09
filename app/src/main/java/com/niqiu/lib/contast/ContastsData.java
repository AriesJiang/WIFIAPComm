package com.niqiu.lib.contast;

import android.os.Environment;

import java.io.File;

/**
 * Created by JC001 on 2015/11/13.
 */
public class ContastsData {

    /**
     * lastRestart记录上次重启时间，小于120s删除app数据
     */
    public static final String PREFERENCE_LASTRESTART = "lastRestart";
    public static final int PREFERENCE_RESTARTDURING = 120000;
    public static final String PREFERENCE_NAME = "NiQiuPreference";// 设置保存时的文件的名称



    /**
     * SD卡的存放路径,建议写成是应用名
     */
    public static final String SD_PATH = "NiQiu";
    public static final File FILE_SDCARD = Environment
            .getExternalStorageDirectory();
    public static final File FILE_LOCAL = new File(FILE_SDCARD,
            ContastsData.SD_PATH);
    public static final File IMAGE_PATH = new File(FILE_LOCAL, "images/avatar");
    public static final File DEBUG_PATH = new File(FILE_LOCAL, "NiQiuFile/debug");
    public static final File DEBUG_PATH2 = new File(FILE_LOCAL, "NiQiuFile/debug2");

}
