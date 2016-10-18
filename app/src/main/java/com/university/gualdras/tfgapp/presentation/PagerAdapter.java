package com.university.gualdras.tfgapp.presentation;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.university.gualdras.tfgapp.presentation.configurationTab.ConfigurationTab;
import com.university.gualdras.tfgapp.presentation.contactsTab.ContactTab;
import com.university.gualdras.tfgapp.presentation.recentChatTab.RecentChatTab;

public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ConfigurationTab();
            case 1:
                return new ContactTab();
            case 2:
                return new RecentChatTab();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

