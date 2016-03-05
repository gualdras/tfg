package com.university.gualdras.tfgapp.presentation.contactsTab;

/**
 * Created by gualdras on 19/09/15.
 */

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.provider.ContactsContract;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.ServerSharedConstants;
import com.university.gualdras.tfgapp.domain.ContactItem;
import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.gcm.ServerComunication;
import com.university.gualdras.tfgapp.persistence.DataProvider;
import com.university.gualdras.tfgapp.presentation.chat.ChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class ContactTab extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    String TAG = "contactTab";
    private Context mContext;
    //private ContactListAdapter contactListAdapter;

    private SimpleCursorAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.contact_item,
                null,
                new String[]{DataProvider.COL_NAME, DataProvider.COL_PHOTO},
                new int[]{R.id.contact_name, R.id.photo_chat_profile},
                0);

        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = container.getContext();
        setHasOptionsMenu(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /*@Override
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
    }*/

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ContactItem contactItem = (ContactItem) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.EXTRA_CONTACT_NAME, contactItem.getContactName());
        intent.putExtra(Constants.EXTRA_PHOTO_PROFILE, contactItem.getPhotoProfile());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contacts_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {

            case R.id.refresh_menu_item:
                new RefreshContactsTask().execute();
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(getActivity(),
                DataProvider.CONTENT_URI_PROFILE,
                new String[]{DataProvider.COL_ID, DataProvider.COL_NAME, DataProvider.COL_PHOTO},
                null,
                null,
                DataProvider.COL_ID + " DESC");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private class RefreshContactsTask extends AsyncTask<Void, Void, String> {

        Boolean error = true;

        @Override
        protected String doInBackground(Void... params) {
            String contactId, completeNumber;
            String data = "";

            ArrayList<String> contactsAlreadyChecked = new ArrayList<>();

            JSONObject jsonParams = new JSONObject();
            JSONArray contactsJson = new JSONArray();
            HttpURLConnection httpURLConnection = null;

            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber numberProto = null;
            Cursor phones;

            ContentResolver cr = mContext.getContentResolver();
            Cursor contacts = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if (contacts.getCount() > 0) {
                while (contacts.moveToNext()) {
                    contactId = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID));
                    phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                    while (phones.moveToNext()) {
                        String unformattedNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        try {
                            numberProto = phoneUtil.parse(unformattedNumber, "ES");
                        } catch (NumberParseException e) {
                            e.printStackTrace();
                        }

                        if(phoneUtil.isValidNumber(numberProto)) {
                            int countryCode = numberProto.getCountryCode();
                            long nationalNumber = numberProto.getNationalNumber();

                            completeNumber = Long.toString(countryCode)  + Long.toString(nationalNumber);


                            if (!contactsAlreadyChecked.contains(completeNumber)) {
                                contactsAlreadyChecked.add(completeNumber);
                                JSONObject c = new JSONObject();
                                try {
                                    c.put(ServerSharedConstants.ID, contactId);
                                    c.put(ServerSharedConstants.PHONE_NUMBER, completeNumber);
                                    contactsJson.put(c);
                                } catch (JSONException e) {
                                    Log.d(TAG, e.toString());
                                }
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
                httpURLConnection = ServerComunication.post(Constants.USERS_URL, jsonParams);
                int code = httpURLConnection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                    data = readStream(in);
                    error = false;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != httpURLConnection)
                    httpURLConnection.disconnect();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            if (!error) {
                JSONProcess(result);
            }
        }

        private void updateContacs(ArrayList<ContactItem> newContacts) {

            for(ContactItem contact: newContacts) {
                ContentValues values = new ContentValues(2);
                values.put(DataProvider.COL_NAME, contact.getContactName());
                values.put(DataProvider.COL_PHONE_NUMBER, contact.getPhoneNumber());
                values.put(DataProvider.COL_PHOTO, contact.getPhotoProfile());
                mContext.getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);
            }
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer data = new StringBuffer("");
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    data.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException");
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return data.toString();
        }

        //Todo: remove id
        private ArrayList<ContactItem> JSONProcess(String response) {
            String contactName = "", phoneNumber = "", photo = "";
            ArrayList<ContactItem> result = new ArrayList<>();

            ContentResolver cr = mContext.getContentResolver();

            try {
                JSONObject responseObject = (JSONObject) new JSONTokener(
                        response).nextValue();
                JSONArray contacts = responseObject
                        .getJSONArray(ServerSharedConstants.CONTACTS);

                for (int i = 0; i < contacts.length(); i++) {

                    JSONObject c = contacts.getJSONObject(i);
                    String id = c.getString(ServerSharedConstants.ID);


                    phoneNumber = c.getString(ServerSharedConstants.PHONE_NUMBER);

                    /*String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
                    String[] whereNameParams = new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, id};
                    Cursor nameCur = cr.query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
                    while (nameCur.moveToNext()) {
                        String given = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                        String family = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                        String display = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                        String kk = given + family;
                    }
                    nameCur.close();*/

                    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
                    Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI}, null, null, null);


                    if(cursor.moveToFirst()) {
                        contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                        photo = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));
                    }

                    result.add(new ContactItem(photo, contactName, phoneNumber));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}