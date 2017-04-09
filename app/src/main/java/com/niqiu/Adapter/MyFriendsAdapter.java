package com.niqiu.Adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.niqiu.R;

import java.util.List;

/**
 * Created by Aries on 2015/10/22.
 */
public class MyFriendsAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<ScanResult> list;
    Context context;

    public MyFriendsAdapter(Context context, List<ScanResult> list) {
        // TODO Auto-generated constructor stub
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    public void setList(List<ScanResult> list){
        this.list = list;
    }


    @Override
    public int getCount() {
        if (list == null){
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
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_0));
        } else if (Math.abs(scanResult.level) > 80) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_1));
        } else if (Math.abs(scanResult.level) > 70) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_1));
        } else if (Math.abs(scanResult.level) > 60) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_2));
        } else if (Math.abs(scanResult.level) > 50) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_3));
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.stat_sys_wifi_signal_4));
        }
        return view;
    }

}
