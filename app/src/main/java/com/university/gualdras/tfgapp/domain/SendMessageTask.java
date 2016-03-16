package com.university.gualdras.tfgapp.domain;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.ServerSharedConstants;
import com.university.gualdras.tfgapp.StartActivity;
import com.university.gualdras.tfgapp.gcm.ServerComunication;
import com.university.gualdras.tfgapp.persistence.DataProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by gualdras on 7/03/16.
 */
public class SendMessageTask extends AsyncTask<Void, Void, Void> {

    String to, msg;
    Context mContext;

    public SendMessageTask(Context mContext, String to, String msg){
        this.mContext = mContext;
        this.to = to;
        this.msg = msg;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection httpURLConnection = null;
        JSONObject jsonParam = new JSONObject();
        String url = Constants.USERS_URL + "/" + to + Constants.SEND;

        try {
            jsonParam.put(ServerSharedConstants.FROM, StartActivity.getPhoneNumber());
            jsonParam.put(ServerSharedConstants.MSG, msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            httpURLConnection = ServerComunication.post(url, jsonParam, Constants.MAX_ATTEMPTS);
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED) {
                //Todo: do something
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != httpURLConnection)
                httpURLConnection.disconnect();
        }

        saveMessage(msg, to);
        return null;
    }
    private void saveMessage(String msg, String to){
        ContentValues values = new ContentValues(2);
        values.put(DataProvider.COL_MSG, msg);
        values.put(DataProvider.COL_TO, to);
        mContext.getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);
    }
}
