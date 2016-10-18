package com.university.gualdras.tfgapp.domain;

import android.content.ContentValues;
import android.content.Context;

import com.university.gualdras.tfgapp.persistence.DataProvider;



public class ContactItem {

    private String photoProfile;
    private String contactName;
    private String phoneNumber;

    public ContactItem() {
    }

    public ContactItem(String photoProfile, String contactName) {
        this.photoProfile = photoProfile;
        this.contactName = contactName;
    }

    public ContactItem(String photoProfile, String contactName, String phoneNumber) {
        this.photoProfile = photoProfile;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
    }

    public String getPhotoProfile() {
        return photoProfile;
    }

    public String getContactName() {
        return contactName;
    }

    public void setPhotoProfile(String photoProfile) {
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

    public void saveContact(Context mContext){
        ContentValues values = new ContentValues(3);
        values.put(DataProvider.COL_NAME, contactName);
        values.put(DataProvider.COL_PHONE_NUMBER, phoneNumber);
        values.put(DataProvider.COL_PHOTO, photoProfile);
        mContext.getContentResolver().insert(DataProvider.CONTENT_URI_PROFILE, values);
    }
}
