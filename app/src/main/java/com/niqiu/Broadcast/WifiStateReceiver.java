
package com.niqiu.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.niqiu.WifiHotManager;

/**
 * @Description: TODO(the specified SSID wifi state broadcast listener)
 * @author Snail (Zhanghf QQ:651555765)
 * @date 2014-4-25 上午9:10:33
 * @version V1.0
 */
public class WifiStateReceiver extends BroadcastReceiver {

    private WifiHotManager.WifiBroadCastOperator mWifiOperator;
    private String ssid;
    private WifiHotManager.OperationsType operationsType;

    public WifiStateReceiver(WifiHotManager.WifiBroadCastOperator mWifiOperator, String ssid) {
        this.mWifiOperator = mWifiOperator;
        ssid = ssid;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("WifiStateReceiver", "into onReceive(Context context, Intent intent)");
        // 这个监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.i("WIFI状态", "wifiState-->" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLING:  // 0
                    Log.i("WifiStateReceiver", "WIFI_STATE_DISABLING");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:  // 1
                    Log.i("WifiStateReceiver", "WIFI_STATE_DISABLED");
                    break;
                case WifiManager.WIFI_STATE_ENABLING:  // 2
                    Log.i("WifiStateReceiver", "WIFI_STATE_ENABLING");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:  // 3
                    Log.i("WifiStateReceiver", "WIFI_STATE_ENABLED");
                    if (operationsType != null) {
                        mWifiOperator.operationByType(operationsType, ssid);
                    }
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN: // 4
                    Log.i("WifiStateReceiver", "WIFI_STATE_UNKNOWN");
                    break;
                default:
                        break;
            }
        }
    }

    public void setOperationsType(WifiHotManager.OperationsType operationsType) {
        this.operationsType = operationsType;
    }

}
