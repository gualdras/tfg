package com.university.gualdras.tfgapp;

import android.os.Environment;

/**
 * Created by gualdras on 25/09/15.
 */
public interface Constants {

    public static final String EXTRA_CONTACT_ID = "ExtraContactId";
    public static final String EXTRA_PHOTO_PROFILE = "ExtraPhotoProfile";

    //preferences

    String FIRST_TIME = "firstTime";
    String PHONE_NUMBER = "phoneNumber";

    int MAX_ATTEMPTS = 7;

    int INSTALL_CODE = 1;

    String DEFAULT_CONTACT_PHOTO = "android.resource://com.university.gualdras.tfgapp/" + R.drawable.contact_photo;


    String PATH_TO_DICT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tfg/dict/";
    String PATH_TO_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tfg/";

    //Server urls
    public static final String SENDER_ID = "122334701005";
    public static final String SERVER_URL = "http://tfg-server.appspot.com";
    String USERS_URL = SERVER_URL + "/users";
    String SEND = "/send";
    String IMAGES_URL = SERVER_URL + "/images";
    String UPLOAD_FORM_URL = SERVER_URL + "/upload_form";
}
