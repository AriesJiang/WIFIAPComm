package com.niqiu.lib.crash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.niqiu.R;
import com.niqiu.lib.AppManager.AppManager;
import com.niqiu.lib.SharePreference.SharePreferenceSetting;
import com.niqiu.lib.contast.ContastsData;

/**
 * 在Application中统一捕获异常，保存到文件中下次再打开时上传
 *
 * @author Aries
 * @create 2015-1-14 下午4:29:13
 */
public class CrashHandler implements UncaughtExceptionHandler {

    private String TAG = "CrashHandler";
    /**
     * 系统默认的UncaughtException处理类
     */
    private UncaughtExceptionHandler mDefaultHandler;
    /**
     * CrashHandler实例
     */
    private static CrashHandler INSTANCE;
    /**
     * 用于格式化日期,作为日志文件名的一部分
     */
    @SuppressLint("SimpleDateFormat")
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    /**
     * 用来存储设备信息和异常信息
     */
    private CrashInfo mCrashInfo;

    /**
     * 程序的Context对象
     */
    private Context mContext;

    /**
     * 控制文件位置,同时控制log文件不在debug2保存
     */
    private final boolean isLog = false;
    /**
     *
     */
    private String mLogDir;

    /**
     * 重启所用的Intent
     */
    private PendingIntent restartIntent;

