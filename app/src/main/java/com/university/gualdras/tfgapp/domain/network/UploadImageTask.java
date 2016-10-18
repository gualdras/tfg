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


public class UploadImageTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "ImageUpload";

    Context mContext;
    MessageItem messageItem;
    SuggestedImage suggestedImage;

    public UploadImageTask(Context context, SuggestedImage suggestedImage, MessageItem messageItem){
        this.suggestedImage = suggestedImage;
        this.mContext = context;
        this.messageItem = messageItem;
    }

    @Override
    protected String doInBackground(Void... params) {
        String imgURL = suggestedImage.getBlobUrl();
        if(imgURL == null){
            String blobURL = getBlobURL();

            imgURL = uploadImage(blobURL);
            suggestedImage.setBlobUrl(imgURL);
            putImageInformation();
        }
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
                .addFormDataPart("file", "image.png", RequestBody.create(MediaType.parse("image/png"), new File(messageItem.getLocalResource())))
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

    private void putImageInformation(){
        HttpURLConnection httpUrlConnection = null;

        try {
            httpUrlConnection = (HttpURLConnection) new URL(Constants.IMAGES_URL + "/" + suggestedImage.getBlobUrl())
                    .openConnection();

            httpUrlConnection.setRequestMethod("PUT");
            httpUrlConnection.setRequestProperty("Content-Type", "application/json");

            DataOutputStream wr = new DataOutputStream(httpUrlConnection.getOutputStream ());

            JSONObject jsonParam = new JSONObject();
            jsonParam.put(ServerSharedConstants.LINK, suggestedImage.getLink());
            jsonParam.put(ServerSharedConstants.SITE_LINK, suggestedImage.getSiteLink());
            jsonParam.put(ServerSharedConstants.TAG, new JSONObject(suggestedImage.getTags()));
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
    }

    @Override
    protected void onPostExecute(String imgURL) {
        messageItem.setMsg(imgURL);
        new SendMessageTask(mContext, messageItem).execute();
    }
}

