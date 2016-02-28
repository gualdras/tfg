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
                    Toast.makeText(context, "Registered", Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG);
                }
            }
        };


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            //if(!sharedPreferences.getBoolean(Preferences.SENT_TOKEN_TO_SERVER, false)){
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            //}
        }
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean(Constants.FIRST_TIME, true)){
            getPhoneNumber();
        }
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

    private void getPhoneNumber(){
        DialogFragment mDialog = UserPhoneNumberFragment.newInstance();
        mDialog.show(getFragmentManager(), getString(R.string.phonenumber_dialog_title));
    }


    //Todo: review the utility of trim()
    private String GetCountryZipCode(){
        String countryID;
        String CountryZipCode = "";
        boolean found = false;

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        countryID = manager.getSimCountryIso().toUpperCase().trim();
        String[] countryCodes = this.getResources().getStringArray(R.array.CountryCodes);
        for(int i = 0; i < countryCodes.length && !found; i++){
            String[] c = countryCodes[i].split(",");
            if(c[1].trim().equals(countryID)){
                CountryZipCode = c[0].trim();
                found = true;
            }
        }
        return CountryZipCode;
    }

    private static int getDefaultCountryCodePosition(Spinner countryCodes){
        ArrayAdapter adapter = (ArrayAdapter) countryCodes.getAdapter();
        TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        int spinnerPosition = 0;
        String countryID = manager.getSimCountryIso().toUpperCase().trim();
        String[] codes = mContext.getResources().getStringArray(R.array.CountryCodes);
        for(String c: codes){
            String [] codeParts = c.split(",");
            if(codeParts[1].trim().equals(countryID)){
                spinnerPosition = adapter.getPosition(c);
            }
        }
        return spinnerPosition;
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

    public static class UserPhoneNumberFragment extends DialogFragment {

        Spinner spinnerCodes;
        EditText phoneNumberET;

        public static UserPhoneNumberFragment newInstance(){
            return new UserPhoneNumberFragment();
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.phonenumber_dialog, null);
            spinnerCodes = (Spinner) view.findViewById(R.id.country_code_spinner);
            phoneNumberET = (EditText) view.findViewById(R.id.phone_number_et);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.CountryCodes, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCodes.setAdapter(adapter);
            spinnerCodes.setSelection(getDefaultCountryCodePosition(spinnerCodes));

            //Todo: Try it with normal app and appcompat
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(view);

            builder.setCancelable(false);

            builder.setMessage(getString(R.string.phonenumber_dialog_message));

            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String completePhoneNumber;
                    String countryCode = (String) spinnerCodes.getSelectedItem();
                    String localNumber = phoneNumberET.getText().toString();
                    completePhoneNumber = countryCode.split(",")[0] + localNumber;
                    StartActivity.setPhoneNumber(completePhoneNumber);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(Constants.FIRST_TIME, false);
                    editor.commit();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    getActivity().finish();
                    System.exit(0);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(phoneNumberET, InputMethodManager.SHOW_IMPLICIT);
                }
            });
            return dialog;
        }
    }
}