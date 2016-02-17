package com.university.gualdras.tfgapp.domain;

import android.graphics.Bitmap;

/**
 * Created by gualdras on 20/09/15.
 */

public class ContactItem {

    private Bitmap photoProfile;
    private String contactName;

    public ContactItem() {
    }

    public ContactItem(Bitmap photoProfile, String contactName) {
        this.photoProfile = photoProfile;
        this.contactName = contactName;
    }

    public Bitmap getPhotoProfile() {
        return photoProfile;
    }

    public String getContactName() {
        return contactName;
    }

    public void setPhotoProfile(Bitmap photoProfile) {
        this.photoProfile = photoProfile;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
