package com.university.gualdras.tfgapp.domain.network;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gualdras on 17/04/16.
 */
public class ImagesSearchTask extends AsyncTask<Void, Void, ArrayList<Bitmap>> {
    private static final String TAG = "ImagesSearch";


    private static final String cx = "008487867410969848959:nja-zwqdndm";
    private static final String key = "AIzaSyCFo39giOeV_OH0rE6_H7BWgGNisAeI5UM";

    String keyWords;
    Activity activity;

    public ImagesSearchTask(String keyWords, Activity activity) {
        this.keyWords = keyWords;
        this.activity = activity;
    }

    @Override
    protected ArrayList<Bitmap> doInBackground(Void... params) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        Customsearch customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory(), null);
        com.google.api.services.customsearch.Customsearch.Cse.List list;
        Search results;
        List<Result> items;

        try {
            list = customsearch.cse().list(keyWords);
            list.setKey(key);
            list.setCx(cx);
            list.setSearchType("image");

            results = list.execute();
            items = results.getItems();

            for (int i = 0; i<15 && i < items.size(); i++) {
                HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(items.get(i).getLink())
                        .openConnection();

                InputStream in = new BufferedInputStream(
                        httpUrlConnection.getInputStream());

                bitmaps.add(BitmapFactory.decodeStream(in));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmaps;
    }

    @Override
    protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
        if(bitmaps != null){
            new ImageLabelDetectionTask(bitmaps, activity).execute();
        } else{
            Toast.makeText(activity, "No results", Toast.LENGTH_SHORT);
        }
    }
}
