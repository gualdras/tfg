package com.university.gualdras.tfgapp.presentation.chat;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.university.gualdras.tfgapp.R;
import com.university.gualdras.tfgapp.domain.LabeledImage;

import java.util.ArrayList;

/**
 * Created by gualdras on 13/04/16.
 */
public class KK extends Activity {

    private FragmentManager mFragmentManager;
    FilterImagesFragment mFilterImagesFragment = new FilterImagesFragment();

    static KKImagesFragment mKkImagesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.images_search);

        mKkImagesFragment = (KKImagesFragment) getFragmentManager().findFragmentById(R.id.image_fragment);

        // Get a reference to the FragmentManager
        mFragmentManager = getFragmentManager();

        // Start a new FragmentTransaction
        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();

        // Add the TitleFragment to the layout
        fragmentTransaction.add(R.id.img_search_fragment, mFilterImagesFragment);
        fragmentTransaction.addToBackStack(null);

        // Commit the FragmentTransaction
        fragmentTransaction.commit();
    }

    public static void addLabeledImage(ArrayList<LabeledImage> labeledImages){
        mKkImagesFragment.add(labeledImages);
    }
}