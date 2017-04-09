package com.niqiu;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.niqiu.Broadcast.WifiConnectReceiver;
import com.niqiu.Broadcast.WifiScanRsultReciver;
import com.niqiu.Broadcast.WifiStateReceiver;
import com.niqiu.contant.Global;
import com.niqiu.util.WifiHotAdmin;
import com.niqiu.util.WifiHotConfigAdmin;

import java.util.List;

/**
 * @author Snail (Zhanghf QQ:651555765)
 * @version V1.0
 * @Description: TODO(wifi hot manager)
 * @date 2014-4-24 下午7:43:20
 */
public class WifiHotManager {

    public static String TAG = WifiHotManager.class.getName();

    private static WifiHotManager instance = null;
    private Context mContext;
    private WifiBroadCastOperator mWifiBroadCastOperator;

    /**
     * the specified SSID wifi state and wifi scan result broadcast listener
     **/
    private WifiStateReceiver mWifiStateReceiver;
    private WifiScanRsultReciver mWifiScanRsultReciver;
    private WifiConnectReceiver mWifiConnectReceiver;

    /**
     * wifi hotpot manager
     **/
    private WifiHotAdmin mWifiHotAdmin;

    private WifiManager wifiManager;
    private String mSSID;
    private boolean isConnecting = false;

    public enum OperationsType {
        CONNECT, SCAN;
    }

    public static WifiHotManager getInstance(Context context,
                                             WifiBroadCastOperator wifiOperations) {
        if (instance == null) {
            instance = new WifiHotManager(context, wifiOperations);
        }
        return instance;
    }

