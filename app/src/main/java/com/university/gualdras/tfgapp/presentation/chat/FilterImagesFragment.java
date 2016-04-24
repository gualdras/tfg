package com.university.gualdras.tfgapp.presentation.chat;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.domain.network.ImagesSearchTask;

/**
 * Created by gualdras on 17/04/16.
 */
public class FilterImagesFragment extends Fragment{
    EditText mEditText;
    Button sendBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.write_message_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mEditText = (EditText) getActivity().findViewById(R.id.et_write_message);
        sendBtn = (Button) getActivity().findViewById(R.id.btn_send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditText.getText().toString().trim().length() != 0) {
                    new ImagesSearchTask(mEditText.getText().toString().trim()).execute();
                    mEditText.getText().clear();
                }
            }
        });
        mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
