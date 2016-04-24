package com.university.gualdras.tfgapp.presentation.chat;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.domain.network.ImageUploadTask;
import com.university.gualdras.tfgapp.domain.MessageItem;
import com.university.gualdras.tfgapp.domain.network.SendMessageTask;
import com.university.gualdras.tfgapp.persistence.DataProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by gualdras on 22/09/15.
 */
public class ChatActivity extends AppCompatActivity implements MessagesFragment.OnFragmentInteractionListener, OptionsSelectionListener {

    private int SPEECH_CODE = 2134;
    private TextView contactNameTV;
    private CircleImageView profilePhotoIV;

    private Toolbar toolbar;

    private FragmentManager mFragmentManager;
    private FrameLayout mFrameLayout;
    private GalleryFragment mGallery = new GalleryFragment();
    private WriteMessageFragment mWriteMessage = new WriteMessageFragment();
    private WritableOptionsFragment mWritableOptions = new WritableOptionsFragment();

    private String contactId;
    private String profileName;
    private String contactPhoneNumber;
    private String contactPhotoId;


    private static Context mContext;

    private int PICK_IMAGE_CODE = 0;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_chat);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent myIntent = getIntent();
        contactId = myIntent.getStringExtra(Constants.EXTRA_CONTACT_ID);

        Cursor c = getContentResolver().query(Uri.withAppendedPath(DataProvider.CONTENT_URI_PROFILE, contactId), null, null, null, null);
        if (c.moveToFirst()) {
            profileName = c.getString(c.getColumnIndex(DataProvider.COL_NAME));
            contactPhoneNumber = c.getString(c.getColumnIndex(DataProvider.COL_PHONE_NUMBER));
            contactPhotoId = c.getString(c.getColumnIndex(DataProvider.COL_PHOTO));
        }

        contactNameTV = (TextView) findViewById(R.id.chat_contact_name);
        contactNameTV.setText(profileName);

        profilePhotoIV = (CircleImageView) findViewById(R.id.chat_profile_photo);
        try {
            profilePhotoIV.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(contactPhotoId)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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


/*    public void onWriteMsgSelection() {

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
    }*/

    @Override
    public void onWriteMsgSelection() {
        Intent intent = new Intent(this, KK.class);
        startActivity(intent);
    }

    /*
        public void onImgSelection() {
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
    */
    public void onImgSelection(){
            Intent chooserIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(chooserIntent, PICK_IMAGE_CODE);
    }

    @Override
    public void onSpeechRecognitionSelection() {
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.speech_recognition_hint);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().getDisplayLanguage());
        startActivityForResult(speechIntent, SPEECH_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MessageItem messageItem;
        if(requestCode == SPEECH_CODE){
            if(resultCode == RESULT_OK){
                String msg = "";
                ArrayList<String> words = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                for(String w: words){
                    msg += w;
                }
                messageItem = new MessageItem(sharedPreferences.getString(Constants.PHONE_NUMBER, ""), contactPhoneNumber, MessageItem.TEXT_TYPE, msg);
                sendMessage(messageItem);
            } else{
                Toast.makeText(this, "Not understood", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if(requestCode == PICK_IMAGE_CODE && resultCode == RESULT_OK){
                Uri uri = data.getData();
                String path = getRealPathFromURI(uri);
                messageItem = new MessageItem(sharedPreferences.getString(Constants.PHONE_NUMBER, ""), contactPhoneNumber, MessageItem.IMG_TYPE, "", path);
                new ImageUploadTask(this, path, messageItem).execute();
            }
        }
    }


    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    public String getContactNumber() {
        return contactPhoneNumber;
    }


    public static void sendMessage(MessageItem messageItem) {
        new SendMessageTask(mContext, messageItem).execute();
    }
}


