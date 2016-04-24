package com.university.gualdras.tfgapp.domain.network;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.domain.MessageItem;
import com.university.gualdras.tfgapp.presentation.chat.ChatActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
public class ImageUploadTask extends AsyncTask<Void, Void, String> {

    private static final String ITEMS_TAG = "items";
    private static final String TAG = "ImageUpload";

    String path;
    Activity mContext;
    MessageItem messageItem;

    SharedPreferences sharedPreferences;

    public ImageUploadTask(Activity context, String path, MessageItem messageItem){
        this.path = path;
        this.mContext = context;
        this.messageItem = messageItem;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    protected String doInBackground(Void... params) {
        String blobURL = getBlobURL();
        String imgURL = uploadImage(blobURL);

        return imgURL;
    }

    private String getBlobURL(){
        String blobUrl = "";
        HttpURLConnection httpUrlConnection = null;

        try {
            httpUrlConnection = (HttpURLConnection) new URL(Constants.UPLOAD_FORM_URL)
                    .openConnection();

            switch (httpUrlConnection.getResponseCode()){
                case HttpURLConnection.HTTP_OK:
                    InputStream in = new BufferedInputStream(
                            httpUrlConnection.getInputStream());

                    blobUrl = NetworkUtils.readStream(in);
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    InputStream err = new BufferedInputStream(httpUrlConnection.getErrorStream());

                    blobUrl = NetworkUtils.readStream(err);
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

        return blobUrl;
    }

    private String uploadImage(String imgUploadURL){
        String imgURL = "";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "image.png", RequestBody.create(MediaType.parse("image/png"), new File(path)))
                .addFormDataPart("title", "My photo")
                .build();

        Request request = new Request.Builder()
                .url(imgUploadURL)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            imgURL = response.body().string();
            Log.d("response", "uploadImage:"+imgURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgURL;
    }

    @Override
    protected void onPostExecute(String imgURL) {
        messageItem.setMsg(imgURL);
        ChatActivity.sendMessage(messageItem);
    }
}

