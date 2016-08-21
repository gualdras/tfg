package com.university.gualdras.tfgapp.domain.network;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.ServerSharedConstants;
import com.university.gualdras.tfgapp.domain.SuggestedImage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gualdras on 17/04/16.
 */
public class ImagesSearchTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "ImagesSearch";


    private static final String cx = "008487867410969848959:nja-zwqdndm";
    private static final String key = "AIzaSyCFo39giOeV_OH0rE6_H7BWgGNisAeI5UM";

    SharedPreferences sharedPreferences;

    String search;
    ImageInteractionListener mListener;
    ArrayList<String> keyWords;

    ArrayList<SuggestedImage> suggestedImages = new ArrayList<>();

    public ImagesSearchTask(String search, ArrayList<String> keyWords, Activity activity) {
        this.search = search;
        this.keyWords = keyWords;
        this.mListener = (ImageInteractionListener) activity;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    protected Void doInBackground(Void... params) {
        getRecommendation();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        /*
        if(searchResults != null){
            mListener.onImageSearchCompleted(searchResults, keyWords);
        }
        */
    }

    public void getRecommendation(){
        HttpURLConnection httpUrlConnection = null;
        StringBuilder parameters = new StringBuilder();
        parameters.append("?" + ServerSharedConstants.USER_ID + "=" + sharedPreferences.getString(Constants.PHONE_NUMBER, ""));
        for(String keyWord: keyWords){
            parameters.append("&" + ServerSharedConstants.KEY_WORDS + "=" +keyWord);
        }
        try {
            String url = Constants.IMAGES_URL + parameters.toString();
            httpUrlConnection = (HttpURLConnection) new URL(url)
                    .openConnection();

            httpUrlConnection.setRequestMethod("GET");

            int responseCode = httpUrlConnection.getResponseCode();

            switch (responseCode){
                case HttpURLConnection.HTTP_OK:
                    InputStream in = new BufferedInputStream(
                            httpUrlConnection.getInputStream());

                    String response = readStream(in);


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
    }

    public void getImagesColaborative(ArrayList<String> blobs, ArrayList<String> links){
        for(int i = 0; i < blobs.size(); i++){
            suggestedImages.add(new SuggestedImage(keyWords, blobs.get(i), links.get(i)));
        }
    }

    public void getSites(String[] sites){
        for(int i = 0; sites != null && i < sites.length; i++){
            boolean searchingImage = true;
            List<Result> searchResults = searchGSE(sites[i]);
            for(int j = 0; searchingImage && j < searchResults.size(); j++){
                SuggestedImage suggestedImage = new SuggestedImage(searchResults.get(i), keyWords);
                if(!suggestedImages.contains(suggestedImage)){
                    suggestedImages.add(suggestedImage);
                    searchingImage = false;
                }
            }
        }
    }

    public void getImagesRemaining(){
        List<Result> results = searchGSE(null);
    }

    private List<Result> searchGSE(@Nullable String site){
        Customsearch customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory(), null);
        com.google.api.services.customsearch.Customsearch.Cse.List list;
        Search results;
        List<Result> searchResults = null;

        try {
            list = customsearch.cse().list(search);
            list.setKey(key);
            list.setCx(cx);
            list.setSearchType("image");
            if(site != null){
                list.setSiteSearch(search);
            }

            results = list.execute();
            searchResults = results.getItems();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for(Result result: searchResults){
            new SuggestedImage(result, keyWords);
        }
        return searchResults;
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
