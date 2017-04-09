package com.niqiu;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.niqiu.Broadcast.Receiver2;

/**
 * Created by JC001 on 2015/10/22.
 */
public class BroadcastActivity extends Activity {
    private final String ACTION_SEND = "com.forrest.action.SENDMESSAGE",
            ACTION_CLEAR = "com.forrest.action.CLEARNOTIFICATION";
    Receiver2 receiver2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        receiver2 = new Receiver2();
        registerReceiver(receiver2, new IntentFilter("com.forrest.action.CLEARNOTIFICATION"));

        ((Button) findViewById(R.id.send)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickMenuItem(ACTION_SEND);
            }
        });
        ((Button) findViewById(R.id.cancle)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickMenuItem(ACTION_CLEAR);
            }
        });
    }

    private void clickMenuItem(final String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver2 != null) {
            unregisterReceiver(receiver2);
            receiver2 = null;
        }
    }
}
