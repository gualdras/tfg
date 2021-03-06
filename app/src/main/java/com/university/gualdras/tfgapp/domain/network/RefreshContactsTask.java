package com.university.gualdras.tfgapp.domain.network;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.ServerSharedConstants;
import com.university.gualdras.tfgapp.domain.ContactItem;
import com.university.gualdras.tfgapp.persistence.DataProvider;
import com.university.gualdras.tfgapp.presentation.contactsTab.ContactTab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class RefreshContactsTask extends AsyncTask<Context, Void, String> {

    String TAG = "refreshContactTab";
    Boolean error = true;
    Context mContext;

    @Override
    protected String doInBackground(Context... params) {
        mContext = params[0];
        String data = "";

        HttpURLConnection httpUrlConnection = null;

        JSONObject jsonParams = processPhoneNumbers();
        try {
            httpUrlConnection = (HttpURLConnection) new URL(Constants.USERS_URL)
                    .openConnection();

            httpUrlConnection.setRequestMethod("PUT");
            httpUrlConnection.setRequestProperty("Content-Type", "application/json");

            DataOutputStream wr = new DataOutputStream(httpUrlConnection.getOutputStream());
            wr.writeBytes(jsonParams.toString());
            wr.flush();
            wr.close();

            int responseCode = httpUrlConnection.getResponseCode();

            switch (responseCode){
                case HttpURLConnection.HTTP_OK:
                    InputStream in = new BufferedInputStream(httpUrlConnection.getInputStream());
                    data = NetworkUtils.readStream(in);
                    error = false;
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    InputStream err = new BufferedInputStream(httpUrlConnection.getErrorStream());
                    break;
            }
        } catch (MalformedURLException exception) {
            Log.e(TAG, "MalformedURLException");
        } catch (IOException exception) {
            Log.e(TAG, "IOException");
        } finally {
            if (null != httpUrlConnection)
                httpUrlConnection.disconnect();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String response) {
        if (!error) {
            updateContacts(response);
        }
        ContactTab.OnRefreshContactsFinish();
    }

    private void updateContacts(String response) {
        ArrayList<ContactItem> newContacts = JSONProcess(response);
        for (ContactItem contact : newContacts) {
            contact.saveContact(mContext);
        }
    }

    private JSONObject processPhoneNumbers(){
        String contactId, completeNumber;

        ArrayList<String> contactsAlreadyChecked = new ArrayList<>();

        JSONObject jsonParams = new JSONObject();
        JSONArray contactsJson = new JSONArray();

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber numberProto = null;
        Cursor phones = null;

        ContentResolver cr = mContext.getContentResolver();
        Cursor users = cr.query(DataProvider.CONTENT_URI_PROFILE, new String[]{DataProvider.COL_PHONE_NUMBER}, null, null, null);
        Cursor contacts = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        //Add contacts to contactsAlreadyChecked that are already users
        if(users!= null){
            while (users.moveToNext()){
                contactsAlreadyChecked.add(users.getString(users.getColumnIndex(DataProvider.COL_PHONE_NUMBER)));
            }
        }
        if (contacts != null && contacts.getCount() > 0) {
            while (contacts.moveToNext()) {
                contactId = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID));
                phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                while (phones != null && phones.moveToNext()) {
                    String unformattedNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                    String countryID = manager.getSimCountryIso().toUpperCase().trim();
                    try {
                        numberProto = phoneUtil.parse(unformattedNumber, countryID);
                    } catch (NumberParseException e) {
                        e.printStackTrace();
                    }

                    if (phoneUtil.isValidNumber(numberProto)) {
                        completeNumber = Long.toString(numberProto.getCountryCode()) + Long.toString(numberProto.getNationalNumber());

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

        if (phones != null && !phones.isClosed()) {
            phones.close();
        }

        if (contacts != null && !contacts.isClosed()) {
            contacts.close();
        }
        try {
            jsonParams.put(ServerSharedConstants.CONTACTS, contactsJson);
        } catch (JSONException e) {
            Log.d(TAG, e.toString());
        }
        return jsonParams;
    }

    private ArrayList<ContactItem> JSONProcess(String response) {
        String contactName = "", phoneNumber, photo = "";

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
                    if((cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI))) != null){
                        photo = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI));
                    } else {
                        photo = Constants.DEFAULT_CONTACT_PHOTO;
                    }
                }
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                result.add(new ContactItem(photo, contactName, phoneNumber));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
