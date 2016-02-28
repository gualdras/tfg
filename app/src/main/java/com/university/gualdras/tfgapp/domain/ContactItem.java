package com.university.gualdras.tfgapp.domain;

import android.graphics.Bitmap;

/**
 * Created by gualdras on 20/09/15.
 */

public class ContactItem {

    private Bitmap photoProfile;
    private String contactName;
    private String phoneNumber;

    public ContactItem() {
    }


    public ContactItem(Bitmap photoProfile, String contactName) {
        this.photoProfile = photoProfile;
        this.contactName = contactName;
    }

    public ContactItem(Bitmap photoProfile, String contactName, String phoneNumber) {
        this.photoProfile = photoProfile;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
