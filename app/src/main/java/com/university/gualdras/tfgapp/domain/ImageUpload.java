package com.university.gualdras.tfgapp.domain;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.university.gualdras.tfgapp.Constants;

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
public class ImageUpload extends AsyncTask<Void, Void, String> {

    private static final String ITEMS_TAG = "items";
    private static final String TAG = "ImageUpload";

    String path;
    Context context;

    public ImageUpload(Context context, String blobkey){
        this.path = blobkey;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
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
        return kk;
    }

    @Override
    protected void onPostExecute(String blobKey) {
        new ImageDownload(context, blobKey).execute();
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

