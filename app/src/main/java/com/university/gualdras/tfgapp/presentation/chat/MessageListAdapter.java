package com.university.gualdras.tfgapp.presentation.chat;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.domain.MessageItem;

import java.util.ArrayList;

/**
 * Created by gualdras on 23/09/15.
 */


//Todo: Remove this class
public class MessageListAdapter extends BaseAdapter {

    private ArrayList<MessageItem> mList = new ArrayList<MessageItem>();
    private static LayoutInflater mInflater = null;
    private Context mContext;

    public MessageListAdapter(Context mContext){
        this.mContext = mContext;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(MessageItem msg_chat){
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

        View newView = convertView;
        final ViewHolder holder;

        final MessageItem messageItem = (MessageItem) getItem(position);

        if (null == convertView) {
            holder = new ViewHolder();
            newView = mInflater.inflate(R.layout.chat_message_item, parent, false);
            holder.msg = (TextView) newView.findViewById(R.id.msg);
            holder.time = (TextView) newView.findViewById(R.id.time);
            newView.setTag(holder);
        } else {
            holder = (ViewHolder) newView.getTag();
        }

        holder.msg.setText(messageItem.getMsg());
        holder.time.setText(messageItem.getDateTime());

        return newView;
    }

    static class ViewHolder {
        TextView msg;
        TextView time;
    }
}
