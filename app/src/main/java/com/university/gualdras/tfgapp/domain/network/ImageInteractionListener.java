package com.university.gualdras.tfgapp.domain.network;

import android.graphics.Bitmap;

import com.google.api.services.customsearch.model.Result;
import com.university.gualdras.tfgapp.domain.LabeledImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gualdras on 26/05/16.
 */
public interface ImageInteractionListener {
    void onImageLabeled(ArrayList<LabeledImage> labeledImages);
    void onImageSearchCompleted(List<Result> searchResults);
    void onSuggestedImageDownloadFinish(Bitmap bitmap);
}
