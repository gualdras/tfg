package com.university.gualdras.tfgapp.domain.network;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.domain.SuggestedImage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SuggestedImageDownloadTask extends AsyncTask<Void, Void, SuggestedImage> {
    private static final String TAG = "ImageDownload";

    SuggestedImage suggestedImage;
    ImageInteractionListener mListener;

    public SuggestedImageDownloadTask(Activity activity, SuggestedImage suggestedImage){
        this.mListener = (ImageInteractionListener) activity;
        this.suggestedImage = suggestedImage;
    }

    @Override
    protected SuggestedImage doInBackground(Void... params) {
        Bitmap bitmap;
        HttpURLConnection httpUrlConnection = null;
        if (suggestedImage.getBlobUrl() != null) {
            try {
                httpUrlConnection = (HttpURLConnection) new URL(Constants.IMAGES_URL + "/" + suggestedImage.getBlobUrl())
                        .openConnection();

                switch (httpUrlConnection.getResponseCode()) {
                    case HttpURLConnection.HTTP_OK:
                        InputStream in = new BufferedInputStream(
                                httpUrlConnection.getInputStream());

                        bitmap = BitmapFactory.decodeStream(in);
                        suggestedImage.setBitmap(bitmap);
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
        } else {
            try {
                httpUrlConnection = (HttpURLConnection) new URL(suggestedImage.getLink())
                        .openConnection();
                InputStream in = new BufferedInputStream(
                        httpUrlConnection.getInputStream());
                bitmap = BitmapFactory.decodeStream(in);
                suggestedImage.setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != httpUrlConnection)
                    httpUrlConnection.disconnect();
            }
        }
        return suggestedImage;
    }

    @Override
    protected void onPostExecute(SuggestedImage suggestedImage) {
        mListener.onSuggestedImageDownloadFinish(suggestedImage);
    }
}
