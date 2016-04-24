package com.university.gualdras.tfgapp.presentation.contactsTab;

/**
 * Created by gualdras on 19/09/15.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.university.gualdras.tfgapp.Constants;
import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.domain.network.RefreshContactsTask;
import com.university.gualdras.tfgapp.persistence.DataProvider;
import com.university.gualdras.tfgapp.presentation.chat.ChatActivity;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactTab extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    String TAG = "contactTab";
    private Context mContext;
    //private ContactListAdapter contactListAdapter;

    private SimpleCursorAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.contact_item,
                null,
                new String[]{DataProvider.COL_NAME, DataProvider.COL_PHOTO},
                new int[]{R.id.contact_name, R.id.contact_profile_photo},
                0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch(view.getId()) {
                    case R.id.contact_profile_photo:
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(cursor.getString(columnIndex)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        CircleImageView photo = (CircleImageView) view;
                        photo.setImageBitmap(bitmap);
                        return true;
                }
                return false;
            }
        });
        setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = container.getContext();
        setHasOptionsMenu(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.EXTRA_CONTACT_ID, String.valueOf(id));
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contacts_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {

            case R.id.refresh_menu_item:
                new RefreshContactsTask().execute(mContext);
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                DataProvider.CONTENT_URI_PROFILE,
                new String[]{DataProvider.COL_ID, DataProvider.COL_NAME, DataProvider.COL_PHOTO},
                null,
                null,
                DataProvider.COL_ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}