    private static String startClassName = null;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance(String startClassName) {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        INSTANCE.startClassName = startClassName;
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx) {
        mContext = ctx;
        mLogDir = mContext.getApplicationInfo().dataDir + "/log_cache";
        if (!isLog
                && (Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED))) {
            File dir = new File(mLogDir);
            if (!dir.exists())
                dir.mkdirs();
        } else {
            File dir = ContastsData.DEBUG_PATH;
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @SuppressWarnings("ResourceType")
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else { // 如果自己处理了异常，则不会弹出错误对话框，则需要手动退出app
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            // 方案1
            // AppManager.getAppManager().AppExit(mContext);
            // android.os.Process.killProcess(android.os.Process.myPid());
            // System.exit(0);

            // 方案2
            Intent intent = new Intent();
            // 参数1：包名，参数2：程序入口的activity
            Log.e(TAG,
                    "-----startClassName------" + startClassName);
            intent.setClassName(mContext.getPackageName(),
                    startClassName);
            restartIntent = PendingIntent.getActivity(mContext, -1, intent,
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            // 1秒钟后重启应用
            AlarmManager mgr = (AlarmManager) mContext
                    .getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000,
                    restartIntent);

            AppManager.getAppManager().AppExit(mContext);

        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @return true代表处理该异常，不再向上抛异常，
     * false代表不处理该异常(可以将该log信息存储起来)然后交给上层(这里就到了系统的异常处理)去处理，
     * 简单来说就是true不会弹出那个错误提示框，false就会弹出
     */
    private boolean handleException(final Throwable ex) {
        Log.e(TAG, "-----handleException--111111----");
        if (ex == null) {
            return false;
        }
        Log.e(TAG, "-----handleException--222222----");
        // final String msg = ex.getLocalizedMessage();
        // final StackTraceElement[] stack = ex.getStackTrace();
        // final String message = ex.getMessage();

        boolean isUp = true;
        // if (isUp) {
        // // 使用Toast来显示异常信息
        // new Thread() {
        // @Override
        // public void run() {
        // Looper.prepare();
        // Toast.makeText(mContext, "亲爱的用户，应用将在2秒后重启",
        // Toast.LENGTH_LONG).show();
        // Looper.loop();
        // }
        //
        // }.start();
        // }
        cleanApplicationData();
        collectDeviceInfo(mContext);
        saveCrashInfo2File(ex);

        return isUp;
    }

    /**
     * 当遇到频繁重启的时候，先清除应用中的文件，需要在保存错误报告之前调用
     */
    private void cleanApplicationData() {
        Object lastRestart = SharePreferenceSetting.getSp(
                ContastsData.PREFERENCE_NAME, ContastsData.PREFERENCE_LASTRESTART,
                mContext, new TypeToken<Long>() {
                }.getType());
        long lastRestartMtime = (Long) (lastRestart == null ? 0L : lastRestart);
        long curtime = System.currentTimeMillis();
        long during = Math.abs(curtime - lastRestartMtime);
        if (true && during < ContastsData.PREFERENCE_RESTARTDURING) {
//            DataCleanManager.cleanApplicationData(mContext);
        }
        SharePreferenceSetting.setSp(ContastsData.PREFERENCE_NAME,
                ContastsData.PREFERENCE_LASTRESTART, mContext,
                System.currentTimeMillis());
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        String versionName = null, versionCode = null;
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                versionName = pi.versionName == null ? "null" : pi.versionName;
                versionCode = pi.versionCode + "";
                Log.e(TAG, "versionName=====" + versionName);
                Log.e(TAG, "versionCode=====" + versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info");
        }

        // DetailInfo mDetailInfo = new DetailInfo();
        // mCrashInfo = new CrashInfo();
        // ArrayList<String> mField = new ArrayList<String>();
        // Field[] fieldsCrash = mDetailInfo.getClass().getDeclaredFields();
        //
        // //先通过反射，将需要提交的字段从自己定义的类中拿出来
        // for (Field field : fieldsCrash) {
        // try {
        // field.setAccessible(true);
        // // Log.d(TAG, field.getName() + " : " + field.get(mDetailInfo));
        // mField.add(field.getName());
        // } catch (Exception e) {
        // Log.e(TAG, "an error occured when collect crash info");
        // Log.e(TAG, e.toString());
        // }
        // }
        // Log.e(TAG, "---------------------------------");
        //
        // //为成员相同的字段赋值
        // Field[] fields = Build.class.getDeclaredFields();
        // for (Field field : fields) {
        // try {
        // field.setAccessible(true);
        // // infos.put(field.getName(), field.get(null).toString());
        // Log.e(TAG, field.getName() + " : " + field.get(null));
        //
        // for (int i = 0; i < mField.size(); i++) {
        // if (TextUtils.equals(mField.get(i), field.getName())) {
        // Method method = mDetailInfo.getClass()
        // .getDeclaredMethod("set" + field.getName(),
        // new Class[] { String.class });
        // method.invoke(mDetailInfo, (String) field.get(null));
        // }
        // }
        // } catch (Exception e) {
        // Log.e(TAG, "an error occured when collect crash info");
        // Log.e(TAG, e.toString());
        // }
        // }

        mCrashInfo = new CrashInfo();
        DetailInfo mDetailInfo = DeviceInfo.getDeviceInfo(mContext);

        try {
//            mCrashInfo.setUserIdentityCode(App.getUserIdentityCode());
            mCrashInfo.setLevel("ERROR");
            mCrashInfo.setProduct(mContext.getString(R.string.app_name)
                    + versionCode + "_V" + versionName);
            mCrashInfo.setSupportEmail("me.mory@qq.com");
            mCrashInfo.setEnvironment(gson.toJson(mDetailInfo).trim()
                    .toString());
            mCrashInfo
                    .setMtime(Long.toString(System.currentTimeMillis() / 1000));
            Log.e(TAG, "-----gson-----"
                    + gson.toJson(mDetailInfo).trim().toString());
        } catch (Exception e) {
            Log.e(TAG, "an error occured when collect user info");
            Log.e(TAG, e.toString());
        }

    }

    Gson gson = new Gson();
    private String start = "crash-";

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        // for (Map.Entry<String, String> entry : infos.entrySet()) {
        // String key = entry.getKey();
        // String value = entry.getValue();
        // sb.append(key + "=" + value + "\n");
        // }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        mCrashInfo.setInfo(result);

        sb.append(gson.toJson(mCrashInfo).trim().toString());
        Log.e(TAG, "-----gson-----" + gson.toJson(mCrashInfo).trim().toString());

        CrashInfo test = gson.fromJson(sb.toString(), CrashInfo.class);
        try {
            Log.e(TAG,
                    "-----DetailInfo-----"
                            + gson.fromJson(test.getEnvironment(),
                            DetailInfo.class).toString());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = start + time + "-" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {

                if (isLog) {
                    // 1111 用于备份，同时让没root权限的手机能查看
                    File dir = ContastsData.DEBUG_PATH2;
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File path0 = new File(dir.getPath(), fileName);

                    // 为每个头像生成一个带当前时间的名字
                    FileOutputStream fos0 = new FileOutputStream(path0);
                    fos0.write(sb.toString().getBytes());
                    if (fos0 != null) {
                        fos0.flush();
                        fos0.close();
                    }
                }

                String dirPath = getPathString();
                // 22222222
                File path = new File(dirPath, fileName);

                // 为每个头像生成一个带当前时间的名字
                FileOutputStream fos = new FileOutputStream(path);
                fos.write(sb.toString().getBytes());
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }

            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...");
        }
        return null;
    }

    /**
     * Get CrashInfo info
     *
     * @param fileName
     * @return
     */
    public synchronized CrashInfo getDebug(String fileName) {
        return decodeInfo(getString(fileName));
    }

    /**
     * Get String Log
     *
     * @param fileName
     * @return
     */
    public synchronized String getString(String fileName) {
        File file = new File(getPathString(), fileName);
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        InputStream ins = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        String line;

        try {
            ins = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(ins));
            while ((line = reader.readLine()) != null) {
                // buffer.append(line).append("\r\n");
                buffer.append(line);
            }

            if (reader != null) {
                reader.close();
            }
            if (ins != null) {
                ins.close();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // Use try instead of throw IOException as we need to make sure we
            // got the weather info
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        Log.e(TAG, buffer.toString());
        return buffer.toString();
    }

    /**
     * decode String to CrashInfo
     *
     * @param encodedJsonArray
     * @return
     */
    private CrashInfo decodeInfo(String encodedJsonArray) {

        CrashInfo result = null;
        Gson gson = new Gson();

        try {
            Log.e(TAG, "---encodedJsonArray-----" + encodedJsonArray);
            result = gson
                    .fromJson(encodedJsonArray.toString(), CrashInfo.class);

        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, String.format(
                    "Cant' create jaon array from String [%s]",
                    encodedJsonArray));
            e.printStackTrace();
            return null;
        }
        return result;
    }

    File[] files;
    int max = 0;

    /**
     * post log files to server
     */
    public void postLogs() {
        Log.i(TAG, "*****postLog****");
        File mFile = new File(getPathString());
        if (!mFile.exists()) {
            return;
        }
        // files = mFile.listFiles();
        // for (int i = 1; i < files.length; i++) {
        // if (files[max].lastModified() < files[i].lastModified()) {
        // max = i;
        // }
        // }
        files = getCrashReportFiles();
        for (int i = 0; i < files.length; i++) {
            Log.e(TAG, "***************" + files[i].getName());
            postLogfile(files[i].getName());
        }
    }

    /**
     * 获取错误报告文件名
     *
     * @return
     */
    private File[] getCrashReportFiles() {
        File filesDir = new File(getPathString());
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(start);
            }
        };
        return filesDir.listFiles(filter);
    }

    /**
     * post log file to server
     */
    public void postLogfile(final String fileName) {
        Log.i(TAG, "*****postLogfile****");

        Log.e(TAG, "***************" + fileName);

    }

    private String getPathString() {
        String dirPath;
        if (isLog) {
            // 未发布模式
            File dir = ContastsData.DEBUG_PATH;
            dirPath = dir.getPath();
        } else {
            // 发布模式
            dirPath = mLogDir;
        }
        return dirPath;

    }
}
