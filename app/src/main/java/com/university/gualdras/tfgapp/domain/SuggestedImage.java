package com.university.gualdras.tfgapp.domain;

import android.graphics.Bitmap;

import com.google.api.services.customsearch.model.Result;

import java.util.HashMap;

/**
 * Created by gualdras on 27/05/16.
 */
public class SuggestedImage {
    String blobUrl;
    String link;
    String siteLink;
    String [] flickrTags;
    Bitmap bitmap;
    String path;
    HashMap<String, Float> tags;
    HashMap<String, Integer> keyWords;


    public SuggestedImage(String link, String siteLink, Bitmap bitmap, String path) {
        this.link = link;
        this.siteLink = siteLink;
        this.bitmap = bitmap;
        this.path = path;
    }

    public SuggestedImage(String link, String siteLink, String[] flickrTags, Bitmap bitmap, String path) {
        this.link = link;
        this.siteLink = siteLink;
        this.flickrTags = flickrTags;
        this.bitmap = bitmap;
        this.path = path;
    }

    public SuggestedImage(Result searchResults, HashMap<String, Integer> keyWords) {
            link = searchResults.getLink();
            siteLink = searchResults.getDisplayLink();
        this.keyWords = keyWords;
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

    public String[] getFlickrTags() {
        return flickrTags;
    }

    public void setFlickrTags(String[] flickrTags) {
        this.flickrTags = flickrTags;
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

    public HashMap<String, Integer> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(HashMap<String, Integer> keyWords) {
        this.keyWords = keyWords;
    }
}