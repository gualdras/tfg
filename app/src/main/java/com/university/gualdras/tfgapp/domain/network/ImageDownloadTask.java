package com.university.gualdras.tfgapp.domain.network;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.domain.MessageItem;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gualdras on 29/03/16.
 */
public class ImageDownloadTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "ImageDownload";
    Context mContext;
    MessageItem messageItem;

    public ImageDownloadTask(Context context, MessageItem messageItem){
        this.mContext = context;
        this.messageItem = messageItem;
    }

    @Override
    protected String doInBackground(Void... params) {
        Bitmap bitmap = null;
        HttpURLConnection httpUrlConnection = null;
        String imgPath;

        try {
            httpUrlConnection = (HttpURLConnection) new URL(Constants.DOWNLOAD_IMG_URL + messageItem.getMsg())
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
        String path = getRealPathFromURI(uri);
        messageItem.setLocalResource(path);
        messageItem.saveMessageReceived(mContext);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}
