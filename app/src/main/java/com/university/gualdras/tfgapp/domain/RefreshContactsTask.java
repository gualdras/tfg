package com.university.gualdras.tfgapp.domain;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.ServerSharedConstants;
import com.university.gualdras.tfgapp.gcm.ServerComunication;
import com.university.gualdras.tfgapp.persistence.DataProvider;

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

/**
 * Created by gualdras on 6/03/16.
 */

public class RefreshContactsTask extends AsyncTask<Context, Void, String> {

    String TAG = "refreshContactTab";
    Boolean error = true;
    Context mContext;

    @Override
    protected String doInBackground(Context... params) {
        mContext = params[0];
        String contactId, completeNumber;
        String data = "";

        ArrayList<String> contactsAlreadyChecked = new ArrayList<>();

        JSONObject jsonParams = new JSONObject();
        JSONArray contactsJson = new JSONArray();
        HttpURLConnection httpURLConnection = null;

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber numberProto = null;
        Cursor phones = null;

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

                    if (phoneUtil.isValidNumber(numberProto)) {
                        int countryCode = numberProto.getCountryCode();
                        long nationalNumber = numberProto.getNationalNumber();

                        completeNumber = Long.toString(countryCode) + Long.toString(nationalNumber);


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

        //Todo: change this
        if (phones != null && !phones.isClosed()) {
            phones.close();
        }

        if (!contacts.isClosed()) {
            contacts.close();
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
    protected void onPostExecute(String response) {
        if (!error) {
            updateContacts(response);
        }
    }

    private void updateContacts(String response) {
        ArrayList<ContactItem> newContacts = JSONProcess(response);

        for (ContactItem contact : newContacts) {
            ContentValues values = new ContentValues(3);
            values.put(DataProvider.COL_NAME, contact.getContactName());
            values.put(DataProvider.COL_PHONE_NUMBER, contact.getPhoneNumber());
            values.put(DataProvider.COL_PHOTO, contact.getPhotoProfile());
            mContext.getContentResolver().insert(DataProvider.CONTENT_URI_PROFILE, values);
        }
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

                phoneNumber = c.getString(ServerSharedConstants.PHONE_NUMBER);

                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
                Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI}, null, null, null);

                if (cursor.moveToFirst()) {
                    contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    photo = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                result.add(new ContactItem(photo, contactName, phoneNumber));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
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
}
