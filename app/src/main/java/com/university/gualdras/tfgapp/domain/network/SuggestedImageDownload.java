package com.university.gualdras.tfgapp.domain.network;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gualdras on 10/05/16.
 */
public class SuggestedImageDownload extends AsyncTask<Void, Void, Bitmap> {

    String link;
    ImageInteractionListener mListener;

    public SuggestedImageDownload(Activity activity, String link){
        this.mListener = (ImageInteractionListener) activity;
        this.link = link;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = null;
        HttpURLConnection httpUrlConnection;
        try {
            httpUrlConnection = (HttpURLConnection) new URL(link)
                    .openConnection();
            InputStream in = new BufferedInputStream(
                    httpUrlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mListener.onSuggestedImageDownloadFinish(bitmap);
    }
}
