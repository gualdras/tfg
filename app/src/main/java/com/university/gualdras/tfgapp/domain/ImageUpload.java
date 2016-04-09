package com.university.gualdras.tfgapp.domain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.presentation.chat.ChatActivity;
import com.university.gualdras.tfgapp.presentation.chat.Kk;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by gualdras on 9/03/16.
 */
public class ImageUpload extends AsyncTask<Void, Void, Bitmap> {

    private static final String ITEMS_TAG = "items";
    private static final String TAG = "ImageUpload";

    String path;
    Activity context;

    public ImageUpload(Activity context, String path){
        this.path = path;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        String data = "";
        HttpURLConnection httpUrlConnection = null;

        try {
            httpUrlConnection = (HttpURLConnection) new URL(Constants.UPLOAD_FORM_URL)
                    .openConnection();

            switch (httpUrlConnection.getResponseCode()){
                case HttpURLConnection.HTTP_OK:
                    InputStream in = new BufferedInputStream(
                            httpUrlConnection.getInputStream());

                    data = readStream(in);
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    InputStream err = new BufferedInputStream(httpUrlConnection.getErrorStream());

                    data = readStream(err);
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
        String imgUploadURL = data;
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "victor.png", RequestBody.create(MediaType.parse("image/png"), new File(path)))
                .addFormDataPart("title", "My photo")
                .build();

        Request request = new Request.Builder()
                .url(imgUploadURL)
                .post(requestBody)
                .build();
        String kk="";
        try {
            Response response = client.newCall(request).execute();
            kk = response.body().string();
            Log.d("response", "uploadImage:"+kk);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Todo: Delete from here
        Bitmap avatar = null;

        httpUrlConnection = null;

        try {
            httpUrlConnection = (HttpURLConnection) new URL(Constants.DOWNLOAD_IMG_URL + kk)
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
    protected void onPostExecute(Bitmap img) {
        ChatActivity.startImageDownload(img);
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

