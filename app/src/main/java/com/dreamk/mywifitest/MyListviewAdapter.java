package com.dreamk.mywifitest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class MyListviewAdapter extends BaseAdapter {
    private final LinkedList<ListViewData> mData;
    private final Context mContext;

    /**
     *
     * @param mData 要显示的数据 链表<ListViewData>
     * @param mContext context
     */
    public MyListviewAdapter(LinkedList<ListViewData> mData, Context mContext){
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder myViewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
            myViewHolder = new MyViewHolder();
            myViewHolder.tv_ssid = convertView.findViewById(R.id.tv_ssid);
            myViewHolder.tv_rssi = convertView.findViewById(R.id.tv_rssi);
            myViewHolder.tv_mac = convertView.findViewById(R.id.tv_mac);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
            myViewHolder.tv_ssid.setText(mData.get(position).getSSID());
            myViewHolder.tv_rssi.setText(String.valueOf(mData.get(position).getRSSI()));
            myViewHolder.tv_mac.setText(mData.get(position).getMac());
        return convertView;
    }

    static class MyViewHolder{
        TextView tv_ssid,tv_rssi,tv_mac;
    }
}
