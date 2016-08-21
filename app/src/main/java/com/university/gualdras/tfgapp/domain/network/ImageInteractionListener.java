package com.university.gualdras.tfgapp.domain.network;

import com.google.api.services.customsearch.model.Result;
import com.university.gualdras.tfgapp.domain.SuggestedImage;

import java.util.List;

/**
 * Created by gualdras on 26/05/16.
 */
public interface ImageInteractionListener {
    void onImageCheckedInDatastore(SuggestedImage suggestedImage);
    void onImageLabeled(SuggestedImage suggestedImage);
    void onImageSearchCompleted(List<Result> searchResults, String text);
    void onSuggestedImageDownloadFinish(SuggestedImage suggestedImage);
}
