package com.university.gualdras.tfgapp.domain.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.ServerSharedConstants;
import com.university.gualdras.tfgapp.domain.MessageItem;
import com.university.gualdras.tfgapp.domain.SuggestedImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gualdras on 30/05/16.
 */
public class UpdateImageTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "ImageUpdate";

    Context mContext;
    SuggestedImage suggestedImage;
    MessageItem messageItem;

    public UpdateImageTask(Context mContext, SuggestedImage suggestedImage, MessageItem messageItem) {
        this.mContext = mContext;
        this.suggestedImage = suggestedImage;
        this.messageItem = messageItem;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection httpUrlConnection = null;

        try {
            httpUrlConnection = (HttpURLConnection) new URL(Constants.IMAGES_URL + "/" + suggestedImage.getBlobUrl())
                    .openConnection();

            httpUrlConnection.setRequestMethod("PUT");
            httpUrlConnection.setRequestProperty("Content-Type", "application/json");


            DataOutputStream wr = new DataOutputStream(httpUrlConnection.getOutputStream ());

            JSONObject jsonParam = new JSONObject();
            jsonParam.put(ServerSharedConstants.KEY_WORDS, new JSONArray(suggestedImage.getKeyWords()));
            jsonParam.put(ServerSharedConstants.PHONE_NUMBER, messageItem.getFrom());


            wr.writeBytes(jsonParam.toString());

            wr.flush();
            wr.close();

            switch (httpUrlConnection.getResponseCode()){
                case HttpURLConnection.HTTP_OK:
                    InputStream in = new BufferedInputStream(
                            httpUrlConnection.getInputStream());

                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    InputStream err = new BufferedInputStream(httpUrlConnection.getErrorStream());
                    break;
            }

        } catch (MalformedURLException exception) {
            Log.e(TAG, "MalformedURLException");
        } catch (IOException exception) {
            Log.e(TAG, "IOException");
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (null != httpUrlConnection)
                httpUrlConnection.disconnect();
        }

        return null;
    }



    @Override
    protected void onPostExecute(Void aVoid) {
        messageItem.setMsg(suggestedImage.getBlobUrl());
        new SendMessageTask(mContext, messageItem).execute();
    }
}
