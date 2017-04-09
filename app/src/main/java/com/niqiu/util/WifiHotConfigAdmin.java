package com.niqiu.util;

import android.net.wifi.WifiConfiguration;
import android.util.Log;

/**
 * @Description: TODO(wifi config info tools:1.no password; 2.Wep; 3. WAP)
 * @author Snail (Zhanghf QQ:651555765)
 * @date 2014-4-24 下午7:43:20
 * @version V1.0
 */
public class WifiHotConfigAdmin {

	public static String TAG = "WifiConfigurationAdmin";

	/**
	 * no password
	 * 
	 * @param ssid
	 * @param password
	 * @return wificonfig
	 */
	public static WifiConfiguration createWifiNoPassInfo(String ssid,
			String password) {
		WifiConfiguration wifiConfiguration = new WifiConfiguration();
		wifiConfiguration = createWifiInfo(wifiConfiguration, ssid, password);
		//Up to four WEP keys, don't forget ESC 
		wifiConfiguration.wepKeys[0] = "\"" + "" + "\"";
		//Sets the bit at index index to true 
		wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		wifiConfiguration.wepTxKeyIndex = 0;
		wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
		return wifiConfiguration;
	}
	
	/**
	 * Wep
	 * @param ssid
	 * @param password
	 * @return
	 */
    public static WifiConfiguration createWifiWepInfo(String ssid, String password) {

        Log.v(TAG, "into WIFICIPHER_WEP   SSID = " + ssid + "  Password = " + password);
        WifiConfiguration config = new WifiConfiguration();
        config = createWifiInfo(config, ssid, password);
        config.hiddenSSID = true;
        config.wepKeys[0] = "\"" + password + "\"";
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        config.wepTxKeyIndex = 0;
        Log.v(TAG, "out WIFICIPHER_WEP   SSID = " + ssid + "  Password = " + password);
        return config;

    }

    /**
     * WPA
     * @param ssid
     * @param password
     * @return
     */
    public static WifiConfiguration createWifiWpaInfo(String ssid, String password) {

        Log.v(TAG, "into WIFICIPHER_WPA   SSID = " + ssid + "  Password = " + password);
        WifiConfiguration config = new WifiConfiguration();
        config = createWifiInfo(config, ssid, password);
        config.preSharedKey = "\"" + password + "\"";
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        Log.v(TAG, "out WIFICIPHER_WPA   SSID = " + ssid + "  Password = " + password);
        return config;

    }

	private static WifiConfiguration createWifiInfo(
			WifiConfiguration wifiConfiguration, String SSID, String password) {
		Log.v(TAG, "into wifi热点连接配置   SSID = " + SSID + "  Password = "
				+ password);
		// clear config attributes
		wifiConfiguration.allowedAuthAlgorithms.clear(); // Defaults to automatic selection.
		wifiConfiguration.allowedGroupCiphers.clear(); // Defaults to CCMP TKIP WEP104 WEP40.
		wifiConfiguration.allowedKeyManagement.clear();// Defaults to WPA-PSK WPA-EAP.
		wifiConfiguration.allowedPairwiseCiphers.clear(); // Defaults to CCMP TKIP.
		wifiConfiguration.allowedProtocols.clear(); // Defaults to WPA RSN.
		wifiConfiguration.SSID = "\"" + SSID + "\"";
		wifiConfiguration.priority = 0; // when choosing an access point with which to associate.

		return wifiConfiguration;
	}
}
