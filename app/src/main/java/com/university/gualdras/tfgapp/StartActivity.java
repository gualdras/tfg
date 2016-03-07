package com.university.gualdras.tfgapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

/**
 * Created by gualdras on 9/02/16.
 */
public class StartActivity extends Application {

    static String mPhoneNumber= null;
    @Override
    public void onCreate() {
        super.onCreate();

        TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = tMgr.getLine1Number();
    }

    public static String getPhoneNumber(){
        return mPhoneNumber;
    }

    public static void setPhoneNumber(String phoneNumber){
        mPhoneNumber = phoneNumber;
    }
}
