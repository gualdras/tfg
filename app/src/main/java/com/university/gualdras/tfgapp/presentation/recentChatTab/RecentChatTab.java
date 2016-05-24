package com.university.gualdras.tfgapp.presentation.recentChatTab;

/**
 * Created by gualdras on 19/09/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.university.gualdras.tfgapp.R;

public class RecentChatTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_recent_chat, container, false);
    }
}
