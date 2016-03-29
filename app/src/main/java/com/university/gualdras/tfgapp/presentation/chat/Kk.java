package com.university.gualdras.tfgapp.presentation.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.university.gualdras.tfgapp.R;

/**
 * Created by gualdras on 29/03/16.
 */
public class Kk extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.kk);
        imageView = (ImageView) findViewById(R.id.kk_image);

        Intent intent = getIntent();
        Bitmap img = intent.getParcelableExtra("img");
        imageView.setImageBitmap(img);

    }
}
