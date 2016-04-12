package com.university.gualdras.tfgapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;

/**
 * Created by gualdras on 9/02/16.
 */
public class StartActivity extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    static String mPhoneNumber = null;

    @Override
    public void onCreate() {
        super.onCreate();

        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = tMgr.getLine1Number();
    }
}