package com.niqiu.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.niqiu.WifiHotManager;
import com.niqiu.contant.Global;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Snail  (Zhanghf QQ:651555765)
 * @version V1.0
 * @Description: TODO(wifi scan result broadcast listener)
 * @date 2014-4-25 上午9:06:06
 */
public class WifiScanRsultReciver extends BroadcastReceiver {

    private WifiHotManager.WifiBroadCastOperator mWifiOperator;
    private WifiManager mWifiManager;

    /**
     * scan wifi connect list
     **/
    private List<ScanResult> wifiList = new ArrayList<ScanResult>();

    public WifiScanRsultReciver(WifiHotManager.WifiBroadCastOperator mWifiBroadCastOperator) {
        this.mWifiOperator = mWifiBroadCastOperator;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("WifiScanRsultReciver", "into onReceive(Context context, Intent intent)");
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equalsIgnoreCase(intent.getAction())) {
            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifiList.clear();
            //sav scan results
            wifiList = mWifiManager.getScanResults();
//            for (ScanResult item : mWifiManager.getScanResults()) {
//                if (item.SSID.startsWith(Global.HOTPOT_NAME_Head)) {
//                    wifiList.add(item);
//                }
//            }
            if(wifiList!= null){
                Log.i("WifiScanRsultReciver", "Size of wifiList is===="+ wifiList.size());
            }
            //refresh
            mWifiOperator.disPlayWifiScanResult(wifiList);
        }
    }

}
