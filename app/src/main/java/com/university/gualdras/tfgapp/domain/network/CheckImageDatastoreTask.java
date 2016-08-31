package com.university.gualdras.tfgapp.domain.network;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.ServerSharedConstants;
import com.university.gualdras.tfgapp.domain.SuggestedImage;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckImageDatastoreTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "CheckImageDatastore";

    SuggestedImage suggestedImage;
    ImageInteractionListener mListener;

    public CheckImageDatastoreTask(Activity mActivity, SuggestedImage suggestedImage) {
        this.suggestedImage = suggestedImage;
        this.mListener = (ImageInteractionListener) mActivity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection httpUrlConnection = null;

        try {
            String url = Constants.IMAGES_URL + "?" + ServerSharedConstants.LINK + "=" + suggestedImage.getLink();
            httpUrlConnection = (HttpURLConnection) new URL(url)
                    .openConnection();

            httpUrlConnection.setRequestMethod("GET");

            switch (httpUrlConnection.getResponseCode()){
                case HttpURLConnection.HTTP_OK:
                    InputStream in = new BufferedInputStream(
                            httpUrlConnection.getInputStream());

                    String blob = JSONProcess(NetworkUtils.readStream(in));

                    suggestedImage.setBlobUrl(blob);

                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    InputStream err = new BufferedInputStream(httpUrlConnection.getErrorStream());
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

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mListener.onImageCheckedInDatastore(suggestedImage);
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

    private String JSONProcess (String response){
        String result = null;
        try {
            JSONObject responseObject = (JSONObject) new JSONTokener(
                    response).nextValue();
            result = responseObject.getString(ServerSharedConstants.BLOB);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
