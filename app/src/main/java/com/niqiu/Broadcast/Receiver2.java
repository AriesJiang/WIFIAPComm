package com.niqiu.Broadcast;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by JC001 on 2015/10/22.
 */
public class Receiver2 extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        deleteNotification();
    }

    private void deleteNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Receiver1.NOTIFICATION_ID);
    }
}
