package com.university.gualdras.tfgapp.presentation.chat;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by gualdras on 22/09/15.
 */
public class ChatActivity extends AppCompatActivity implements Constants, OptionsSelectionListener {

    private ListView chatList;
    private Button sendButton;
    private TextView contactName;
    private CircleImageView profilePhoto;
    private EditText editTextWriteMessage;

    private MessageListAdapter msgListAdapter;

    private Toolbar toolbar;

    private FragmentManager mFragmentManager;
    private FrameLayout mFrameLayout;
    private GalleryFragment mGallery = new GalleryFragment();
    private WriteMessageFragment mWriteMessage = new WriteMessageFragment();
    private WritableOptionsFragment mWritableOptions = new WritableOptionsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msgListAdapter = new MessageListAdapter(getApplicationContext());
        setContentView(R.layout.activity_chat);

        chatList = (ListView) findViewById(R.id.list_message);
        chatList.setAdapter(msgListAdapter);
        /*
        sendButton = (Button) findViewById(R.id.button_send);
        sendButton.setOnClickListener(mClickListener);

        editTextWriteMessage = (EditText) findViewById(R.id.et_write_message);
        editTextWriteMessage.setOnEditorActionListener(mEditTextWriteListener);
        */
        profilePhoto = (CircleImageView) findViewById(R.id.photo_chat_profile);
        contactName = (TextView) findViewById(R.id.contact_name);

        toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);

        Intent myIntent = getIntent();
        contactName.setText(myIntent.getStringExtra(Constants.EXTRA_CONTACT_NAME));
        profilePhoto.setImageBitmap((Bitmap) myIntent.getParcelableExtra(Constants.EXTRA_PHOTO_PROFILE));

        // Get a reference to the FragmentManager
        mFrameLayout = (FrameLayout) findViewById(R.id.fragment_container);
        mFragmentManager = getFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();

        // Add the TitleFragment to the layout
        fragmentTransaction.add(R.id.fragment_container, mWritableOptions);

        // Commit the FragmentTransaction
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {

        if (mWriteMessage.isAdded() || mGallery.isAdded()) {
            if (mGallery.isAdded()) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                params.gravity = Gravity.BOTTOM;
                mFrameLayout.setLayoutParams(params);
            }
            mFragmentManager = getFragmentManager();

            // Start a new FragmentTransaction
            FragmentTransaction fragmentTransaction = mFragmentManager
                    .beginTransaction();

            // Add the TitleFragment to the layout
            fragmentTransaction.replace(R.id.fragment_container, mWritableOptions);

            // Commit the FragmentTransaction
            fragmentTransaction.commit();


            mFragmentManager.executePendingTransactions();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (msgListAdapter.getCount() == 0) {
            loadMessages();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveMessages();
    }


    // TODO - save current conversation
    private void saveMessages() {

    }

    // TODO - load messages from a previous conversation
    private void loadMessages() {

    }

    // Methods called by other class to respond to events occurred in this activity

    @Override
    public void onWriteMsgSelection() {
        // Get a reference to the FragmentManager
        mFragmentManager = getFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();

        // Add the TitleFragment to the layout
        fragmentTransaction.remove(mWritableOptions);
        fragmentTransaction.add(R.id.fragment_container, mWriteMessage);
        fragmentTransaction.addToBackStack(null);

        // Commit the FragmentTransaction
        fragmentTransaction.commit();
        mFragmentManager.executePendingTransactions();
    }

    @Override
    public void onSelectImg() {
        mFragmentManager = getFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();

        fragmentTransaction.remove(mWritableOptions);
        // Add the TitleFragment to the layout
        fragmentTransaction.add(R.id.fragment_container, mGallery);
        fragmentTransaction.addToBackStack(null);

        // Commit the FragmentTransaction
        fragmentTransaction.commit();

        mFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        mFragmentManager.executePendingTransactions();
    }
}


