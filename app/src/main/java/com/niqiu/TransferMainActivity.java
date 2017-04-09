package com.niqiu;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.niqiu.Adapter.MyTranWIFIAdapter;
import com.niqiu.contant.Global;
import com.niqiu.util.WifiUtils;

import java.util.List;

/**
 * 传输文件主界面
 */
public class TransferMainActivity extends AppCompatActivity implements WifiHotManager.WifiBroadCastOperator, AdapterView.OnItemClickListener {

    Button startAp, next;
    ListView listView;
    TextView curStatue;
    public static final String TAG = "TransferMainActivity";

    private WifiHotManager mWifiHotManager;
    private List<ScanResult> wifiList;
    public String mSSID;

    private MyTranWIFIAdapter myAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        if (!WifiUtils.isWifiApEnabled()){
            listView.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessage(0x789);
        }else{
            listView.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mHandler != null){
            mHandler.removeMessages(0x789);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWifiHotManager!=null){
            mWifiHotManager.unRegisterWifiScanBroadCast();
            mWifiHotManager.unRegisterWifiStateBroadCast();
//            mWifiHotManager.disableWifiHot();
        }
//        if (mHandler != null){
//            mHandler.removeMessages(0x789);
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiscanlist);
        //wifi管理类
        mWifiHotManager = WifiHotManager.getInstance(this, TransferMainActivity.this);
        curStatue = (TextView) findViewById(R.id.curStatue);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        doWhenWIFIstatueChanged();

        startAp = (Button) findViewById(R.id.startAp);
        startAp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close wifi and create hotpot
                if (mHandler != null) {
                    mHandler.removeMessages(0x789);
                }
                boolean isSucceed = mWifiHotManager
                        .startApWifiHot(Global.HOTPOT_NAME_Head + WifiUtils.getLocalHostName());
                if (isSucceed) {
                    curStatue.setText("已创建传送热点:" + Global.HOTPOT_NAME_Head + WifiUtils.getLocalHostName());
                    if (myAdapter != null) {
                        myAdapter.setList(null);
                        myAdapter.notifyDataSetChanged();
                    }
                    listView.setVisibility(View.GONE);
                    Toast.makeText(TransferMainActivity.this, "热点正在创建",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TransferMainActivity.this, "热点创建失败！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWifiHotManager!=null){
                    mWifiHotManager.unRegisterWifiScanBroadCast();
                    mWifiHotManager.unRegisterWifiStateBroadCast();
                }
                if (mHandler != null){
                    mHandler.removeMessages(0x789);
                }
                startActivity(new Intent(TransferMainActivity.this, TransferActivity.class));
            }
        });
    }

    public void doWhenWIFIstatueChanged(){
        Log.i(TAG, "-----doWhenWIFIstatueChanged---------");
        WifiUtils.mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (!WifiUtils.isWifiConnect() && !WifiUtils.isWifiApEnabled()) { // 无开启热点无连接WIFI
            curStatue.setText("已连接传送热点:无");
        }
        if (WifiUtils.isWifiConnect()) { // Wifi已连接
            curStatue.setText("已连接WIFI:"+ WifiUtils.getSSID());
        }

        if (WifiUtils.isWifiApEnabled()) { // 已开启热点
            if (!TextUtils.isEmpty(WifiUtils.getApSSID())) {
                curStatue.setText("已连接传送热点:"+ WifiUtils.getSSID());
            }
            else {
                curStatue.setText("WiFi已开启，未连接");
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.i(TAG, "into onBackPressed()");
            mWifiHotManager.unRegisterWifiScanBroadCast();
            mWifiHotManager.unRegisterWifiStateBroadCast();
//            mWifiHotManager.disableWifiHot();
            this.finish();
            Log.i(TAG, "out onBackPressed()");
            return true;
        }
        return true;
    }

    @Override
    public void disPlayWifiScanResult(List<ScanResult> wifiList) {
        Log.i(TAG, "into 刷新wifi热点列表");
        if (null == myAdapter) {
            Log.i(TAG, "into 刷新wifi热点列表 adapter is null！");
            this.wifiList = wifiList;
            myAdapter = new MyTranWIFIAdapter(this, wifiList);
            listView.setAdapter(myAdapter);
        } else {
            Log.i(TAG, "into 刷新wifi热点列表 adapter is not null！");
            this.wifiList = wifiList;
            myAdapter.setList(wifiList);
            myAdapter.notifyDataSetChanged();
        }
//        mWifiHotManager.unRegisterWifiScanBroadCast();
        Log.i(TAG, "out 刷新wifi热点列表");
    }

    @Override
    public boolean disPlayWifiConnResult(boolean result, WifiInfo wifiInfo, String connStaute) {
        if (result) {
            curStatue.setText(connStaute);
        } else {
            curStatue.setText(connStaute);
        }
        doWhenWIFIstatueChanged();
        return false;
    }

    @Override
    public void operationByType(WifiHotManager.OperationsType operationsType, String SSID) {
        Log.i(TAG, "into operationByType！type = " + operationsType);
        if (operationsType == WifiHotManager.OperationsType.CONNECT) {
            //reconnect hotpot
            mWifiHotManager.connectToHotpot(SSID, wifiList, Global.PASSWORD);
        } else if (operationsType == WifiHotManager.OperationsType.SCAN) {
            mWifiHotManager.scanWifiHot();
        }
        Log.i(TAG, "out operationByType！");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.listView:
                if (mHandler != null) {
                    mHandler.removeMessages(0x789);
                }
                ScanResult result = wifiList.get(position);
                // refresh current mSSID
                mSSID = result.SSID;
                Log.i(TAG, "into  onItemClick() SSID= " + result.SSID);
                // connect hotpot
                mWifiHotManager.connectToHotpot(result.SSID, wifiList,
                        Global.PASSWORD);
                Log.i(TAG, "out  onItemClick() SSID= " + result.SSID);
                break;

            default:
                break;
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x789) {
                mWifiHotManager.scanWifiHot();
                mHandler.sendEmptyMessageDelayed(0x789, 2000);
            }
        }
    };

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
