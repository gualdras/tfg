package com.university.gualdras.tfgapp.presentation.contactsTab;

/**
 * Created by gualdras on 19/09/15.
 */
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.provider.ContactsContract;


import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.ServerSharedConstants;
import com.university.gualdras.tfgapp.domain.ContactItem;
import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.gcm.ServerComunication;
import com.university.gualdras.tfgapp.presentation.chat.ChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ContactTab extends ListFragment {

    private Context mContext;
    private ContactListAdapter contactListAdapter;
    String TAG = "contactTab";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = container.getContext();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
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


    private void refreshContacs() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String contactId;
                JSONObject jsonParams = new JSONObject();
                JSONArray contactsJson = new JSONArray();
                HttpURLConnection httpURLConnection;

                ContentResolver cr = mContext.getContentResolver();
                Cursor contacts = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                if (contacts.getCount() > 0) {
                    while (contacts.moveToNext()) {
                        contactId = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID));
                        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (phones.moveToNext()) {
                            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                JSONObject c = new JSONObject();
                                try {
                                    c.put("id", contactId);
                                    c.put("phoneNumber", number);
                                    contactsJson.put(c);
                                } catch (JSONException e) {
                                    Log.d(TAG, e.toString());
                                }
                            }
                        }
                    }
                }

                try {
                    jsonParams.put(ServerSharedConstants.CONTACTS, contactsJson);
                } catch (JSONException e) {
                    Log.d(TAG, e.toString());
                }

                try {
                    httpURLConnection = ServerComunication.post(Constants.SERVER_URL, jsonParams);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);
    }
}