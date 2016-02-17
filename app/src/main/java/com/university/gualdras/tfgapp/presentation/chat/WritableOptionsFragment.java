package com.university.gualdras.tfgapp.presentation.chat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.presentation.chat.OptionsSelectionListener;

/**
 * Created by gualdras on 12/10/15.
 */

public class WritableOptionsFragment extends Fragment {

    private ImageButton writeMsgBtn, selectImageBtn;

    private OptionsSelectionListener mListener;


    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OptionsSelectionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OptionSelectionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.writable_options_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        writeMsgBtn = (ImageButton) getActivity().findViewById(R.id.write_message_btn);
        writeMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onWriteMsgSelection();
            }
        });
        selectImageBtn = (ImageButton) getActivity().findViewById(R.id.select_image_btn);
        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSelectImg();
            }
        });
    }
}
