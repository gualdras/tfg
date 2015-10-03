package com.university.gualdras.tfgapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {
    final int[] ICONS_TABS = {R.drawable.ic_settings, R.drawable.ic_recent_chats, R.drawable.ic_contact};
    final int[] ICONS_SELECTED_TABS = {R.drawable.ic_settings_yellow, R.drawable.ic_recent_chats_yellow, R.drawable.ic_contacts_yellow};
    final int[] NAME_TABS = {R.string.settings_tab, R.string.recent_chats_tab, R.string.contacts_tab};
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
    }

    @Override
    protected void onStart(){
        super.onStart();
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
}