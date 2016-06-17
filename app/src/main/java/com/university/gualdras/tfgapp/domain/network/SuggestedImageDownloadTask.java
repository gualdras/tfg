package com.university.gualdras.tfgapp.domain.network;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.university.gualdras.tfgapp.domain.SuggestedImage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gualdras on 10/05/16.
 */
public class SuggestedImageDownloadTask extends AsyncTask<Void, Void, SuggestedImage> {

    SuggestedImage suggestedImage;
    ImageInteractionListener mListener;

    public SuggestedImageDownloadTask(Activity activity, SuggestedImage suggestedImage){
        this.mListener = (ImageInteractionListener) activity;
        this.suggestedImage = suggestedImage;
    }

    @Override
    protected SuggestedImage doInBackground(Void... params) {
        Bitmap bitmap = null;
        HttpURLConnection httpUrlConnection;
        try {
            httpUrlConnection = (HttpURLConnection) new URL(suggestedImage.getLink())
                    .openConnection();
            InputStream in = new BufferedInputStream(
                    httpUrlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(in);
            suggestedImage.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return suggestedImage;
    }

    @Override
    protected void onPostExecute(SuggestedImage suggestedImage) {
        mListener.onSuggestedImageDownloadFinish(suggestedImage);
    }
}
