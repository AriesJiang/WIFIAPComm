package com.niqiu.Broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.niqiu.MainActivity;
import com.niqiu.R;

/**
 * Created by JC001 on 2015/10/22.
 */
public class Receiver1 extends BroadcastReceiver {
    private Context context;
    public static final int NOTIFICATION_ID = 10001;

    public void onReceive(Context context, Intent intent) {
        this.context = context;
        showNotification();
    }

    private void showNotification() {
        CharSequence text1 = "来电话啦...嘿嘿";
        CharSequence text2 = "赶紧接电话，否则误大事了";
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(contentIntent);
        builder.setContentTitle(text1);
        builder.setContentText(text2);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.drawable.stat_sys_wifi_signal_0);
        builder.setContent(null);
        builder.setAutoCancel(true);
        Notification notification = builder.getNotification();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                android.content.Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
