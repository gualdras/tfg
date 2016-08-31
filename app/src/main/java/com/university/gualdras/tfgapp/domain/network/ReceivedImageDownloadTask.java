package com.university.gualdras.tfgapp.domain.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.Utils;
import com.university.gualdras.tfgapp.domain.MessageItem;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ReceivedImageDownloadTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "ImageDownload";
    Context mContext;
    MessageItem messageItem;

    public ReceivedImageDownloadTask(Context context, MessageItem messageItem){
        this.mContext = context;
        this.messageItem = messageItem;
    }

    @Override
    protected String doInBackground(Void... params) {
        Bitmap bitmap = null;
        HttpURLConnection httpUrlConnection = null;
        String imgPath;

        try {
            httpUrlConnection = (HttpURLConnection) new URL(Constants.IMAGES_URL + "/" + messageItem.getMsg())
                    .openConnection();

            switch (httpUrlConnection.getResponseCode()){
                case HttpURLConnection.HTTP_OK:
                    InputStream in = new BufferedInputStream(
                            httpUrlConnection.getInputStream());

                    bitmap = BitmapFactory.decodeStream(in);
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
        imgPath = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "img" , null);
        return imgPath;
    }

    @Override
    protected void onPostExecute(String imgPath) {
        Uri uri = Uri.parse(imgPath);
        String path = Utils.getRealPathFromURI(mContext,  uri);
        messageItem.setLocalResource(path);
        messageItem.saveMessageReceived(mContext);
    }
}
