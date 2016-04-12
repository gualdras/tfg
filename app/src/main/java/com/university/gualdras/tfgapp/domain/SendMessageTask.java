package com.university.gualdras.tfgapp.domain;

import android.content.Context;
import android.os.AsyncTask;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.gcm.ServerComunication;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by gualdras on 7/03/16.
 */
public class SendMessageTask extends AsyncTask<Void, Void, Void> {

    MessageItem messageItem;
    Context mContext;

    public SendMessageTask(Context mContext, MessageItem messageItem){
        this.mContext = mContext;
        this.messageItem = messageItem;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection httpURLConnection = null;
        JSONObject jsonParam = messageItem.messageToJSON();
        String url = Constants.USERS_URL + "/" + messageItem.getTo() + Constants.SEND;

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

        messageItem.saveMessageSended(mContext);
        return null;
    }
}
