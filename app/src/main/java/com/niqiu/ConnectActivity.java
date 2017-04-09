package com.niqiu;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ConnectActivity extends AppCompatActivity {

    Button button, button2;
    ListView listView;
    TextView textView2;
    public WifiManager mWifiManager;
    //描述任何Wifi连接状态
    private WifiInfo mWifiInfo;
    private List<ScanResult> mScanResult;

    private final int SCAN = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCAN :
                    Log.e("ConnectActivity","scan -----------");
                    mWifiManager.startScan();
                    mScanResult =  mWifiManager.getScanResults();
                    listView.setAdapter(new MyAdapter(ConnectActivity.this,mScanResult));
                    mWifiInfo = mWifiManager.getConnectionInfo();
                    textView2.setText("当前网络情况:"+mWifiInfo.getBSSID()+"\n"+mWifiInfo.getMacAddress()+
                            "\n"+mWifiInfo.getSSID()+"\n"+mWifiInfo.getFrequency()+"\n"+mWifiInfo.getIpAddress());
                    mHandler.sendEmptyMessageDelayed(SCAN, 2000);
                    break;
            }

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeMessages(SCAN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        //获取系统Wifi服务   WIFI_SERVICE
        this.mWifiManager = (WifiManager) getSystemService("wifi");
        //获取连接信息
        this.mWifiInfo = mWifiManager.getConnectionInfo();
        textView2 = (TextView) findViewById(R.id.textView2);
        listView = (ListView) findViewById(R.id.listView);
        if (!this.mWifiManager.isWifiEnabled()) {
            Toast.makeText(this, "wifi未打开！", Toast.LENGTH_LONG).show();
        }else {
            mHandler.sendEmptyMessageDelayed(SCAN, 3000);
        }

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWifiManager.isWifiEnabled()){
                    mWifiManager.setWifiEnabled(true);
                    mHandler.sendEmptyMessageDelayed(SCAN, 3000);
                }
            }
        });
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWifiManager.isWifiEnabled()){
                    mWifiManager.setWifiEnabled(false);
                }
            }
        });
    }

    public class MyAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<ScanResult> list;
        public MyAdapter(Context context, List<ScanResult> list) {
            // TODO Auto-generated constructor stub
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = null;
            view = inflater.inflate(R.layout.item_wifi_list, null);
            ScanResult scanResult = list.get(position);
            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(scanResult.SSID);
            TextView signalStrenth = (TextView) view.findViewById(R.id.signal_strenth);
            signalStrenth.setText(String.valueOf(Math.abs(scanResult.level)));
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            //判断信号强度，显示对应的指示图标
            if (Math.abs(scanResult.level) > 100) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_wifi_signal_0));
            } else if (Math.abs(scanResult.level) > 80) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_wifi_signal_1));
            } else if (Math.abs(scanResult.level) > 70) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_wifi_signal_1));
            } else if (Math.abs(scanResult.level) > 60) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_wifi_signal_2));
            } else if (Math.abs(scanResult.level) > 50) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_wifi_signal_3));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.stat_sys_wifi_signal_4));
            }
            return view;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
