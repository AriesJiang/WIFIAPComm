package com.niqiu;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.niqiu.Adapter.MyConnAdapter;
import com.niqiu.contant.Global;

import java.util.ArrayList;
import java.util.List;

public class ConnectWIFIActivity extends AppCompatActivity implements WifiHotManager.WifiBroadCastOperator, AdapterView.OnItemClickListener {

    Button button, button2, closeWIFI;
    ListView listView;
    TextView textView2, curStatue;
    public static final String TAG = "ConnectWIFIActivity";

    private WifiHotManager mWifiHotManager;
    private List<ScanResult> wifiList;
    public String mSSID;

    private MyConnAdapter myAdapter;
    private ArrayList<ScanResult> mAPScanResult = new ArrayList();//检测到热点信息列表

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        //wifi管理类
        mWifiHotManager = WifiHotManager.getInstance(this, ConnectWIFIActivity.this);
        curStatue = (TextView) findViewById(R.id.curStatue);
        textView2 = (TextView) findViewById(R.id.textView2);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        closeWIFI = (Button) findViewById(R.id.closeWIFI);
        closeWIFI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifiHotManager.deleteMoreCon(mSSID);
                mWifiHotManager.setConnecting(false);
            }
        });
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWifiHotManager.scanWifiHot();
            }
        });
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close wifi and create hotpot
                boolean isSucceed = mWifiHotManager
                        .startApWifiHot(Global.HOTPOT_NAME_Head+getLocalHostName());
                if (isSucceed) {
                    if (myAdapter != null) {
                        myAdapter.setList(null);
                        myAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(ConnectWIFIActivity.this, "热点正在创建",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ConnectWIFIActivity.this, "热点创建失败！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.i(TAG, "into onBackPressed()");
            mWifiHotManager.unRegisterWifiScanBroadCast();
            mWifiHotManager.unRegisterWifiStateBroadCast();
            mWifiHotManager.disableWifiHot();
            this.finish();
            Log.i(TAG, "out onBackPressed()");
            return true;
        }
        return true;
    }

    /**获取手机信息**/
    public String getLocalHostName() {
        String str1 = Build.BRAND; //主板
        String str2 = Build.MODEL;  //机型
        if (-1 == str2.toUpperCase().indexOf(str1.toUpperCase()))
            str2 = str1 + "_" + str2;
        return str2;
    }

    @Override
    public void disPlayWifiScanResult(List<ScanResult> wifiList) {
        Log.i(TAG, "into 刷新wifi热点列表");
        if (null == myAdapter) {
            Log.i(TAG, "into 刷新wifi热点列表 adapter is null！");
            this.wifiList = wifiList;
            myAdapter = new MyConnAdapter(this, wifiList);
            listView.setAdapter(myAdapter);
        } else {
            Log.i(TAG, "into 刷新wifi热点列表 adapter is not null！");
            this.wifiList = wifiList;
            myAdapter.setList(wifiList);
            myAdapter.notifyDataSetChanged();
        }
        mWifiHotManager.unRegisterWifiScanBroadCast();
        Log.i(TAG, "out 刷新wifi热点列表");
    }

    @Override
    public boolean disPlayWifiConnResult(boolean result, WifiInfo wifiInfo, String connStaute) {
        if (result) {
            curStatue.setText(connStaute);
            closeWIFI.setVisibility(View.VISIBLE);
        }else {
            curStatue.setText(connStaute);
            closeWIFI.setVisibility(View.INVISIBLE);
        }
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
