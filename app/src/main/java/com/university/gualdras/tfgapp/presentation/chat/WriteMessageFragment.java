package com.university.gualdras.tfgapp.presentation.chat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.domain.MessageItem;

/**
 * Created by gualdras on 12/10/15.
 */
public class WriteMessageFragment extends Fragment{

    EditText mEditText;
    Button sendBtn;

    SharedPreferences sharedPreferences;


    private MessagesFragment.OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MessagesFragment.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
                //Todo - change destinatary
                if(mEditText.getText().toString().trim().length() != 0) {
                    MessageItem messageItem = new MessageItem(sharedPreferences.getString(Constants.PHONE_NUMBER, ""), mListener.getContactNumber(), MessageItem.TEXT_TYPE, mEditText.getText().toString());
                    ChatActivity.sendMessage(messageItem);
                    mEditText.getText().clear();
                }
            }
        });
        mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    @Override
    public void onResume() {
        super.onResume();

    }

}
