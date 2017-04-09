package com.niqiu.Adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.niqiu.R;
import com.niqiu.contant.Global;
import com.niqiu.util.WifiUtils;

import java.util.List;

/**
 * Created by Aries on 2015/10/22.
 */
public class MyTranWIFIAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<ScanResult> list;
    Context context;

    public MyTranWIFIAdapter(Context context, List<ScanResult> list) {
        // TODO Auto-generated constructor stub
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    public void setList(List<ScanResult> list) {
        this.list = list;
    }


    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
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
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_tran_wifi_list, null);
            viewHolder.rssi = ((ImageView) convertView.findViewById(R.id.rssi));
            viewHolder.ssid = ((TextView) convertView.findViewById(R.id.ssid));
            viewHolder.desc = ((TextView) convertView.findViewById(R.id.desc));
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ScanResult scanResult = list.get(position);
        viewHolder.ssid.setText(scanResult.SSID);
        if (WifiUtils.isWifiConnect() && scanResult.BSSID.equals(WifiUtils.getBSSID())){
            viewHolder.desc.setText("已连接");
            viewHolder.rssi.setImageResource(R.drawable.ic_connected);
        }else{
            viewHolder.desc.setText(getDesc(scanResult));
            //判断信号强度，显示对应的指示图标
            if (Math.abs(scanResult.level) > 100) {
                viewHolder.rssi.setImageDrawable(context.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_0));
            } else if (Math.abs(scanResult.level) > 80) {
                viewHolder.rssi.setImageDrawable(context.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_1));
            } else if (Math.abs(scanResult.level) > 70) {
                viewHolder.rssi.setImageDrawable(context.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_1));
            } else if (Math.abs(scanResult.level) > 60) {
                viewHolder.rssi.setImageDrawable(context.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_2));
            } else if (Math.abs(scanResult.level) > 50) {
                viewHolder.rssi.setImageDrawable(context.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_3));
            } else {
                viewHolder.rssi.setImageDrawable(context.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_4));
            }
        }
        return convertView;
    }

    private String getDesc(ScanResult ap) {
        String desc = "";
        if (ap.SSID.startsWith(Global.HOTPOT_NAME_Head)) {
            desc = "传送热点，可以直接连接";
        }
        else {
            String descOri = ap.capabilities;
            if (descOri.toUpperCase().contains("WPA-PSK")
                    || descOri.toUpperCase().contains("WPA2-PSK")) {
                desc = "受到密码保护";
            }
            else {
                desc = "未受保护的网络";
            }
        }

//        // 是否连接此热点
//        if (TextUtils.equals(mSSID, WifiUtils.getSSID())) {
//            desc = "已连接";
//        }
        return desc;
    }

    public static class ViewHolder {
        public ImageView rssi;
        public TextView ssid;
        public TextView desc;
    }

}
