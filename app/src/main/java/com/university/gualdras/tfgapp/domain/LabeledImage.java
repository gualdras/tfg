package com.university.gualdras.tfgapp.domain;

import android.graphics.Bitmap;

/**
 * Created by gualdras on 23/04/16.
 */
public class LabeledImage {
    Bitmap image;
    String labels;

    public LabeledImage(Bitmap image, String labels) {
        this.image = image;
        this.labels = labels;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }
}
