package com.university.gualdras.tfgapp.domain.network;

import com.university.gualdras.tfgapp.domain.LabeledImage;

import java.util.ArrayList;

/**
 * Created by gualdras on 25/04/16.
 */
public interface ImageLabeledListener {
    void onImageLabeled(ArrayList<LabeledImage> labeledImages);
}
