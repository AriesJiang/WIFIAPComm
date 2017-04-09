package com.niqiu.Adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.niqiu.R;
import com.niqiu.contant.Global;
import com.niqiu.data.TransmissionBean;
import com.niqiu.data.User;
import com.niqiu.util.WifiUtils;

import java.util.List;

/**
 * Created by Aries on 2015/10/22.
 */
public class MyTranWIFIUserAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<TransmissionBean> list;
    Context context;

    public MyTranWIFIUserAdapter(Context context, List<TransmissionBean> list) {
        // TODO Auto-generated constructor stub
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    public void setList(List<TransmissionBean> list) {
        this.list = list;
    }

    public List<TransmissionBean> getList() {
        return list;
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
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_tran_wifi_file_list, null);
            viewHolder.avater = ((ImageView) convertView.findViewById(R.id.avater));
            viewHolder.progressBar = ((ProgressBar) convertView.findViewById(R.id.progressBar));
            viewHolder.name = ((TextView) convertView.findViewById(R.id.name));
            viewHolder.ip = ((TextView) convertView.findViewById(R.id.ip));
            viewHolder.file = ((TextView) convertView.findViewById(R.id.file));
            viewHolder.percent = ((TextView) convertView.findViewById(R.id.percent));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TransmissionBean item = (TransmissionBean) getItem(position);
        item.setFileText(viewHolder.file);
        item.setPercentText(viewHolder.percent);
        item.setProgressBar(viewHolder.progressBar);
        User itemUser = item.getUser();
        viewHolder.avater.setImageResource(R.mipmap.ic_launcher);
        viewHolder.name.setText(itemUser.getUserName());
        viewHolder.ip.setText(itemUser.getIp());
        if (item.getFileSize() != 0){
            viewHolder.file.setText(item.getFileCount()/item.getFileSize());
            viewHolder.percent.setText(item.getPercent()+"%");
            viewHolder.progressBar.setProgress(item.getPercent());
        }

        return convertView;
    }

    private String getDesc(ScanResult ap) {
        String desc = "";
        if (ap.SSID.startsWith(Global.HOTPOT_NAME_Head)) {
            desc = "传送热点，可以直接连接";
        } else {
            String descOri = ap.capabilities;
            if (descOri.toUpperCase().contains("WPA-PSK")
                    || descOri.toUpperCase().contains("WPA2-PSK")) {
                desc = "受到密码保护";
            } else {
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
        public ImageView avater;
        public TextView name;
        public TextView ip, file, percent;
        public ProgressBar progressBar;
    }

}
