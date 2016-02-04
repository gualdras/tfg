package com.university.gualdras.tfgapp.presentation.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gualdras on 23/09/15.
 */
public class MessageListAdapter extends BaseAdapter {

    private ArrayList<TextView> mList = new ArrayList<TextView>();
    private static LayoutInflater inflater = null;
    private Context mContext;

    public MessageListAdapter(Context mContext){
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public void add(TextView msg_chat){
        mList.add(msg_chat);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }


    //TODO - review this method
    @Override
    public long getItemId(int position) {
        return position;
    }

    //TODO - Implement getView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
