package com.university.gualdras.tfgapp.presentation;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.InstallActivity;
import com.university.gualdras.tfgapp.StartActivity;
import com.university.gualdras.tfgapp.domain.ContactItem;
import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.gcm.Preferences;
import com.university.gualdras.tfgapp.gcm.RegistrationIntentService;
import com.university.gualdras.tfgapp.presentation.contactsTab.ContactListAdapter;


public class MainActivity extends AppCompatActivity {

    final int[] ICONS_TABS = {R.drawable.ic_settings, R.drawable.ic_recent_chats, R.drawable.ic_contact};
    final int[] ICONS_SELECTED_TABS = {R.drawable.ic_settings_yellow, R.drawable.ic_recent_chats_yellow, R.drawable.ic_contacts_yellow};
    final int[] NAME_TABS = {R.string.settings_tab, R.string.recent_chats_tab, R.string.contacts_tab};

    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    SharedPreferences sharedPreferences;
    private static Context mContext;

    // TODO - Check for google play services apk
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean(Constants.FIRST_TIME, true)){
            startActivityForResult(new Intent(this, InstallActivity.class), Constants.INSTALL_CODE);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        for(int i = 0; i< NAME_TABS.length; i++){
            tabLayout.addTab(tabLayout.newTab().setText(NAME_TABS[i]).setIcon(ICONS_TABS[i]));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tab.setIcon(ICONS_SELECTED_TABS[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(ICONS_TABS[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(1);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        //gcm stuff
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(Preferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Toast.makeText(context, "Registered", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                }
            }
        };
        
        mContext = this;

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Preferences.REGISTRATION_COMPLETE));
    }

    //TODO - Delete the manual adding of contacts
    @Override
    protected void onStart(){
        super.onStart();

        //Todo: delete
        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.jserrano);
        ContactItem jesus = new ContactItem(bMap, "Jesus");
        ContactListAdapter contactListAdapter = new ContactListAdapter(getApplicationContext());
        contactListAdapter.add(jesus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.INSTALL_CODE && resultCode == RESULT_OK){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constants.FIRST_TIME, false);
            editor.apply();

            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                //if(!sharedPreferences.getBoolean(Preferences.SENT_TOKEN_TO_SERVER, false)){
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
                //}
            }
        }
        else {
            if(requestCode == Constants.INSTALL_CODE && resultCode == RESULT_CANCELED){
                finish();
            }
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}