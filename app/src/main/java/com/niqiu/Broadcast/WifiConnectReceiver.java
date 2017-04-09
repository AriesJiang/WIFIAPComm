package com.niqiu.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.niqiu.WifiHotManager;
import com.niqiu.contant.Global;

/**
 * 
 * @Description: TODO(wifi connect broadcast listener) 
 * @author Snail  (Zhanghf QQ:651555765)
 * @date 2014-5-4 下午3:24:25 
 * @version V1.0
 */
public class WifiConnectReceiver extends BroadcastReceiver {

	private WifiHotManager.WifiBroadCastOperator mWifiOperator;
	private WifiManager mWifiManager;

	public WifiConnectReceiver(WifiHotManager.WifiBroadCastOperator mWifiOperator) {
		this.mWifiOperator = mWifiOperator;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
	    Log.i("WifiConnectReceiver", "into onReceive(Context context, Intent intent)");
		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			//receivew
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				mWifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
				String SSID = wifiInfo.getSSID();
				
				switch (networkInfo.getState()) {
				/**State : CONNECTING, CONNECTED, SUSPENDED, DISCONNECTING, DISCONNECTED, UNKNOWN**/
				case CONNECTED:
					Log.e("APActivity", "CONNECTED");
					if(!TextUtils.isEmpty(SSID)&&SSID.contains(Global.HOTPOT_NAME_Head)) {
					    //refresh
						mWifiOperator.disPlayWifiConnResult(true, wifiInfo, "成功连接"+SSID);
					} else {
						mWifiOperator.disPlayWifiConnResult(false, wifiInfo,"连接失败");
					}
					break;
				case CONNECTING:
					Log.e("APActivity", "CONNECTING");
					mWifiOperator.disPlayWifiConnResult(false, wifiInfo, "正在连接...");
					break;
				case DISCONNECTED:
					Log.e("APActivity", "DISCONNECTED");
					mWifiOperator.disPlayWifiConnResult(false, wifiInfo, "没链接任何传送热点");
					break;
				case DISCONNECTING:
					Log.e("APActivity", "DISCONNECTING");
					break;
				case SUSPENDED:
					Log.e("APActivity", "SUSPENDED");
					break;
				case UNKNOWN:
					Log.e("APActivity", "UNKNOWN");
					break;
				default:
					break;
				}
			}
		}
	}

}
