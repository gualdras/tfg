package com.university.gualdras.tfgapp.domain.network;

import com.google.api.services.customsearch.model.Result;

import java.util.List;

/**
 * Created by gualdras on 12/05/16.
 */
public interface ImageSearchListener {
    void onImageSearchCompleted(List<Result> searchResults);
}
