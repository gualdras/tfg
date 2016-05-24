package com.university.gualdras.tfgapp.presentation.chat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.api.services.customsearch.model.Result;
import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.domain.MessageItem;
import com.university.gualdras.tfgapp.domain.network.ImagesSearchTask;
import com.university.gualdras.tfgapp.domain.network.SuggestedImageDownload;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gualdras on 12/10/15.
 */
public class WriteMessageFragment extends Fragment {

    EditText mEditText;
    Button sendBtn;
    RecyclerView recyclerView;

    SharedPreferences sharedPreferences;
    SuggestedImageAdapter mAdapter;

    LinearLayout mLinearLayout;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.write_message_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_gallery);

        mEditText = (EditText) getActivity().findViewById(R.id.et_write_message);
        mEditText.addTextChangedListener(new TextWatcher() {
            CountDownTimer timer = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if(mAdapter != null){
                    mAdapter.clear();
                }
                if (timer != null) {
                    timer.cancel();
                }
                timer = new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        if (s.length() > 0) {
                            new ImagesSearchTask(s.toString(), getActivity()).execute();
                            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                        }
                    }
                }.start();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendBtn = (Button) getActivity().findViewById(R.id.btn_send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo - change destinatary
                if (mEditText.getText().toString().trim().length() != 0) {
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

    public void ImageSearchCompleted(List<Result> searchResults) {
        for (int i = 0; i < searchResults.size() && i < 6; i++) {
            new SuggestedImageDownload(getActivity(), searchResults.get(i).getLink()).execute();
        }
    }

    public void SuggestedImageDownload(Bitmap bitmap) {
        if (mAdapter == null) {
            ArrayList<Bitmap> arrayList = new ArrayList<>();
            arrayList.add(bitmap);
            mAdapter = new SuggestedImageAdapter(arrayList);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        } else {
            mAdapter.add(bitmap);
        }
    }

}
