package com.university.gualdras.tfgapp.domain.network;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

import java.io.IOException;
import java.util.List;

/**
 * Created by gualdras on 17/04/16.
 */
public class ImagesSearchTask extends AsyncTask<Void, Void, List<Result>> {
    private static final String TAG = "ImagesSearch";


    private static final String cx = "008487867410969848959:nja-zwqdndm";
    private static final String key = "AIzaSyCFo39giOeV_OH0rE6_H7BWgGNisAeI5UM";

    String keyWords;
    ImageInteractionListener mListener;

    public ImagesSearchTask(String keyWords, Activity activity) {
        this.keyWords = keyWords;
        this.mListener = (ImageInteractionListener) activity;
    }

    @Override
    protected List<Result> doInBackground(Void... params) {
        Customsearch customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory(), null);
        com.google.api.services.customsearch.Customsearch.Cse.List list;
        Search results;
        List<Result> searchResults = null;

        try {
            list = customsearch.cse().list(keyWords);
            list.setKey(key);
            list.setCx(cx);
            list.setSearchType("image");

            results = list.execute();
            searchResults = results.getItems();

            /*for (int i = 0; i<15 && i < items.size(); i++) {
                HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(items.get(i).getLink())
                        .openConnection();

                InputStream in = new BufferedInputStream(
                        httpUrlConnection.getInputStream());

                bitmaps.add(BitmapFactory.decodeStream(in));
            }*/
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return searchResults;
    }

    @Override
    protected void onPostExecute(List<Result> searchResults) {
        if(searchResults != null){
            mListener.onImageSearchCompleted(searchResults, keyWords);
        }
    }
}
