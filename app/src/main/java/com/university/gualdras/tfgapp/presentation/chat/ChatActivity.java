package com.university.gualdras.tfgapp.presentation.chat;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.Utils;
import com.university.gualdras.tfgapp.domain.InputProcess;
import com.university.gualdras.tfgapp.domain.MessageItem;
import com.university.gualdras.tfgapp.domain.SuggestedImage;
import com.university.gualdras.tfgapp.domain.network.CheckImageDatastoreTask;
import com.university.gualdras.tfgapp.domain.network.ImageInteractionListener;
import com.university.gualdras.tfgapp.domain.network.ImageLabelDetectionTask;
import com.university.gualdras.tfgapp.domain.network.ImagesSearchTask;
import com.university.gualdras.tfgapp.domain.network.SendMessageTask;
import com.university.gualdras.tfgapp.domain.network.SuggestedImageDownloadTask;
import com.university.gualdras.tfgapp.domain.network.UpdateImageTask;
import com.university.gualdras.tfgapp.domain.network.UploadImageTask;
import com.university.gualdras.tfgapp.persistence.DataProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity implements ImageInteractionListener {

    private int SPEECH_CODE = 2134;
    private TextView contactNameTV;
    private CircleImageView profilePhotoIV;

    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private SuggestedImageAdapter mAdapter;
    CountDownTimer timer;

    private FragmentManager mFragmentManager;
    private FrameLayout mFrameLayout;
    private WriteMessageFragment mWriteMessage = new WriteMessageFragment();
    private WritableOptionsFragment mWritableOptions = new WritableOptionsFragment();

    private String contactId;
    private String profileName;
    private String contactPhoneNumber;
    private String contactPhotoId;


    private static Context mContext;

    private int PICK_IMAGE_CODE = 0;

    SharedPreferences sharedPreferences;

    ImagesSearchTask imagesSearchTask;
    ArrayList<SuggestedImageDownloadTask> suggestedImageDownloadTasks = new ArrayList<>();


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

        recyclerView = (RecyclerView) findViewById(R.id.rv_gallery);


        mFrameLayout = (FrameLayout) findViewById(R.id.fragment_container);

        if(savedInstanceState == null) {
            mFragmentManager = getFragmentManager();

            FragmentTransaction fragmentTransaction = mFragmentManager
                    .beginTransaction();

            fragmentTransaction.add(R.id.fragment_container, mWritableOptions);
            fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {

        if (mWriteMessage.isAdded()) {
            if(mAdapter != null)
                mAdapter.clear();
            if(recyclerView != null)
                recyclerView.setVisibility(View.GONE);

            mFragmentManager = getFragmentManager();

            FragmentTransaction fragmentTransaction = mFragmentManager
                    .beginTransaction();

            fragmentTransaction.replace(R.id.fragment_container, mWritableOptions);

            fragmentTransaction.commit();


            mFragmentManager.executePendingTransactions();
        } else {
            super.onBackPressed();
        }
    }

    // Methods called by other class to respond to events occurred in this activity


   public void onWriteMsgSelection() {

        mFragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();

        fragmentTransaction.remove(mWritableOptions);
        fragmentTransaction.add(R.id.fragment_container, mWriteMessage);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
        mFragmentManager.executePendingTransactions();
    }

    public void onSpeechRecognitionSelection() {
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.speech_recognition_hint);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().getDisplayLanguage());
        startActivityForResult(speechIntent, SPEECH_CODE);
    }

    /*
    Method to handle send images that are stored locally
    public void onImgSelection(){
            Intent chooserIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(chooserIntent, PICK_IMAGE_CODE);
    } */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SPEECH_CODE){
            if(resultCode == RESULT_OK){
                String text;
                ArrayList<String> words = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                text = words.get(0);
                onWriteMsgSelection();
                mWriteMessage.changeText(text);
            } else{
                Toast.makeText(this, R.string.not_understood, Toast.LENGTH_SHORT).show();
            }
        }
        /*
        else {
            if(requestCode == PICK_IMAGE_CODE && resultCode == RESULT_OK){
                ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
                Uri uri = data.getData();
                String path = Utils.getRealPathFromURI(this, uri);

                //*This send the image
                messageItem = new MessageItem(sharedPreferences.getString(Constants.PHONE_NUMBER, ""), contactPhoneNumber, MessageItem.IMG_TYPE, "", path);
                new ImageUploadTask(this, path, messageItem).execute();

                //This is for labeling an image
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                bitmaps.add(bitmap);
                new ImageLabelDetectionTask(bitmaps, this).execute();
            }
        }
        */
    }

    public String getContactNumber() {
        return contactPhoneNumber;
    }

    public void onTextChangedListener(final String text){
        if(text.length() > 0 && recyclerView.getVisibility() == View.GONE){
            recyclerView.setVisibility(View.VISIBLE);
        } else{
            if(text.length() == 0){
                recyclerView.setVisibility(View.GONE);
            }
        }
        if(mAdapter != null){
            if(imagesSearchTask != null)
                imagesSearchTask.cancel(true);
            for(SuggestedImageDownloadTask suggestedImageDownloadTask: suggestedImageDownloadTasks){
                suggestedImageDownloadTask.cancel(true);
            }

            mAdapter.clear();
        }
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (text.length() > 0) {
                    String search = InputProcess.cleanInput(text);
                    if (search.length() > 0) {
                        ArrayList<String> keyWords = InputProcess.getSynonyms(search);
                        ArrayList<String> categories = InputProcess.getHypernyms(search);
                        imagesSearchTask = new ImagesSearchTask(search, keyWords, categories, ChatActivity.this);
                        imagesSearchTask.execute();
                    }
                }
            }
        }.start();
    }

    //--------------------------------------------------------------------------------------------------------------//

    @Override
    public void onImageSearchCompleted(ArrayList<SuggestedImage> suggestedImages, ArrayList<String> keyWords) {
        SuggestedImage suggestedImage;

        for (int i = 0; i < suggestedImages.size(); i++) {
            suggestedImage = suggestedImages.get(i);
            SuggestedImageDownloadTask mTask = new SuggestedImageDownloadTask(this, suggestedImage);
            suggestedImageDownloadTasks.add(mTask);
            mTask.execute();
        }
    }


    @Override
    public void onSuggestedImageDownloadFinish(SuggestedImage suggestedImage) {
        if (mAdapter == null) {
            ArrayList<SuggestedImage> suggestedImages = new ArrayList<>();
            suggestedImages.add(suggestedImage);
            mAdapter = new SuggestedImageAdapter(this, suggestedImages);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        } else {
            mAdapter.add(suggestedImage);
        }
    }


    public void onSuggestedImageSelected(SuggestedImage suggestedImage){
        mAdapter.clear();
        recyclerView.setVisibility(View.GONE);
        if(suggestedImage.getBlobUrl() != null ){
            sendImage(suggestedImage);
        } else {
            new CheckImageDatastoreTask(this, suggestedImage).execute();
        }
    }

    @Override
    public void onImageCheckedInDatastore(SuggestedImage suggestedImage) {
        if(suggestedImage.getBlobUrl() != null ){
            sendImage(suggestedImage);
        }else {
            new ImageLabelDetectionTask(this, suggestedImage).execute();
        }
    }

    @Override
    public void onImageLabeled(SuggestedImage suggestedImage) {
        sendImage(suggestedImage);
    }


    public static void sendMessage(MessageItem messageItem) {
        new SendMessageTask(mContext, messageItem).execute();
    }

    public void sendImage(SuggestedImage suggestedImage){
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), suggestedImage.getBitmap(), "image", null);
        Uri uri = Uri.parse(path);
        String imgPath = Utils.getRealPathFromURI(mContext, uri);

        MessageItem messageItem = new MessageItem(sharedPreferences.getString(Constants.PHONE_NUMBER, ""), contactPhoneNumber, MessageItem.IMG_TYPE, suggestedImage.getBlobUrl(), imgPath);

        if(suggestedImage.getBlobUrl() == null){
            new UploadImageTask(mContext, suggestedImage, messageItem).execute();
        } else{
            new UpdateImageTask(mContext, suggestedImage, messageItem).execute();
        }
    }
}