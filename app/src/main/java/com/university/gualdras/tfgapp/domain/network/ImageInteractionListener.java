package com.university.gualdras.tfgapp.domain.network;

import com.university.gualdras.tfgapp.domain.SuggestedImage;

import java.util.ArrayList;


public interface ImageInteractionListener {
    void onImageCheckedInDatastore(SuggestedImage suggestedImage);
    void onImageLabeled(SuggestedImage suggestedImage);
    void onImageSearchCompleted(ArrayList<SuggestedImage> suggestedImages, ArrayList<String> keyWords);
    void onSuggestedImageDownloadFinish(SuggestedImage suggestedImage);
}
