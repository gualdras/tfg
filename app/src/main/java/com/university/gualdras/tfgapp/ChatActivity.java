package com.university.gualdras.tfgapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by gualdras on 22/09/15.
 */
public class ChatActivity extends AppCompatActivity implements Constants{

    private ListView chatList;
    private Button sendButton;
    private TextView contactName;
    private CircleImageView profilePhoto;
    private EditText editTextWriteMessage;

    private MessageListAdapter msgListAdapter;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msgListAdapter = new MessageListAdapter(getApplicationContext());
        setContentView(R.layout.activity_chat);

        chatList = (ListView) findViewById(R.id.list_message);
        chatList.setAdapter(msgListAdapter);

        sendButton = (Button) findViewById(R.id.button_send);
        sendButton.setOnClickListener(mClickListener);

        editTextWriteMessage = (EditText) findViewById(R.id.et_write_message);
        editTextWriteMessage.setOnEditorActionListener(mEditTextWriteListener);

        profilePhoto = (CircleImageView) findViewById(R.id.photo_chat_profile);
        contactName = (TextView) findViewById(R.id.contact_name);

        toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);

        Intent myIntent = getIntent();
        contactName.setText(myIntent.getStringExtra(Constants.EXTRA_CONTACT_NAME));
        profilePhoto.setImageBitmap((Bitmap) myIntent.getParcelableExtra(Constants.EXTRA_PHOTO_PROFILE));

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(msgListAdapter.getCount() == 0){
            loadMessages();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveMessages();
    }

    private TextView.OnClickListener mClickListener = new TextView.OnClickListener() {

        @Override
        public void onClick(View v) {

        }
    };

    private TextView.OnEditorActionListener mEditTextWriteListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            return false;
        }
    };

        // TODO - save current conversation
    private void saveMessages(){

    }

    // TODO - load messages from a previous conversation
    private void loadMessages(){

    }
}
