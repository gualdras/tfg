package com.university.gualdras.tfgapp.domain;

import java.lang.String;

/**
 * Created by gualdras on 11/10/15.
 */
public class FriendProfile {

    private int mobileNumber;
    private String friendName, photo;

    public FriendProfile(int mobileNumber, String friendName, String photo) {
        this.mobileNumber = mobileNumber;
        this.friendName = friendName;
        this.photo = photo;
    }

    public int getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(int mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
