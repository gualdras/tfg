package com.university.gualdras.tfgapp.presentation.chat;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.domain.MessageItem;
import com.university.gualdras.tfgapp.persistence.DataProvider;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MessagesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat[] df = new DateFormat[]{
            DateFormat.getDateInstance(), DateFormat.getTimeInstance()};

    private OnFragmentInteractionListener mListener;
    private SimpleCursorAdapter adapter;
    private Date now;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        now = new Date();

        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.chat_message_item,
                null,
                new String[]{DataProvider.COL_MSG, DataProvider.COL_MSG, DataProvider.COL_AT, DataProvider.COL_TYPE},
                new int[]{R.id.msg, R.id.img, R.id.time},
                0);
        //Todo Try to do the date visualization automatically
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (view.getId()) {
                    case R.id.msg:
                        if (!cursor.getString(cursor.getColumnIndex(DataProvider.COL_TYPE)).equals(MessageItem.IMG_TYPE)) {
                            LinearLayout root = (LinearLayout) view.getParent();
                            if (cursor.getString(cursor.getColumnIndex(DataProvider.COL_FROM)) == null) {
                                root.setGravity(Gravity.RIGHT);
                                root.setPadding(50, 10, 10, 10);
                            } else {
                                root.setGravity(Gravity.LEFT);
                                root.setPadding(10, 10, 50, 10);
                            }
                        } else{
                            TextView tv = (TextView) view;
                            tv.setText("");
                            return true;
                        }
                        break;

                    case R.id.img:
                        if (cursor.getString(cursor.getColumnIndex(DataProvider.COL_TYPE)).equals(MessageItem.IMG_TYPE)) {
                            ImageView imageView = (ImageView) view;
                            DisplayMetrics metrics = new DisplayMetrics();
                            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            Bitmap bitmap = decodeSampledBitmapFromPath(cursor.getString(columnIndex), metrics.widthPixels, metrics.heightPixels);
                            imageView.setImageBitmap(bitmap);
                            return true;
                        }
                    /*case R.id.time:
                        TextView tv = (TextView) view;
                        tv.setText(getDisplayTime(cursor.getString(columnIndex)));
                        return true;*/
                }
                return false;
            }
        });

        setListAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setDivider(null);

        Bundle args = new Bundle();
        args.putString(DataProvider.COL_PHONE_NUMBER, mListener.getContactNumber());
        getLoaderManager().initLoader(0, args, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        String getContactNumber();
    }

    private String getDisplayTime(String datetime) {
        try {
            Date dt = sdf.parse(datetime);
            if (now.getYear() == dt.getYear() && now.getMonth() == dt.getMonth() && now.getDate() == dt.getDate()) {
                return df[1].format(dt);
            }
            return df[0].format(dt);
        } catch (ParseException e) {
            return datetime;
        }
    }

    //----------------------------------------------------------------------------

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String profileEmail = args.getString(DataProvider.COL_PHONE_NUMBER);
        CursorLoader loader = new CursorLoader(getActivity(),
                DataProvider.CONTENT_URI_MESSAGES,
                null,
                DataProvider.COL_FROM + " = ? or " + DataProvider.COL_TO + " = ?",
                new String[]{profileEmail, profileEmail},
                DataProvider.COL_AT + " DESC");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

// Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return bmp;
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

}
