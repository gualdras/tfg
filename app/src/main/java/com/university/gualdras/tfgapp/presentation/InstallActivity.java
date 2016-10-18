package com.university.gualdras.tfgapp.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.domain.CopyDictionary;
import com.university.gualdras.tfgapp.gcm.Preferences;
import com.university.gualdras.tfgapp.gcm.RegistrationIntentService;


public class InstallActivity extends AppCompatActivity{

    private static final String TAG = "InstallActivity";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    Spinner spn;
    EditText et;
    TextView tv;
    Button okBtn;
    SharedPreferences sharedPreferences;

    //ProgressDialog progressDialog;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_install);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        et = (EditText) findViewById(R.id.phone_number_et);

        tv = (TextView) findViewById(R.id.installation_information_tv);

        spn = (Spinner) findViewById(R.id.country_code_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.CountryCodes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(adapter);
        spn.setSelection(getDefaultCountryCodePosition(spn));

        okBtn = (Button) findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String completePhoneNumber;
                String countryCode = (String) spn.getSelectedItem();
                String localNumber = et.getText().toString();
                completePhoneNumber = countryCode.split(",")[0] + localNumber;

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.PHONE_NUMBER, completePhoneNumber);
                editor.apply();

                setResult(RESULT_OK);
                registrationIntent();
//                progressDialog = ProgressDialog.show(InstallActivity.this, getResources().getString(R.string.progress_install_title), getResources().getString(R.string.progress_install_gcm));

                new CopyDictionary(getAssets(), InstallActivity.this).execute();


                finish();
            }
        });

        registrateGCM();
    }

    @Override
    protected void onStart() {
        super.onStart();
        et.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Preferences.REGISTRATION_COMPLETE));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    private void registrateGCM(){
        //gcm stuff
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(Preferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //progressDialog.dismiss();
                    finish();
                } else {
                    //progressDialog.dismiss();
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void registrationIntent(){
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            //if(!sharedPreferences.getBoolean(Preferences.SENT_TOKEN_TO_SERVER, false)){
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            //}
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

    private int getDefaultCountryCodePosition(Spinner countryCodes){
        ArrayAdapter adapter = (ArrayAdapter) countryCodes.getAdapter();
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        int spinnerPosition = 0;
        String countryID = manager.getSimCountryIso().toUpperCase().trim();
        String[] codes = getResources().getStringArray(R.array.CountryCodes);
        for(String c: codes){
            String [] codeParts = c.split(",");
            if(codeParts[1].trim().equals(countryID)){
                spinnerPosition = adapter.getPosition(c);
            }
        }
        return spinnerPosition;
    }
}
