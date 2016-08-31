package com.university.gualdras.tfgapp.domain;

import android.graphics.Bitmap;

import com.google.api.services.customsearch.model.Result;

import java.util.ArrayList;
import java.util.HashMap;


public class SuggestedImage {
    String blobUrl;
    String link;
    String siteLink;
    Bitmap bitmap;
    String path;
    HashMap<String, Float> tags;
    ArrayList<String> keyWords;


    public SuggestedImage(String link, String siteLink, Bitmap bitmap, String path) {
        this.link = link;
        this.siteLink = siteLink;
        this.bitmap = bitmap;
        this.path = path;
    }

    public SuggestedImage(Result searchResults, ArrayList<String> keyWords) {
        link = searchResults.getLink();
        siteLink = searchResults.getDisplayLink();
        this.keyWords = keyWords;
    }

    public SuggestedImage(String link, String blobUrl, ArrayList<String> keyWords) {
        this.keyWords = keyWords;
        this.blobUrl = blobUrl;
        this.link = link;
    }

    public String getBlobUrl() {
        return blobUrl;
    }

    public void setBlobUrl(String blobUrl) {
        this.blobUrl = blobUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public void setSiteLink(String siteLink) {
        this.siteLink = siteLink;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HashMap<String, Float> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, Float> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(ArrayList<String> keyWords) {
        this.keyWords = keyWords;
    }

    @Override
    public boolean equals(Object o) {
        SuggestedImage suggestedImage = (SuggestedImage)o;
        if((this.link != null && this.link.equalsIgnoreCase(suggestedImage.link)) || (this.blobUrl != null && this.blobUrl.equals(suggestedImage.blobUrl))){
            return true;
        }
        return false;
    }
}
