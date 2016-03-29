package com.university.gualdras.tfgapp.presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.StartActivity;

/**
 * Created by gualdras on 28/02/16.
 */
public class InstallActivity extends AppCompatActivity{

    Spinner spn;
    EditText et;
    Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_install);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        et = (EditText) findViewById(R.id.phone_number_et);

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
                StartActivity.setPhoneNumber(completePhoneNumber);

                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        et.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
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
