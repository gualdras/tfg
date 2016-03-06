package com.university.gualdras.tfgapp.presentation.contactsTab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.university.gualdras.tfgapp.domain.ContactItem;
import com.university.gualdras.tfgapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by gualdras on 20/09/15.
 */
public class ContactListAdapter extends BaseAdapter{

    private ArrayList<ContactItem> mList = new ArrayList<ContactItem>();
    private static LayoutInflater inflater = null;
    private Context mContext;

    public ContactListAdapter(Context mContext){
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View newView = convertView;
        ViewHolder holder;

        ContactItem curr = mList.get(position);

        if (null == convertView) {
            holder = new ViewHolder();
            newView = inflater
                    .inflate(R.layout.contact_item, parent, false);
            holder.photoProfile = (CircleImageView) newView.findViewById(R.id.contact_profile_photo);
            holder.contactName = (TextView) newView.findViewById(R.id.contact_name);
            newView.setTag(holder);

        } else {
            holder = (ViewHolder) newView.getTag();
        }

        //holder.photoProfile.setImageBitmap(curr.getPhotoProfile());
        holder.contactName.setText(curr.getContactName());
        return newView;
    }

    static class ViewHolder {

        CircleImageView photoProfile;
        TextView contactName;

    }


    public void add(ContactItem listItem) {
        mList.add(listItem);
        notifyDataSetChanged();
    }

    public ArrayList<ContactItem> getList() {
        return mList;
    }

    public void removeAllViews() {
        mList.clear();
        this.notifyDataSetChanged();
    }
}

