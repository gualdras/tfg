package com.university.gualdras.tfgapp.presentation.contactsTab;

/**
 * Created by gualdras on 19/09/15.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.domain.ContactItem;
import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.presentation.chat.ChatActivity;

public class ContactTab extends ListFragment {

    private Context mContext;
    private ContactListAdapter contactListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = container.getContext();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedState){
        super.onActivityCreated(savedState);

        contactListAdapter = new ContactListAdapter(mContext);
        setListAdapter(contactListAdapter);

        Bitmap bMap1 = BitmapFactory.decodeResource(getResources(), R.drawable.victor);
        ContactItem victor = new ContactItem(bMap1, "Victor");
        contactListAdapter.add(victor);

        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.jserrano);
        ContactItem jesus = new ContactItem(bMap, "Jesus");
        contactListAdapter.add(jesus);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ContactItem contactItem = (ContactItem) contactListAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.EXTRA_CONTACT_NAME, contactItem.getContactName());
        intent.putExtra(Constants.EXTRA_PHOTO_PROFILE, contactItem.getPhotoProfile());
        startActivity(intent);
    }
}