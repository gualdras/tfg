package com.university.gualdras.tfgapp.domain.network;

import android.content.Context;
import android.os.AsyncTask;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.domain.MessageItem;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
        HttpURLConnection httpUrlConnection = null;
        JSONObject jsonParams = messageItem.messageToJSON();
        String url = Constants.USERS_URL + "/" + messageItem.getTo();

        try {
            httpUrlConnection = (HttpURLConnection) new URL(url)
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
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    InputStream err = new BufferedInputStream(httpUrlConnection.getErrorStream());
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != httpUrlConnection)
                httpUrlConnection.disconnect();
        }

        messageItem.saveMessageSent(mContext);
        return null;
    }
}
