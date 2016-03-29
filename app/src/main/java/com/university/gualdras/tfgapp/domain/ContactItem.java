package com.university.gualdras.tfgapp.domain;

/**
 * Created by gualdras on 20/09/15.
 */

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
}
