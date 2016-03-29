package com.university.gualdras.tfgapp.domain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.presentation.chat.Kk;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gualdras on 29/03/16.
 */
public class ImageDownload extends AsyncTask<Void, Void, Bitmap> {
    private static final String TAG = "ImageDownload";
    String blobkey;
    Context context;

    public ImageDownload(Context context, String blobkey){
        this.blobkey = blobkey;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap avatar = null;
        HttpURLConnection httpUrlConnection = null;

        try {
            httpUrlConnection = (HttpURLConnection) new URL(Constants.DOWNLOAD_IMG_URL + blobkey)
                    .openConnection();

            switch (httpUrlConnection.getResponseCode()){
                case HttpURLConnection.HTTP_OK:
                    InputStream in = new BufferedInputStream(
                            httpUrlConnection.getInputStream());

                    avatar = BitmapFactory.decodeStream(in);
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

        return avatar;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Intent intent = new Intent(context, Kk.class);
        intent.putExtra("img", bitmap);
        context.startActivity(intent);
    }
}
