package com.university.gualdras.tfgapp.presentation.chat;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.ServerSharedConstants;
import com.university.gualdras.tfgapp.StartActivity;
import com.university.gualdras.tfgapp.gcm.ServerComunication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by gualdras on 22/09/15.
 */
public class ChatActivity extends AppCompatActivity implements MessagesFragment.OnFragmentInteractionListener, OptionsSelectionListener {

    private ListView messageList;
    private TextView contactName;
    private CircleImageView profilePhoto;

    //private MessageListAdapter msgListAdapter;

    private Toolbar toolbar;

    private FragmentManager mFragmentManager;
    private FrameLayout mFrameLayout;
    private GalleryFragment mGallery = new GalleryFragment();
    private WriteMessageFragment mWriteMessage = new WriteMessageFragment();
    private WritableOptionsFragment mWritableOptions = new WritableOptionsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        /*msgListAdapter = new MessageListAdapter(getApplicationContext());
        messageList = (ListView) findViewById(R.id.list_message);
        messageList.setAdapter(msgListAdapter);*/


        profilePhoto = (CircleImageView) findViewById(R.id.photo_chat_profile);
        contactName = (TextView) findViewById(R.id.contact_name);

        toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);

        Intent myIntent = getIntent();
        contactName.setText(myIntent.getStringExtra(Constants.EXTRA_CONTACT_NAME));
        profilePhoto.setImageBitmap((Bitmap) myIntent.getParcelableExtra(Constants.EXTRA_PHOTO_PROFILE));
        mFrameLayout = (FrameLayout) findViewById(R.id.fragment_container);

        if(savedInstanceState == null) {
            // Get a reference to the FragmentManager
            mFragmentManager = getFragmentManager();

            // Start a new FragmentTransaction
            FragmentTransaction fragmentTransaction = mFragmentManager
                    .beginTransaction();

            // Add the TitleFragment to the layout
            fragmentTransaction.add(R.id.fragment_container, mWritableOptions);
            fragmentTransaction.addToBackStack(null);

            // Commit the FragmentTransaction
            fragmentTransaction.commit();

        }
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

    // Methods called by other class to respond to events occurred in this activity


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

    @Override
    public String getProfileEmail() {
        return null;
    }


    //TODO: Put addressee. Fix exceptions.


    public static void sendMessage(final String msg, final String to) {
        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params) {
                HttpURLConnection httpURLConnection = null;
                try {
                    httpURLConnection = (HttpURLConnection) new URL(Constants.USERS_URL + "/" + to + Constants.SEND)
                            .openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JSONObject jsonParam = new JSONObject();
                try {
                    jsonParam.put(ServerSharedConstants.FROM, StartActivity.getPhoneNumber());
                    //jsonParam.put(ServerSharedConstants.TO, to);
                    jsonParam.put(ServerSharedConstants.MSG, msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    ServerComunication.post(httpURLConnection, jsonParam);
                    if(httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED) {
                        httpURLConnection.getResponseCode();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (null != httpURLConnection)
                        httpURLConnection.disconnect();
                }

                return "";
            }
        }.execute(null, null, null);
    }
}