    private WifiHotManager(Context context, WifiBroadCastOperator wifiOperator) {
        this.mContext = context;
        this.mWifiBroadCastOperator = wifiOperator;
        this.mWifiHotAdmin = WifiHotAdmin.newInstance(context);
        this.wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * scan wifi hot
     **/
    public void scanWifiHot() {
        Log.i(TAG, "into wifiHotScan()");

        mWifiHotAdmin.closeWifiAp();
        if (!wifiIsOpen()) { // WIFI is close currently
            //listen to the specified SSID wifi state
            registerWifiStateBroadcast("");
            mWifiStateReceiver.setOperationsType(OperationsType.SCAN);

            //open wifi
            openWifi();
        } else { // WIFI is open currently

            scanNearWifiHots();
        }
        Log.i(TAG, "out wifiHotScan()");
    }

    /**
     * connect to hotpot
     *
     * @param ssid
     * @param wifiList
     * @param password
     */
    public void connectToHotpot(String ssid, List<ScanResult> wifiList,
                                String password) {
        if (ssid == null || password.equals("") || !ssid.startsWith(Global.HOTPOT_NAME_Head)) {
            Log.d(TAG, "WIFI ssid is null or ");
            mWifiBroadCastOperator.disPlayWifiConnResult(false, null, null);
            return;
        }
        if (ssid.equalsIgnoreCase(mSSID) && isConnecting) {
            Log.d(TAG, "same ssid is  connecting!");
            mWifiBroadCastOperator.disPlayWifiConnResult(false, null, null);
            return;
        }
        if (!checkCoonectHotIsEnable(ssid, wifiList)) {
            Log.d(TAG, "ssid is not in the wifiList!");
            mWifiBroadCastOperator.disPlayWifiConnResult(false, null, null);
            return;
        }
        if (!wifiIsOpen()) {
            //listen to ssid wifi
            registerWifiStateBroadcast(ssid);
            mWifiStateReceiver.setOperationsType(OperationsType.CONNECT);
            //open wifi
            openWifi();
        } else {
            // real connecting
            enableNetwork(ssid, password);
        }
    }

    /**
     * create a hot wifi
     *
     * @param wifiName
     */
    public boolean startApWifiHot(String wifiName) {
        Log.i(TAG, "into startAWifiHot(String wifiName) wifiName = " + wifiName);
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        if (mWifiHotAdmin != null) {
            return mWifiHotAdmin.startWifiAp(wifiName);
        }
        Log.i(TAG, "out startAWifiHot(String wifiName)");
        return false;
    }

    /*****************************************************************************/

    /**
     * connect wifi hot really by thread
     *
     * @param ssid
     * @param password
     */
    private void enableNetwork(final String ssid, final String password) {
        // delete more conneted wifi
        deleteMoreCon(ssid);

        registerWifiConnectBroadCast();

        new Thread(new Runnable() {
            @Override
            public void run() {
                WifiConfiguration config = WifiHotConfigAdmin.createWifiNoPassInfo(ssid, password);
                // if connect is successful
                isConnecting = connectHotSpot(config);
                mSSID = ssid;
                if (!isConnecting) {
                    Log.i(TAG, "into enableNetwork(WifiConfiguration wifiConfig) isConnecting =" + isConnecting);
                    return;
                }
            }
        }).start();
    }

    /**
     * connect wifi hot if successful
     *
     * @param wifiConfig
     * @return
     */
    private boolean connectHotSpot(WifiConfiguration wifiConfig) {
        Log.i(TAG, "into enableNetwork(WifiConfiguration wifiConfig)");

        //the ID of the newly created network description
        int wcgID = wifiManager.addNetwork(wifiConfig);
        Log.i(TAG, "into enableNetwork(WifiConfiguration wifiConfig) wcID = "
                + wcgID);
        if (wcgID < 0) {
            return false;
        }
        boolean flag = wifiManager.enableNetwork(wcgID, true);
//		if (!flag){
//			wifiManager.saveConfiguration();
//			wifiManager.reconnect();
//		}
        Log.i(TAG, "out enableNetwork(WifiConfiguration wifiConfig)");
        return flag;
    }

    /**
     * close repeat hotpot avoid not connect
     *
     * @param ssid
     */
    public void deleteMoreCon(String ssid) {
        String destStr = "\"" + ssid + "\"";
        // get current connected wifi
        List<WifiConfiguration> exitingConfigs = wifiManager
                .getConfiguredNetworks();
        if (exitingConfigs == null) {
            return;
        }
        for (WifiConfiguration wifiConfiguration : exitingConfigs) {
            if (wifiConfiguration.SSID.equalsIgnoreCase(destStr)) {
                // disable the specified ssid wifi on exitingConfigs
                wifiManager.disableNetwork(wifiConfiguration.networkId);
                wifiManager.removeNetwork(wifiConfiguration.networkId);
            }
        }
        wifiManager.saveConfiguration();
    }

    /**
     * check connect SSID if at wifi list that has scaned
     *
     * @param ssid
     * @param wifiList
     * @return
     */
    private boolean checkCoonectHotIsEnable(String ssid,
                                            List<ScanResult> wifiList) {
        for (ScanResult result : wifiList) {
            if (result.SSID.equals(ssid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * scan near wifi
     **/
    private void scanNearWifiHots() {
        registerWifiScanBroadcast();
        //start scan
        wifiManager.startScan();
    }

    private void openWifi() {
        Log.i(TAG, "into OpenWifi()");
        if (wifiManager == null) {
            wifiManager = (WifiManager) mContext
                    .getSystemService(Context.WIFI_SERVICE);
        }
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        Log.i(TAG, "out OpenWifi()");
    }

    /**
     * select wifi is open
     **/
    private boolean wifiIsOpen() {
        if (wifiManager == null) {
            wifiManager = (WifiManager) mContext
                    .getSystemService(Context.WIFI_SERVICE);
        }
        return wifiManager.isWifiEnabled();
    }


    /******************************************************************************/

    /**
     * regist wifi: state broadcast listener
     *
     * @param ssid the specified wifi
     */
    private void registerWifiStateBroadcast(String ssid) {
        if (mWifiStateReceiver == null) {
            mWifiStateReceiver = new WifiStateReceiver(mWifiBroadCastOperator, ssid);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // regist wifi broadcast listener
        mContext.registerReceiver(mWifiStateReceiver, intentFilter);
    }

    /**
     * regist wifi: scan result broadcast listener
     */
    private void registerWifiScanBroadcast() {
        if (mWifiScanRsultReciver == null) {
            mWifiScanRsultReciver = new WifiScanRsultReciver(mWifiBroadCastOperator);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        // regist wifi broadcast listener
        mContext.registerReceiver(mWifiScanRsultReciver, intentFilter);
    }

    /**
     * regist wifi: connected wifi broadcast listener
     **/
    private void registerWifiConnectBroadCast() {
        if (mWifiConnectReceiver == null) {
            mWifiConnectReceiver = new WifiConnectReceiver(mWifiBroadCastOperator);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mWifiConnectReceiver, filter);
    }

    /**
     * unregist wifi: state broadcast listener
     **/
    public void unRegisterWifiStateBroadCast() {
        if (mWifiStateReceiver != null) {
            mContext.unregisterReceiver(mWifiStateReceiver);
            mWifiStateReceiver = null;
        }
    }

    /**
     * unregist wifi: scan result broadcast listener
     **/
    public void unRegisterWifiScanBroadCast() {
        if (mWifiScanRsultReciver != null) {
            mContext.unregisterReceiver(mWifiScanRsultReciver);
            mWifiScanRsultReciver = null;
        }
    }

    /**
     * unregist wifi: connected wifi broadcast listener
     **/
    public void unRegisterWifiConnectBroadCast() {
        if (mWifiConnectReceiver != null) {
            mContext.unregisterReceiver(mWifiConnectReceiver);
            mWifiConnectReceiver = null;
        }
    }

    /******************************************************************************/

    /**
     * a wifi broadcast operate interface
     */
    public static interface WifiBroadCastOperator {
        /**
         * display wifi scan result
         *
         * @param wifiList wifihot scan result
         */
        public void disPlayWifiScanResult(List<ScanResult> wifiList);

        /**
         * display wifi connect result
         *
         * @param result   wifi connection result
         * @param wifiInfo wifi connection info
         * @return
         */
        public boolean disPlayWifiConnResult(boolean result, WifiInfo wifiInfo, String  connStatue);

        /**
         * connect the specified ssid by type conntect wifi or scan wifi
         *
         * @param operationsType type conntect wifi or scan wifi
         * @param ssid           connecting wifi that specified ssid
         */
        public void operationByType(OperationsType operationsType, String ssid);
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    public void setConnecting(boolean isConnecting) {
        this.isConnecting = isConnecting;
    }

    /**
     * close wifi hot
     **/
    public void disableWifiHot() {
        mWifiHotAdmin.closeWifiAp();
    }
}
