package com.university.gualdras.tfgapp.presentation;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.R;


public class MainActivity extends AppCompatActivity {

    final int[] ICONS_TABS = {R.drawable.ic_settings, R.drawable.ic_contact, R.drawable.ic_recent_chats};
    final int[] ICONS_SELECTED_TABS = {R.drawable.ic_settings_yellow, R.drawable.ic_contacts_yellow, R.drawable.ic_recent_chats_yellow};
    final int[] NAME_TABS = {R.string.settings_tab, R.string.contacts_tab, R.string.recent_chats_tab};

    private static final String TAG = "MainActivity";

    SharedPreferences sharedPreferences;
    private static Context mContext;

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
        
        mContext = this;
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
        }
        else {
            if(requestCode == Constants.INSTALL_CODE && resultCode == RESULT_CANCELED){
                finish();
            }
        }
    }
}