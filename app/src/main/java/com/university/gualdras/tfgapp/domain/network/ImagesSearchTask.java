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

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;


public class ImagesSearchTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "ImagesSearch";


    private static final String cx = "008487867410969848959:nja-zwqdndm";
    private static final String key = "AIzaSyCFo39giOeV_OH0rE6_H7BWgGNisAeI5UM";

    SharedPreferences sharedPreferences;

    String search;
    ImageInteractionListener mListener;
    ArrayList<String> keyWords;
    ArrayList<String> categories;
    ArrayList<SuggestedImage> suggestedImages = new ArrayList<>();

    public ImagesSearchTask(String search, ArrayList<String> keyWords, ArrayList<String> categories, Activity activity) {
        this.search = search;
        this.keyWords = keyWords;
        this.categories = categories;
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
        if(suggestedImages != null){
            mListener.onImageSearchCompleted(suggestedImages, keyWords);
        }
    }

    public void getRecommendation(){
        HttpURLConnection httpUrlConnection = null;
        StringBuilder parameters = new StringBuilder();
        parameters.append("?" + ServerSharedConstants.USER_ID + "=" + sharedPreferences.getString(Constants.PHONE_NUMBER, ""));
        for(String keyWord: keyWords){
            parameters.append("&" + ServerSharedConstants.KEY_WORDS + "=" +keyWord);
        }
        for(String category: categories){
            parameters.append("&" + ServerSharedConstants.CATEGORIES + "=" + category);
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

                    String response = NetworkUtils.readStream(in);

                    getImagesCollaborative(response);
                    getImagesContentBased(response);
                    getImagesRemaining();
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

    public void getImagesCollaborative(String response) throws JSONException {
        JSONArray items = JSONProcess(response, ServerSharedConstants.IMAGE);
        for(int i=0; i<items.length(); i++){
            JSONObject item = items.getJSONObject(i);
            suggestedImages.add(new SuggestedImage(item.getString(ServerSharedConstants.LINK), item.getString(ServerSharedConstants.BLOB), keyWords));
        }
    }

    public void getImagesContentBased(String response) throws JSONException, IOException {
        ArrayList<String> sites = new ArrayList<>();
        JSONArray items = JSONProcess(response, ServerSharedConstants.SITE_LINK);
        for(int i=0; i<items.length(); i++){
            sites.add(items.getString(i));
        }

        for(int i = 0; i < sites.size(); i++){
            boolean searchingImage = true;
            List<Result> searchResults = searchGSE(sites.get(i));
            for(int j = 0; searchingImage && searchResults != null && j < searchResults.size(); j++){
                SuggestedImage suggestedImage = new SuggestedImage(searchResults.get(j), keyWords);
                if(!suggestedImages.contains(suggestedImage)){
                    suggestedImages.add(suggestedImage);
                    searchingImage = false;
                }
            }
        }
    }

    private JSONArray JSONProcess(String response, String tag) throws JSONException {
        JSONArray items;
            JSONObject responseObject = (JSONObject) new JSONTokener(
                    response).nextValue();

            items = responseObject.getJSONArray(tag);
        return items;
    }



    public void getImagesRemaining() throws IOException {
        List<Result> results = searchGSE(null);

        if(results != null) {
            while (suggestedImages.size() < ServerSharedConstants.NUMBER_OF_IMAGES && results.size() > 0) {
                SuggestedImage suggestedImage = new SuggestedImage(results.remove(0), keyWords);
                if (!suggestedImages.contains(suggestedImage)) suggestedImages.add(suggestedImage);
            }
        }
    }

    private List<Result> searchGSE(@Nullable String site) throws IOException {
        Customsearch customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory(), null);
        com.google.api.services.customsearch.Customsearch.Cse.List list;
        Search results;
        List<Result> searchResults;

            list = customsearch.cse().list(search);
            list.setKey(key);
            list.setCx(cx);
            list.setSearchType("image");
            if(site != null){
                list.setSiteSearch(site);
            }

            results = list.execute();
            searchResults = results.getItems();

        return searchResults;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer data = new StringBuffer("");
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
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
