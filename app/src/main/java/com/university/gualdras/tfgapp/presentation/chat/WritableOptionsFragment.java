package com.university.gualdras.tfgapp.presentation.chat;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.university.gualdras.tfgapp.R;

/**
 * Created by gualdras on 12/10/15.
 */

public class WritableOptionsFragment extends Fragment {

    private ImageButton writeMsgBtn, selectImageBtn, speechRecognitionBtn;


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
                ((ChatActivity)getActivity()).onWriteMsgSelection();
            }
        });
/*        selectImageBtn = (ImageButton) getActivity().findViewById(R.id.select_image_btn);
        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ChatActivity)getActivity()).onImgSelection();
            }
        });
        speechRecognitionBtn = (ImageButton) getActivity().findViewById(R.id.speech_recognition_btn);
        speechRecognitionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ChatActivity)getActivity()).onSpeechRecognitionSelection();
            }
        });*/
    }
}
